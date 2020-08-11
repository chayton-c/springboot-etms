package com.yingda.lkj.beans.entity.backstage.dataversion;

import com.yingda.lkj.beans.enums.dataversion.ApproveDataType;
import com.yingda.lkj.beans.pojo.approvedata.VersionData;
import com.yingda.lkj.utils.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * 数据版本修改记录表
 *
 * @author hood  2020/5/26
 */
@Entity
@Table(name = "data_version_update_detail", schema = "illustrious", catalog = "")
public class DataVersionUpdateDetail {
    // operationType字段
    public static final byte UPDATE = 0; // 修改
    public static final byte ADD = 1; // 新增
    public static final byte DELETE = 2; // 删除

    private String id;
    private byte operationType; // 操作类型
    private String sectionId;
    private String dataVersionId; // 版本id
    private String dataVersionName; // 版本名称
    private double dataVersionNumber; // 版本号
    private String dataTypeId; // 数据类型id
    private String previousDataId; // 修改前的数据id
    private String currentDataId; // 修改后的数据id
    private Timestamp addTime;

    public DataVersionUpdateDetail() {
    }

    public DataVersionUpdateDetail(DataVersion dataVersion, VersionData currentVersionData, VersionData previousVersionData) {
        this.id = UUID.randomUUID().toString();
        this.addTime = new Timestamp(System.currentTimeMillis());

        this.dataVersionId = dataVersion.getId();
        this.dataVersionNumber = dataVersion.getVersionNumber();
        this.dataVersionName = dataVersion.getName();
        this.dataTypeId = dataVersion.getDataTypeId();
        this.sectionId = dataVersion.getSectionId();

        if (currentVersionData == null && previousVersionData != null) {
            this.operationType = DELETE;
            this.previousDataId = previousVersionData.getId();
        }
        if (currentVersionData != null && previousVersionData == null) {
            this.operationType = ADD;
            this.currentDataId = currentVersionData.getId();
        }
        if (currentVersionData != null && previousVersionData != null) {
            this.operationType = UPDATE;
            this.currentDataId = currentVersionData.getId();
            this.previousDataId = previousVersionData.getId();
        }
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
    @Column(name = "data_version_id", nullable = false, length = 36)
    public String getDataVersionId() {
        return dataVersionId;
    }

    public void setDataVersionId(String dataVersionId) {
        this.dataVersionId = dataVersionId;
    }

    @Basic
    @Column(name = "data_version_number", nullable = false, precision = 0)
    public double getDataVersionNumber() {
        return dataVersionNumber;
    }

    public void setDataVersionNumber(double dataVersionNumber) {
        this.dataVersionNumber = dataVersionNumber;
    }

    @Basic
    @Column(name = "previous_data_id", nullable = true, length = 36)
    public String getPreviousDataId() {
        return previousDataId;
    }

    public void setPreviousDataId(String previousDataId) {
        this.previousDataId = previousDataId;
    }

    @Basic
    @Column(name = "current_data_id", nullable = true, length = 36)
    public String getCurrentDataId() {
        return currentDataId;
    }

    public void setCurrentDataId(String currentDataId) {
        this.currentDataId = currentDataId;
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
        DataVersionUpdateDetail that = (DataVersionUpdateDetail) o;
        return Double.compare(that.dataVersionNumber, dataVersionNumber) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(dataVersionId, that.dataVersionId) &&
                Objects.equals(previousDataId, that.previousDataId) &&
                Objects.equals(currentDataId, that.currentDataId) &&
                Objects.equals(addTime, that.addTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataVersionId, dataVersionNumber, previousDataId, currentDataId, addTime);
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
    @Column(name = "operation_type", nullable = false)
    public byte getOperationType() {
        return operationType;
    }

    public void setOperationType(byte operationType) {
        this.operationType = operationType;
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
    @Column(name = "data_version_name", nullable = true, length = 255)
    public String getDataVersionName() {
        return dataVersionName;
    }

    public void setDataVersionName(String dataVersionName) {
        this.dataVersionName = dataVersionName;
    }
}
