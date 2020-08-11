package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.entity.backstage.device.DeviceSubType;
import com.yingda.lkj.beans.entity.backstage.device.DeviceType;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceExtendFieldService;
import com.yingda.lkj.service.backstage.device.DeviceSubTypeService;
import com.yingda.lkj.service.backstage.device.DeviceTypeService;
import com.yingda.lkj.utils.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood  2020/1/2
 */
@Service("deviceTypeService")
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    private DeviceExtendFieldService deviceExtendFieldService;
    @Autowired
    private DeviceSubTypeService deviceSubTypeService;

    @Autowired
    private BaseDao<DeviceType> deviceTypeBaseDao;


    @Override
    public void saveOrUpdate(DeviceType deviceType) {
        deviceTypeBaseDao.saveOrUpdate(deviceType);
    }

    @Override
    public List<DeviceType> getAllDeviceTypes() {
        return deviceTypeBaseDao.find("from DeviceType");
    }

    @Override
    public DeviceType getById(String deviceTypeId) {
        return deviceTypeBaseDao.get(DeviceType.class, deviceTypeId);
    }

    @Override
    public DeviceType getByDeviceSubTypeId(String deviceSubTypeId) {
        DeviceSubType deviceSubType = deviceSubTypeService.getById(deviceSubTypeId);
        return getById(deviceSubType.getDeviceTypeId());
    }

    @Override
    public Map<String, DeviceType> getByNames(List<String> names) {
        names = names.stream().distinct().collect(Collectors.toList());
        List<DeviceType> deviceTypes = deviceTypeBaseDao.find(
                "from DeviceType where name in :names",
                Map.of("names", names)
        );

        return StreamUtil.getMap(deviceTypes, DeviceType::getName, x -> x);
    }

}
