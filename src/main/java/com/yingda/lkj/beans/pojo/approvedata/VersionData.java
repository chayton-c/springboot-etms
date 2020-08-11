package com.yingda.lkj.beans.pojo.approvedata;

/**
 * 版本数据
 *
 * @author hood  2020/5/27
 */
public abstract class VersionData {
    public abstract String getId();
    public abstract Double getDataVersionNumber();
    public abstract String getDataVersionId();
    public abstract void setDataVersionNumber(Double versionNumber);
    public abstract void setDataVersionId(String versionId);

    /**
     * 提供唯一键，唯一键相同的多组数据表示这些数据是对于同一个数据的不同历史版本
     */
    public abstract String getUniqueKey();
}
