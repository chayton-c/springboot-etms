package com.yingda.lkj.beans.entity.backstage.dataversion;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * 审批权限配置表
 *
 * @author hood  2020/5/26
 */
@Entity
@Table(name = "data_approve_config", schema = "illustrious", catalog = "")
public class DataApproveConfig {
    private String id;
    private String sectionId;
    private String dataTypeId; // 数据类型id
    private String leftNodeRoleId; // 提交人的角色
    private String rightNodeRoleId; // 审核人的角色

    public DataApproveConfig() {
    }

    public DataApproveConfig(String sectionId, String leftNodeRoleId, String rightNodeRoleId) {
        this.id = UUID.randomUUID().toString();
        this.sectionId = sectionId;
        this.dataTypeId = "审批权限暂时不区分数据类型";
        this.leftNodeRoleId = leftNodeRoleId;
        this.rightNodeRoleId = rightNodeRoleId;
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
    @Column(name = "section_id", nullable = false, length = 36)
    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    @Basic
    @Column(name = "left_node_role_id", nullable = false, length = 36)
    public String getLeftNodeRoleId() {
        return leftNodeRoleId;
    }

    public void setLeftNodeRoleId(String leftNodeRoleId) {
        this.leftNodeRoleId = leftNodeRoleId;
    }

    @Basic
    @Column(name = "right_node_role_id", nullable = false, length = 36)
    public String getRightNodeRoleId() {
        return rightNodeRoleId;
    }

    public void setRightNodeRoleId(String rightNodeRoleId) {
        this.rightNodeRoleId = rightNodeRoleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataApproveConfig that = (DataApproveConfig) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sectionId, that.sectionId) &&
                Objects.equals(leftNodeRoleId, that.leftNodeRoleId) &&
                Objects.equals(rightNodeRoleId, that.rightNodeRoleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sectionId, leftNodeRoleId, rightNodeRoleId);
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
