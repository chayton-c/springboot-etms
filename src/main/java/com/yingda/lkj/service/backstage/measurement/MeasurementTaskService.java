package com.yingda.lkj.service.backstage.measurement;

import com.yingda.lkj.beans.entity.backstage.device.DeviceMaintenancePlan;
import com.yingda.lkj.beans.entity.backstage.measurement.MeasurementTask;
import com.yingda.lkj.beans.entity.system.User;
import com.yingda.lkj.beans.pojo.measurement.UserMeasurementTaskDetail;

import java.sql.Timestamp;
import java.util.List;

/**
 * 测量主任务接口
 */
public interface MeasurementTaskService {

    /**
     * 保存任务表及执行人关联表
     * @param pageMeasurementTask 未经处理的pageMode
     * @param submitUserId 提交人
     * @param executeUserIds 执行人数组
     */
    MeasurementTask saveOrUpdate(MeasurementTask pageMeasurementTask, User submitUserId, String[] executeUserIds);

    /**
     * 关闭任务
     */
    void closeTask(String measurementTaskId);

    /**
     * 确认任务(提交人点击确认任务后，执行人才能看到)
     */
    void submitTask(String measurementTaskId);

    /**
     * 执行人提交任务
     */
    void executeTask(String measurementTaskId);

    List<MeasurementTask> getByIds(List<String> ids);

    MeasurementTask getById(String id);

    List<MeasurementTask> getByDeviceMaintenancePlan(List<DeviceMaintenancePlan> deviceMaintenancePlans, Timestamp startTime, Timestamp endTime);

    /**
     * 根据计划及起止时间生成任务(只是给甘特图展示用的，不会操作数据库)
     *
     *
     */
    List<MeasurementTask> getTasksGantt(List<DeviceMaintenancePlan> deviceMaintenancePlans, Timestamp startTime, Timestamp endTime);

    UserMeasurementTaskDetail getUserMeasurementTaskDetail(String userId);

    void closeMeasurementTask(List<MeasurementTask> measurementTasks);
}
