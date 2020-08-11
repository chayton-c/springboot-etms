package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.backstage.device.DeviceCustomTemplate;
import com.yingda.lkj.beans.entity.backstage.device.DeviceCustomTemplateField;
import com.yingda.lkj.beans.entity.backstage.device.DeviceMeasurementItem;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceCustomTemplateFieldService;
import com.yingda.lkj.service.backstage.device.DeviceMeasurementItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/7/29
 */
@Service("deviceCustomTemplateFieldService")
public class DeviceCustomTemplateFieldServiceImpl implements DeviceCustomTemplateFieldService {

    @Autowired
    private BaseDao<DeviceCustomTemplateField> deviceCustomTemplateFieldBaseDao;
    @Autowired
    private DeviceMeasurementItemService deviceMeasurementItemService;

    @Override
    public List<DeviceCustomTemplateField> getInUsedFields(String deviceCustomTemplateId) {
        return deviceCustomTemplateFieldBaseDao.find(
                "from DeviceCustomTemplateField where deviceCustomTemplateId = :deviceCustomTemplateId and inUse = :inUse",
                Map.of("deviceCustomTemplateId", deviceCustomTemplateId, "inUse", Constant.IN_USE)
        );
    }

    @Override
    public void setInUse(String deviceCustomTemplateFieldId) {
        deviceCustomTemplateFieldBaseDao.executeHql(
                "update DeviceCustomTemplateField set inUse = :inUse where id = :id",
                Map.of("inUse", Constant.IN_USE, "id", deviceCustomTemplateFieldId)
        );
    }

    @Override
    public void setUnused(String deviceCustomTemplateFieldId) {
        deviceCustomTemplateFieldBaseDao.executeHql(
                "update DeviceCustomTemplateField set inUse = :inUse where id = :id",
                Map.of("inUse", Constant.UNUSED, "id", deviceCustomTemplateFieldId)
        );
    }

    @Override
    public List<DeviceCustomTemplateField> create(DeviceCustomTemplate deviceCustomTemplate, String deviceTypeId) {
        List<DeviceMeasurementItem> deviceMeasurementItems = deviceMeasurementItemService.getByDeviceTypeId(deviceTypeId);
        if (deviceMeasurementItems.isEmpty())
            return new ArrayList<>();

        List<DeviceCustomTemplateField> deviceCustomTemplateFields = new ArrayList<>();
        for (DeviceMeasurementItem deviceMeasurementItem : deviceMeasurementItems)
            deviceCustomTemplateFields.add(new DeviceCustomTemplateField(deviceCustomTemplate, deviceMeasurementItem));

        deviceCustomTemplateFieldBaseDao.bulkInsert(deviceCustomTemplateFields);
        return deviceCustomTemplateFields;
    }
}
