package com.yingda.lkj.beans.enums.dataversion;

import com.yingda.lkj.beans.entity.backstage.dataversion.DataApproveFlow;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.utils.SpringContextUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/5/26
 */
public enum ApproveDataType {
    LKJ14("14", "LKJ-14表", User.class,
            "forward:/backstage/lkjVersion",
            "forward:/backstage/lkjApproveSubmit/importPage",
            "forward:/backstage/lkjApproveUpdate/lkjApproveFlowDetail") {
        @Override
        public void createDataLines(DataApproveFlow dataApproveFlow, List<?> rawDataLines) {
            User lkjDataLineService = (User) SpringContextUtil.getBean("lkjDataLineService");
            List<User> rawLkjDataLines = new ArrayList<>();
            for (Object rawDataLine : rawDataLines)
                rawLkjDataLines.add((User) rawDataLine);
//            lkjDataLineService.createLkjDataLine(dataApproveFlow, rawLkjDataLines);
        }

        @Override
        public void completeDataLines(DataApproveFlow dataApproveFlow) {
//            LkjDataLineService lkjDataLineService = (LkjDataLineService) SpringContextUtil.getBean("lkjDataLineService");
//            lkjDataLineService.completeLkjDataLine(dataApproveFlow);
        }

        @Override
        public void refuseDataLines(DataApproveFlow dataApproveFlow) {
//            LkjDataLineService lkjDataLineService = (LkjDataLineService) SpringContextUtil.getBean("lkjDataLineService");
//            lkjDataLineService.refuseLkjDataLines(dataApproveFlow);
        }
    }
    ;

    public static final String INFO_PAGE_ROUTING_URL = "/backstage/approveData/infoPageRouting";
    public static final String VERSION_PAGE_ROUTING_URL = "/backstage/approveData/versionRouting";

    private String dataTypeId;
    private String dataTypeName;
    private Class<?> dataClass;
    private String versionRouting; // 版本管理页
    private String infoPageRouting; // 信息详情页
    private String approveFlowInfoRouting; // 审批详情页

    ApproveDataType(String dataTypeId, String dataTypeName,
                    Class<?> dataClass,
                    String versionRouting, String infoPageRouting, String approveFlowInfoRouting) {
        this.dataTypeId = dataTypeId;
        this.dataTypeName = dataTypeName;
        this.dataClass = dataClass;
        this.versionRouting = versionRouting;
        this.infoPageRouting = infoPageRouting;
        this.approveFlowInfoRouting = approveFlowInfoRouting;
    }

    /**
     * 生成审批数据
     */
    public abstract void createDataLines(DataApproveFlow dataApproveFlow, List<?> rawDataLines);

    /**
     * 提交审批流下的数据为已完成
     */
    public abstract void completeDataLines(DataApproveFlow dataApproveFlow);

    /**
     * 拒绝审批数据
     */
    public abstract void refuseDataLines(DataApproveFlow dataApproveFlow);

    public static ApproveDataType getById(String dataTypeId) {
        return Arrays.stream(values()).filter(x -> dataTypeId.equals(x.getDataTypeId())).reduce(null, (x, y) -> y);
    }

    public static ApproveDataType getByName(String dataTypeName) {
        return Arrays.stream(values()).filter(x -> dataTypeName.equals(x.getDataTypeName())).reduce(null, (x, y) -> y);
    }

    public static void setComponentsAttributes(HttpServletRequest req, Map<String, Object> attributes, String routingUrl) {
        String dataTypeId = req.getParameter("dataTypeId");

        req.setAttribute("dataTypeId", dataTypeId);
        req.setAttribute("routingUrl", routingUrl);
        req.setAttribute("approveDataTypes", ApproveDataType.values());
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public Class<?> getDataClass() {
        return dataClass;
    }

    public String getApproveFlowInfoRouting() {
        return approveFlowInfoRouting;
    }

    public String getVersionRouting() {
        return versionRouting;
    }

    public String getInfoPageRouting() {
        return infoPageRouting;
    }
}
