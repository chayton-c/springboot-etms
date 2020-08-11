package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.entity.backstage.device.Device;
import com.yingda.lkj.beans.entity.backstage.device.DeviceSubType;
import com.yingda.lkj.beans.entity.backstage.device.DeviceType;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceService;
import com.yingda.lkj.service.backstage.device.DeviceSubTypeService;
import com.yingda.lkj.service.backstage.device.DeviceTypeService;
import com.yingda.lkj.service.backstage.device.SubDeviceService;
import com.yingda.lkj.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/7/28
 */
@Service("subDeviceService")
public class SubDeviceServiceImpl implements SubDeviceService {

    @Autowired
    private BaseDao<Device> deviceBaseDao;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private BaseService<Device> deviceBaseService;
    @Autowired
    private DeviceSubTypeService deviceSubTypeService;
    @Autowired
    private DeviceTypeService deviceTypeService;


    @Override
    public List<Device> getSubDevices(String deviceId) throws Exception {
        List<Device> devices = deviceBaseDao.find("from Device where parentId = :deviceId order by updateTime desc", Map.of("deviceId", deviceId));

        for (Device device : devices) {
            DeviceType deviceType = deviceTypeService.getById(device.getDeviceTypeId());
            DeviceSubType deviceSubType = deviceSubTypeService.getById(device.getDeviceSubTypeId());
            device.setHasSubDevice(hasSubDevice(device.getId()) ? Device.HAS_SUB_DEVICE : Device.NOT_HAS_SUB_DEVICE);
            device.setDeviceTypeName(deviceType.getName());
            device.setDeviceSubTypeName(deviceSubType.getName());
        }
        return devices;
    }

    @Override
    public Device getParenetDevice(String deviceId) {
        return getParenetDevice(deviceService.getById(deviceId));
    }

    @Override
    public Device getParenetDevice(Device device) {
        if (device == null)
            return null;

        return deviceService.getById(device.getParentId());
    }

    @Override
    public void addSubDevice(String parentDeviceId, String subDeviceId) {
        Device subDevice = deviceService.getById(subDeviceId);
        subDevice.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        subDevice.setParentId(parentDeviceId);
        deviceBaseDao.saveOrUpdate(subDevice);
    }

    @Override
    public void removeSubDevice(String subDeviceId) {
        Device subDevice = deviceService.getById(subDeviceId);
        subDevice.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        subDevice.setParentId(null);
        deviceBaseDao.saveOrUpdate(subDevice);
    }

    @Override
    public boolean hasSubDevice(String deviceId) throws Exception {
        Device device = deviceService.getById(deviceId);
        List<Device> devices = deviceBaseService.find(
                "from Device where stationId = :stationId and parentId = :parentId",
                Map.of("stationId", device.getStationId(), "parentId", device.getId())
        );
        return devices.size() > 0;
    }
}
