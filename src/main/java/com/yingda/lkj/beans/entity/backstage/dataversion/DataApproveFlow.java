package com.yingda.lkj.beans.entity.backstage.dataversion;

import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.enums.dataversion.ApproveDataType;
import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.date.DateUtil;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * 数据审批流
 *
 * @author hood  2020/5/26
 */
@Entity
@Table(name = "data_approve_flow", schema = "illustrious", catalog = "")
public class DataApproveFlow {

    // approveStatus字段
    public static final byte PENDING_REVIEW = 0; // 待审核的
    public static final byte APPROVED = 1; // 通过
    public static final byte FAILED = 2; // 未通过

    private String id;
    private String flowNo; // 流水号，唯一
    private String dataTypeId; // 数据类型id
    private String submitUserId; // 发起人
    private String sectionId; // 所在区间
    private String name;
    private String telegraph; // 电报图片地址
    private byte approveStatus; // 审批状态
    private String currentReviewerId; // 当前审批人
    private String remark;
    private Timestamp addTime;
    private Timestamp updateTime;

    // page fileds
    private String submitUserName;
    private String submitUserWorkAreaName;
    private DataApproveNode dataApproveNode; // 当前审批人的审批节点

    public DataApproveFlow() {
    }

    public DataApproveFlow(DataApproveFlow rawLkjApproveFlow, User submitter, String currentReviewer, ApproveDataType approveDataType) {
        Timestamp current = new Timestamp(System.currentTimeMillis());

        this.id = UUID.randomUUID().toString();
        this.flowNo = this.id;
        this.dataTypeId = approveDataType.getDataTypeId();
        this.submitUserId = submitter.getId();
        this.currentReviewerId = currentReviewer;
        this.sectionId = submitter.getSectionId();

        if (StringUtils.isEmpty(rawLkjApproveFlow.getName())) // 如无name，自动生成
            rawLkjApproveFlow.setName(submitter.getUserName() + "测量审批 " + DateUtil.format(current, "yyyy-MM-dd"));

        this.name = rawLkjApproveFlow.getName();
        this.telegraph = rawLkjApproveFlow.getTelegraph();
        this.approveStatus = PENDING_REVIEW;
        this.remark = rawLkjApproveFlow.getRemark();

        this.addTime = current;
        this.updateTime = current;
    }

    @Id
    @Column(name = "id", nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "flow_no", nullable = false, length = 36)
    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    @Basic
    @Column(name = "submit_user_id", nullable = false, length = 36)
    public String getSubmitUserId() {
        return submitUserId;
    }

    public void setSubmitUserId(String submitUserId) {
        this.submitUserId = submitUserId;
    }

    @Basic
    @Column(name = "current_reviewer_id", nullable = false, length = 36)
    public String getCurrentReviewerId() {
        return currentReviewerId;
    }

    public void setCurrentReviewerId(String currentReviewerId) {
        this.currentReviewerId = currentReviewerId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "telegraph", nullable = true, length = 255)
    public String getTelegraph() {
        return telegraph;
    }

    public void setTelegraph(String telegraph) {
        this.telegraph = telegraph;
    }

    @Basic
    @Column(name = "approveStatus", nullable = false)
    public byte getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(byte approveStatus) {
        this.approveStatus = approveStatus;
    }

    @Basic
    @Column(name = "remark", nullable = true, length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "add_time", nullable = true)
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    @Basic
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataApproveFlow that = (DataApproveFlow) o;
        return approveStatus == that.approveStatus &&
                Objects.equals(id, that.id) &&
                Objects.equals(flowNo, that.flowNo) &&
                Objects.equals(submitUserId, that.submitUserId) &&
                Objects.equals(currentReviewerId, that.currentReviewerId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(telegraph, that.telegraph) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(addTime, that.addTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, flowNo, submitUserId, currentReviewerId, name, telegraph, approveStatus, remark, addTime, updateTime);
    }

    @Basic
    @Column(name = "data_type_id", nullable = false, length = 36)
    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    @Transient
    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    @Transient
    public String getSubmitUserWorkAreaName() {
        return submitUserWorkAreaName;
    }

    public void setSubmitUserWorkAreaName(String submitUserWorkAreaName) {
        this.submitUserWorkAreaName = submitUserWorkAreaName;
    }

    @Basic
    @Column(name = "section_id", nullable = false, length = 36)
    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    @Transient
    public DataApproveNode getDataApproveNode() {
        return dataApproveNode;
    }

    public void setDataApproveNode(DataApproveNode dataApproveNode) {
        this.dataApproveNode = dataApproveNode;
    }
}
