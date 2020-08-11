package com.yingda.lkj.controller.backstage;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.backstage.line.RailwayLine;
import com.yingda.lkj.beans.entity.backstage.organization.Organization;
import com.yingda.lkj.beans.entity.system.Menu;
import com.yingda.lkj.beans.entity.system.Role;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.exception.CustomException;
import com.yingda.lkj.beans.pojo.utils.ExcelRowInfo;
import com.yingda.lkj.beans.pojo.utils.ExcelSheetInfo;
import com.yingda.lkj.beans.system.Json;
import com.yingda.lkj.beans.system.JsonMessage;
import com.yingda.lkj.controller.BaseController;
import com.yingda.lkj.service.backstage.organization.OrganizationClientService;
import com.yingda.lkj.service.base.BaseService;
import com.yingda.lkj.service.system.AuthService;
import com.yingda.lkj.service.system.MenuService;
import com.yingda.lkj.service.system.RoleService;
import com.yingda.lkj.service.system.UserService;
import com.yingda.lkj.utils.RequestUtil;
import com.yingda.lkj.utils.StreamUtil;
import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.date.DateUtil;
import com.yingda.lkj.utils.excel.ExcelUtil;
import com.yingda.lkj.utils.pojo.PojoUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.DuplicateMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理页
 *
 * @author hood  2019/12/27
 */
@Controller
@RequestMapping("/backstage/user")
public class UserController extends BaseController {

    @Autowired
    private BaseService<User> userBaseService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizationClientService organizationClientService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MenuService menuService;

    private User pageUser;

    @ModelAttribute
    public void setPageUser(User pageUser) {
        this.pageUser = pageUser;
    }

    @RequestMapping("")
    public ModelAndView userList() throws Exception {
        Map<String, Object> attributes = new HashMap<>();

        byte organizationPermission = getOrganizationPermission();
        String adminWorkshopId = getUser().getWorkshopId();
        String displayName = pageUser.getDisplayName();
        String workshopId = pageUser.getWorkshopId() + "";

        // 如果授权信息到车间，只能看本车间的数据
        if (organizationPermission == Role.WORKSHOP)
            workshopId = adminWorkshopId;
        String workAreaId = pageUser.getWorkAreaId() + "";


        attributes.put("organizationPermission", organizationPermission);
        attributes.put("workshopId", workshopId);
        attributes.put("workAreaId", workAreaId);

        Map<String, String> conditions = new HashMap<>(Map.of("userName", "not in"));
        Map<String, Object> params = new HashMap<>(Map.of("userName", List.of("admin")));

        String sectionId = getSectionId();
        List<Organization> workshops = isAdmin() ? organizationClientService.getWorkshops() : organizationClientService.getSlave(sectionId);
        if (organizationPermission == Role.WORKSHOP)
            workshops = List.of(organizationClientService.getById(adminWorkshopId));
        List<String> workshopIds = StreamUtil.getList(workshops, Organization::getId);
        List<Organization> workAreas = isAdmin() ? organizationClientService.getAllWorkAreas() : organizationClientService.getWorkAreas(workshopIds);

        attributes.put("workshops", workshops);
        attributes.put("workAreas", workAreas);

        if (!isAdmin()) {
            params.put("bureauId", getUser().getBureauId());
            conditions.put("bureauId", "=");
        }
        if (StringUtils.isNotEmpty(displayName)) {
            conditions.put("displayName", "like");
            params.put("displayName", "%" + displayName + "%");
        }
        if (StringUtils.isNotEmpty(workshopId)) {
            conditions.put("workshopId", "=");
            params.put("workshopId", workshopId);
        }
        if (StringUtils.isNotEmpty(workAreaId)) {
            conditions.put("workAreaId", "=");
            params.put("workAreaId", workAreaId);
        }
        if (!isAdmin()) {
            conditions.put("sectionId", "=");
            params.put("sectionId", sectionId);
        }

        List<User> users = userBaseService.getObjcetPagination(User.class, params, conditions, page.getCurrentPage(), page.getPageSize(), "order by addTime desc");

        for (User user : users) {
            String userSectionId = user.getSectionId();
            String userWorkshopId = user.getWorkshopId();
            String userWorkAreaId = user.getWorkAreaId();

            Organization section = organizationClientService.getById(userSectionId);
            Organization workshop = organizationClientService.getById(userWorkshopId);
            Organization workArea = organizationClientService.getById(userWorkAreaId);
            Role role = roleService.getRole(user.getRoleId());

            user.setSectionName(section.getName());
            user.setWorkshopName(workshop.getName());
            user.setWorkAreaName(workArea.getName());
            user.setRoleName(role.getRole());
        }

        // 查询总数
        page.setPageTotal(userBaseService.getObjectNum(User.class, params, conditions));

        attributes.put("users", users);
        attributes.put("page", page);
        attributes.put("params", pageUser);

        return new ModelAndView("/backstage/user/user", attributes);
    }

    @RequestMapping("/infoPage")
    public ModelAndView infoPage(String id) throws Exception {
        User user = userBaseService.get(User.class, id);
        // organization，role都是内存方法
        Organization userBureau = organizationClientService.getById(user.getBureauId());
        Organization userSection = organizationClientService.getById(user.getSectionId());
        Organization userWorkshop = organizationClientService.getById(user.getWorkshopId());
        Organization userWorkArea = organizationClientService.getById(user.getWorkAreaId());
        Role userRole = roleService.getRole(user.getRoleId());

        // 所有角色，除了管理员
        List<Role> roles = roleService.showDown().stream().filter(x -> !Role.ADMIN.equals(x.getRole())).collect(Collectors.toList());
        // 备选局
        List<Organization> bereaus = organizationClientService.getBureaus();
        // 备选站段
        List<Organization> sections = organizationClientService.getSlave(userBureau.getId());
        // 备选车间
        List<Organization> workshops = organizationClientService.getSlave(userSection.getId());
        // 备选工区
        List<Organization> workareas = organizationClientService.getSlave(userWorkshop.getId());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user", user);
        attributes.put("userBureau", userBureau);
        attributes.put("userSection", userSection);
        attributes.put("userWorkshop", userWorkshop);
        attributes.put("userWorkArea", userWorkArea);
        attributes.put("userRole", userRole);
        attributes.put("roles", roles);
        attributes.put("bureaus", bereaus);
        attributes.put("sections", sections);
        attributes.put("workshops", workshops);
        attributes.put("workareas", workareas);

        return new ModelAndView("/backstage/user/user-info", attributes);
    }


    @RequestMapping("/addPage")
    public ModelAndView addPage() throws CustomException {
        User user = getUser();
        // 所有角色，除了管理员
        List<Role> roles = roleService.showDown().stream().filter(x -> !Role.ADMIN.equals(x.getRole())).collect(Collectors.toList());

        // 是管理员
        Role role = getRole();
        if (Role.ADMIN.equals(role.getRole())) {
            List<Organization> bureaus = organizationClientService.getBureaus();
            return new ModelAndView("/backstage/user/user-add", Map.of("roles", roles, "bureaus", bureaus));
        }
        if (!Role.ADMIN.equals(role.getRole())) {
            // 所在局
            Organization bureau = organizationClientService.getById(user.getBureauId());
            return new ModelAndView("/backstage/user/user-add", Map.of("roles", roles, "bureaus", List.of(bureau)));
        }
        throw new CustomException(new Json(JsonMessage.SYS_ERROR));
    }

    @PostMapping("/saveOrUpdate")
    @ResponseBody
    public Json saveOrUpdate() throws Exception {
        Json json = PojoUtils.checkParams(pageUser, User.REQUIRED_MAP);
        if (!json.isSuccess())
            return json;

        if (StringUtils.isEmpty(pageUser.getId()))
            pageUser = new User(pageUser);

        pageUser.setUpdateTime(current());
        userBaseService.saveOrUpdate(pageUser);

        return new Json(JsonMessage.SUCCESS);
    }

    @RequestMapping("/passwordPage")
    public ModelAndView passwordPage() {
        return new ModelAndView("/backstage/user/user-password");
    }

    @RequestMapping("/updatePassword")
    @ResponseBody
    public Json updatePassword() {
//        String oldPassword = req.getParameter("oldPassword");
//        String newPassword = req.getParameter("newPassword");
//        String repeatPassword = req.getParameter("repeatPassword");
//
//        User user = RequestUtil.getUser(req);
//        if (!StringUtils.isNotEmpty(oldPassword, newPassword, repeatPassword))
//
//        if (user.getPassword())
//        if (oldPassword.equals(user.getPassword()) && )

        return null;
    }

    //导出用户信息
    @RequestMapping("/exportUsers")
    @ResponseBody
    public void exportUsers() throws Exception {
        String displayName = pageUser.getDisplayName();
        String workshopId = pageUser.getWorkshopId() + "";
        String workAreaId = pageUser.getWorkAreaId() + "";

        Map<String, String> conditions = new HashMap<>(Map.of("userName", "!=", "sectionId", "="));
        Map<String, Object> params = new HashMap<>(Map.of("userName", "admin", "sectionId", getSectionId()));

        if (StringUtils.isNotEmpty(displayName)) {
            conditions.put("displayName", "like");
            params.put("displayName", "%" + displayName + "%");
        }
        if (StringUtils.isNotEmpty(workshopId)) {
            conditions.put("workshopId", "=");
            params.put("workshopId", workshopId);
        }
        if (StringUtils.isNotEmpty(workAreaId)) {
            conditions.put("workAreaId", "=");
            params.put("workAreaId", workAreaId);
        }

        List<User> users = userBaseService.getObjects(User.class, params, conditions, "order by addTime desc");

        for (User user : users) {
            String userSectionId = user.getSectionId();
            String userWorkshopId = user.getWorkshopId();
            String userWorkAreaId = user.getWorkAreaId();

            Organization section = organizationClientService.getById(userSectionId);
            Organization workshop = organizationClientService.getById(userWorkshopId);
            Organization workArea = organizationClientService.getById(userWorkAreaId);

            user.setSectionName(section.getName());
            user.setWorkshopName(workshop.getName());
            user.setWorkAreaName(workArea.getName());
        }

        Map<Integer, ExcelRowInfo> rowInfoMap = new HashMap<>();
        int rows = 0;
        rowInfoMap.put(rows++, new ExcelRowInfo(rows++, "用户名", "显示姓名", "电务段", "车间", "工区", "添加时间"));
        for (User user : users) {
            String userName = StringUtils.isEmpty(user.getUserName()) ? "-" : user.getUserName();
            String displayname = StringUtils.isEmpty(user.getDisplayName()) ? "-" : user.getDisplayName();
            String sectionName = StringUtils.isEmpty(user.getSectionName()) ? "-" : user.getSectionName();
            String workshopName = StringUtils.isEmpty(user.getWorkshopName()) ? "-" : user.getWorkshopName();
            String workAreaName = StringUtils.isEmpty(user.getWorkAreaName()) ? "-" : user.getWorkAreaName();
            String addTime;
            if (user.getAddTime() == null) {
                addTime = "-";
            } else {
                addTime = DateUtil.format(user.getAddTime(), "yyyy-mm-dd");
            }

            rowInfoMap.put(rows++, new ExcelRowInfo(rows++, userName, displayname, sectionName, workshopName, workAreaName, addTime));
        }

        ExcelSheetInfo excelSheetInfo = new ExcelSheetInfo("用户信息", rowInfoMap);

        Workbook workbook = ExcelUtil.createExcelFile(List.of(excelSheetInfo));
        MultipartFile multipartFile = ExcelUtil.workbook2File(workbook, "用户信息");
        export(multipartFile);
    }

    /**
     * 导入用户
     */
    @RequestMapping("/importUsers")
    @ResponseBody
    public Json importLines(MultipartFile file) throws Exception {
        User submitter = getUser();

        List<ExcelSheetInfo> excelSheetInfos = ExcelUtil.readExcelFile(file);
        if (excelSheetInfos.size() > 1)
            throw new CustomException(new Json(JsonMessage.PARAM_INVALID, "用户excel导入时只能包含一页"));

        Map<Integer, ExcelRowInfo> rowInfoMap = excelSheetInfos.get(0).getRowInfoMap();

        String sectionId = getSectionId();
        List<Organization> workshops = organizationClientService.getSlave(sectionId);

        List<User> users = new ArrayList<>();
        // 数据从第二行开始
        for (int i = 1; i < rowInfoMap.size(); i++) {
            ExcelRowInfo excelRowInfo = rowInfoMap.get(i);
            List<String> cells = excelRowInfo.getCells();

            String userName = cells.get(0).trim();
            if (StringUtils.isEmpty(userName))
                break;
            String displayName = cells.get(1).trim();
            String workshopName = cells.get(2).trim();
            String workAreaName = cells.get(3).trim();
            String roleName = cells.get(4).trim();

            Organization workshop = workshops.stream().filter(x -> workshopName.equals(x.getName())).reduce(null, (x, y) -> y);
            if (workshop == null)
                throw new CustomException(new Json(JsonMessage.PARAM_INVALID, String.format("第%d行出现错误: 找不到名称为%s的车间", i, workshopName)));

            List<Organization> workAreas = organizationClientService.getSlave(workshop.getId());
            Organization workArea = workAreas.stream().filter(x -> workAreaName.equals(x.getName())).reduce(null, (x, y) -> y);
            if (workArea == null)
                throw new CustomException(new Json(JsonMessage.PARAM_INVALID, String.format("第%d行出现错误: 找不到名称为%s的工区", i, workAreaName)));

            Role role = roleService.getRoleByName(roleName);
            if (role == null)
                throw new CustomException(new Json(JsonMessage.PARAM_INVALID, String.format("第%d行出现错误: 找不到名称为%s的角色", i, roleName)));

            // 重名用户替换
            User duplicateNameUser = userBaseService.get(
                    "from User where userName = :userName",
                    Map.of("userName", userName)
            );
            User user = duplicateNameUser == null ? new User() : duplicateNameUser;
            if (duplicateNameUser == null) {
                user.setId(UUID.randomUUID().toString());
                user.setAddTime(current());
            }
            user.setUserName(userName);
            user.setRoleId(role.getId());
            user.setBureauId(submitter.getBureauId());
            user.setSectionId(submitter.getSectionId());
            user.setWorkshopId(workshop.getId());
            user.setWorkAreaId(workArea.getId());
            user.setDisplayName(displayName);
            user.setPassword("123456");
            user.setBanned(User.NOT_BANNED);
            user.setUpdateTime(current());
            users.add(user);
        }

        userBaseService.bulkInsert(users);
        return new Json(JsonMessage.SUCCESS, "查询到成功导入" + users.size() + "条数据");
    }

    @RequestMapping("/getVueAuthorizedMenus")
    @ResponseBody
    public Json getVueAuthorizedMenus() throws CustomException {
        User user = RequestUtil.getUser(req);
        if (user == null)
            throw new CustomException(new Json(JsonMessage.PARAM_INVALID, "用户尚未登录"));

        List<Menu> availableMenus = authService.getVueValuableMenus(user);
        availableMenus = menuService.jsonified(availableMenus);
        return new Json(JsonMessage.SUCCESS, availableMenus);
    }

}
