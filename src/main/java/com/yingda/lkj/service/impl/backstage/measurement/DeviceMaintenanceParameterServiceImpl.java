package com.yingda.lkj.service.impl.backstage.measurement;

import com.yingda.lkj.beans.entity.backstage.device.Device;
import com.yingda.lkj.beans.entity.backstage.measurement.*;
import com.yingda.lkj.beans.pojo.utils.UnitPojo;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceService;
import com.yingda.lkj.service.backstage.measurement.*;
import com.yingda.lkj.utils.StringUtils;
import com.yingda.lkj.utils.UnitUtil;
import com.yingda.lkj.utils.math.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/6/10
 */
@Service("deviceMaintenanceParameterService")
public class DeviceMaintenanceParameterServiceImpl implements DeviceMaintenanceParameterService {

    @Autowired
    private BaseDao<DeviceMaintenanceParameter> deviceMaintenanceParameterBaseDao;
    @Autowired
    private MeasurementItemFieldService measurementItemFieldService;
    @Autowired
    private MeasurementTaskDetailService measurementTaskDetailService;
    @Autowired
    private MeasurementTaskService measurementTaskService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private MeasurementItemService measurementItemService;

    @Override
    public void saveOrUpdateDeviceMaintenanceParameter(MeasurementItemFieldValue measurementItemFieldValue) {
        String valueStr = measurementItemFieldValue.getValue();
        // 转不成数字，就不生成设备维护参数
        if (!NumberUtil.isDouble(valueStr))
            return;
        // 如果没有对应的设备测量字段，不添加新的设备维护参数记录
        if (StringUtils.isEmpty(measurementItemFieldValue.getDeviceMeasurementItemId()))
            return;

        String measurementItemFieldValueId = measurementItemFieldValue.getId();
        String measurementItemFieldId = measurementItemFieldValue.getMeasurementItemFieldId();
        MeasurementItemField measurementItemField = measurementItemFieldService.getById(measurementItemFieldId);

        DeviceMaintenanceParameter deviceMaintenanceParameter = deviceMaintenanceParameterBaseDao.get(
                "from DeviceMaintenanceParameter where sourceDataId = :measurementItemFieldValueId",
                Map.of("measurementItemFieldValueId", measurementItemFieldValueId)
        );

        String unitName = measurementItemField.getUnitName();
        double value = Double.parseDouble(valueStr);
        UnitPojo unitPojo = UnitUtil.convertToSmallestUnit(unitName, value);

        String measurementTaskDetailId = measurementItemFieldValue.getMeasurementTaskDetailId();
        MeasurementTaskDetail measurementTaskDetail = measurementTaskDetailService.getById(measurementTaskDetailId); // 子任务
        MeasurementTask measurementTask = measurementTaskService.getById(measurementTaskDetail.getMeasurementTaskId()); // 任务
        Device device = deviceService.getById(measurementItemFieldValue.getDeviceId()); // 设备
        Map<String, String> executeUserNameMap = measurementTaskDetailService.getExecuteUserNames(List.of(measurementTaskDetail)); // 执行人
        String executeUserNames = executeUserNameMap.get(measurementTaskDetail.getMeasurementTaskId());
        MeasurementItem measurementItem = measurementItemService.getById(measurementItemField.getMeasurementItemId());

        if (deviceMaintenanceParameter == null)
            deviceMaintenanceParameter = new DeviceMaintenanceParameter(measurementItemFieldValue, unitPojo, measurementItemField, executeUserNames, measurementTask, device, measurementItem);

        deviceMaintenanceParameter.setValue(unitPojo.getValue());
        deviceMaintenanceParameter.setUnitName(unitPojo.getUnitName());
        deviceMaintenanceParameter.setExecuteUserNames(executeUserNames);

        deviceMaintenanceParameterBaseDao.saveOrUpdate(deviceMaintenanceParameter);
    }
}
