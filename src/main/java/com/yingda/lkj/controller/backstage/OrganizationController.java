package com.yingda.lkj.controller.backstage;

import com.yingda.lkj.beans.entity.backstage.dataversion.DataApproveConfig;
import com.yingda.lkj.beans.entity.backstage.organization.Organization;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.service.backstage.dataapprove.DataApproveConfigService;
import com.yingda.lkj.service.backstage.organization.OrganizationClientService;
import com.yingda.lkj.service.backstage.organization.OrganizationService;
import com.yingda.lkj.utils.RequestUtil;
import com.yingda.lkj.utils.StreamUtil;
import com.yingda.lkj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.*;

/**
 * <p>人员组织页(局，站段，车间，工区管理)</p>
 * <p>除了组织管理页，其他页面的controller不要调用这里的方法</p>
 *
 * @author hood  2019/12/26
 */
@Controller
@RequestMapping("/backstage/organization")
public class OrganizationController extends BaseController {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private OrganizationClientService organizationClientService;
    @Autowired
    private DataApproveConfigService dataApproveConfigService;

    private Organization organization;

    @ModelAttribute
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * 组织列表
     */
    @RequestMapping("")
    public ModelAndView getAll() {
        User user = RequestUtil.getUser(req);

        List<Organization> completeTree; // 站段及以下
        if ("admin".equals(user.getUserName()))
            completeTree = organizationService.getCompleteTree();
        else
            completeTree = organizationService.getCompleteTree(user.getSectionId());

        List<Organization> expanded = organizationService.expand(completeTree, new ArrayList<>());
        for (Organization organization : expanded) {
            List<Organization> slaves = organization.getOrganizationList();
            organization.setHasSlave(slaves != null && slaves.size() > 0);
            organization.setOrganizationList(null);
        }

        expanded.forEach(x -> x.setOrganizationList(null)); // 下级没用，放进去包就太大了
        return new ModelAndView("/backstage/organization/organization", Map.of("user", user, "organizations", expanded, "leafNodeLevel",
                Organization.LEAF_NODE_LEVEL));
    }

    @RequestMapping("/updateSeq")
    @ResponseBody
    public Json updateSeq(String id, String seq) {
        Organization organization = organizationService.getById(id);
        organization.setSeq(Integer.parseInt(seq));
        organization.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        organizationService.saveOrUpdate(organization);

        return new Json(JsonMessage.SUCCESS);
    }

    @RequestMapping("/saveOrUpdate")
    @ResponseBody
    public Json saveOrUpdate(String lkjApproveConfig) throws CustomException {
        Timestamp current = new Timestamp(System.currentTimeMillis());

        if (StringUtils.isEmpty(organization.getId())) {
            // 校验是否存在同一上级下同名的部门
            String parentId = organization.getParentId();
            String name = organization.getName();

            List<Organization> slaves = organizationClientService.getSlave(parentId);
            if (StreamUtil.getList(slaves, Organization::getName).contains(name))
                throw new CustomException(new Json(JsonMessage.PARAM_INVALID, String.format("已存在名称为%s的部门", name)));

            organization.setId(UUID.randomUUID().toString());
            organization.setAddTime(current);
        }

        organization.setUpdateTime(current);
        organizationService.saveOrUpdate(organization);

        // 如果上传了审批流程数据，保存
        if (StringUtils.isNotEmpty(lkjApproveConfig))
            dataApproveConfigService.saveDataApproveList(dataApproveConfigService.parse(lkjApproveConfig, organization.getId()));

        return new Json(JsonMessage.SUCCESS);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Json delete(String ids) {
        // asList出来的list不能addAll，转成ArrayList才能
        organizationService.delete(new ArrayList<>(Arrays.asList(ids.split(","))));

        return new Json(JsonMessage.SUCCESS);
    }

    /**
     * 菜单详情页
     */
    @RequestMapping("/info")
    public ModelAndView info(String id, String parentId) {
        Organization organization = organizationService.getById(id);

        // 添加的时候(id为空)，parentId为organization的上级id，会把这个值放到详情页的表单input中，添加时作为parentId(如果为空，那么填写 Organization.ROOT_ID)
        if (StringUtils.isEmpty(id)) {
            if (parentId == null) {
                organization.setLevel(Organization.BUREAU);
                organization.setParentId(Organization.ROOT_ID);
            } else {
                // 父节点的等级 + 1
                organization.setLevel((byte) (organizationService.getById(parentId).getLevel() + 1));
                organization.setParentId(parentId);
            }
        }

        // 默认编号
        if (organization.getLevel() != Organization.BUREAU && organization.getCode() == null)
            organization.setCode("ff");

        if (organization.getLevel() == Organization.SECTION) {
            List<DataApproveConfig> dataApproveList = dataApproveConfigService.getDataApproveConfigList(Optional.ofNullable(id).orElse(""));
            return new ModelAndView(
                    "/backstage/organization/organization-info",
                    Map.of("organization", organization, "lkjApproveConfig", dataApproveConfigService.format(dataApproveList))
            );
        }


        return new ModelAndView("/backstage/organization/organization-info", Map.of("organization", organization));
    }
}
