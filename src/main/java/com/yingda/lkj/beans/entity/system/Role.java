package com.yingda.lkj.beans.entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author hood  2019/12/16
 */
@Entity
@Table(name = "role")
public class Role implements Serializable {
    // 对应role字段 表示这个人是管理员
    public static final String ADMIN = "admin";

    // organizationPermission字段
    public static final byte WORKSHOP = 0;
    public static final byte SECTION = 1;

    private String id;
    private String role; // 角色名
    private String permission; // 许可，暂时用不上
    private byte organizationPermission; // 组织权限
    private Timestamp addTime;
    private Timestamp updateTime;
    private byte hide;
    private String description;

    @Id
    @Column(name = "id", nullable = false, length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "role", nullable = false, length = 10)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "permission", nullable = true, length = 10)
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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

    @Basic
    @Column(name = "hide", nullable = false)
    public byte getHide() {
        return hide;
    }

    public void setHide(byte hide) {
        this.hide = hide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role1 = (Role) o;
        return hide == role1.hide &&
                Objects.equals(id, role1.id) &&
                Objects.equals(role, role1.role) &&
                Objects.equals(permission, role1.permission) &&
                Objects.equals(addTime, role1.addTime) &&
                Objects.equals(updateTime, role1.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, permission, hide, addTime, updateTime);
    }

    @Basic
    @Column(name = "description", nullable = false, length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "organization_permission", nullable = false)
    public byte getOrganizationPermission() {
        return organizationPermission;
    }

    public void setOrganizationPermission(byte organizationPermission) {
        this.organizationPermission = organizationPermission;
    }
}
