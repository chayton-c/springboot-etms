package com.yingda.lkj.beans.entity.backstage.dataversion;

import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.date.DateUtil;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * 数据版本
 *
 * @author hood  2020/5/26
 */
@Entity
@Table(name = "data_version", schema = "illustrious", catalog = "")
public class DataVersion {
    // type字段
    public static final byte INTERIM_VERSION = 0; // 临时版本(每次修改lkj时都会生成新的临时版本)
    public static final byte STABLE_VERSION = 1; // 稳定版本(管理员手动生成的)

    private String id;
    private String name;
    private String sectionId; // 站段id
    private String dataTypeId; // 数据类型id
    private double versionNumber; // 版本号
    private byte type;
    private Timestamp addTime;
    private Timestamp updateTime;

    public DataVersion() {
    }

    public DataVersion(String sectionId, String dataTypeId, double versionNumber) {
        Timestamp current = new Timestamp(System.currentTimeMillis());
        String currentDateStr = DateUtil.format(current, "yyyy-MM-dd HH:mm:ss");
        this.id = UUID.randomUUID().toString();
        this.sectionId = sectionId;

        // 输入名称的为正式版本
        this.name = String.format("%s临时版本", currentDateStr);
        this.type = INTERIM_VERSION;

        this.dataTypeId = dataTypeId;
        this.versionNumber = versionNumber;
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
    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "version_number", nullable = false, precision = 0)
    public double getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(double versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Basic
    @Column(name = "add_time", nullable = true)
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataVersion that = (DataVersion) o;
        return Double.compare(that.versionNumber, versionNumber) == 0 &&
                type == that.type &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(addTime, that.addTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, versionNumber, type, addTime);
    }

    @Basic
    @Column(name = "data_type_id", nullable = false, length = 36)
    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
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
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
