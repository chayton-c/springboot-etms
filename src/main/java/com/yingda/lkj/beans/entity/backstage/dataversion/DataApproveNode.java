package com.yingda.lkj.beans.entity.backstage.dataversion;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * 数据审批节点
 *
 * @author hood  2020/5/26
 */
@Entity
@Table(name = "data_approve_node", schema = "illustrious", catalog = "")
public class DataApproveNode {
    // userApproveStatus字段
    public static final byte PENDING_REVIEW = 0; // 待审核的
    public static final byte APPROVED = 1; // 通过
    public static final byte FAILED = 2; // 未通过

    private String id;

    private String dataApproveFlowId; // 审批流id
    private String dataTypeId; // 数据类型id
    private String previousNodeId; // 上一级审批节点id(初始节点，该字段为空)
    private String approveOpinion; // 审核意见
    private String approveUserId; // 节点通过人(初始节点的节点通过人为提交人)

    // 用户审核状态，不同于 DataApproveFlow.approveStatus 这个指审批流在当前节点的审批状态
    // 即可能出现审核记录为未通过，但是当前节点通过但是下一级节点未通过的情况
    private byte userApproveStatus;
    private Timestamp addTime;
    private Timestamp updateTime;

    public DataApproveNode() {
    }

    public DataApproveNode(DataApproveFlow dataApproveFlow, String approveUserId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        this.id = UUID.randomUUID().toString();
        this.dataTypeId = dataApproveFlow.getDataTypeId();
        this.dataApproveFlowId = dataApproveFlow.getId();
        this.previousNodeId = null;
        this.approveOpinion = null;
        this.approveUserId = approveUserId;
        this.userApproveStatus = PENDING_REVIEW;
        this.addTime = timestamp;
        this.updateTime = timestamp;
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
    @Column(name = "data_approve_flow_id", nullable = false, length = 36)
    public String getDataApproveFlowId() {
        return dataApproveFlowId;
    }

    public void setDataApproveFlowId(String dataApproveFlowId) {
        this.dataApproveFlowId = dataApproveFlowId;
    }

    @Basic
    @Column(name = "previous_node_id", nullable = true, length = 36)
    public String getPreviousNodeId() {
        return previousNodeId;
    }

    public void setPreviousNodeId(String previousNodeId) {
        this.previousNodeId = previousNodeId;
    }

    @Basic
    @Column(name = "approve_opinion", nullable = true, length = 255)
    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    @Basic
    @Column(name = "approve_user_id", nullable = false, length = 36)
    public String getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(String approveUserId) {
        this.approveUserId = approveUserId;
    }

    @Basic
    @Column(name = "user_approve_status", nullable = false)
    public byte getUserApproveStatus() {
        return userApproveStatus;
    }

    public void setUserApproveStatus(byte userApproveStatus) {
        this.userApproveStatus = userApproveStatus;
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
        DataApproveNode that = (DataApproveNode) o;
        return userApproveStatus == that.userApproveStatus &&
                Objects.equals(id, that.id) &&
                Objects.equals(dataApproveFlowId, that.dataApproveFlowId) &&
                Objects.equals(previousNodeId, that.previousNodeId) &&
                Objects.equals(approveOpinion, that.approveOpinion) &&
                Objects.equals(approveUserId, that.approveUserId) &&
                Objects.equals(addTime, that.addTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataApproveFlowId, previousNodeId, approveOpinion, approveUserId, userApproveStatus, addTime, updateTime);
    }

    @Basic
    @Column(name = "data_type_id", nullable = false, length = 36)
    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }
}
