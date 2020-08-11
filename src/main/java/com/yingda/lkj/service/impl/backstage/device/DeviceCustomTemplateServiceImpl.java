package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.entity.backstage.device.DeviceCustomTemplate;
import com.yingda.lkj.beans.entity.backstage.device.DeviceType;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceCustomTemplateFieldService;
import com.yingda.lkj.service.backstage.device.DeviceCustomTemplateService;
import com.yingda.lkj.service.backstage.device.DeviceSubTypeService;
import com.yingda.lkj.service.backstage.device.DeviceTypeService;
import com.yingda.lkj.service.base.BaseService;
import com.yingda.lkj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * @author hood  2020/7/29
 */
@Service("deviceCustomTemplateService")
public class DeviceCustomTemplateServiceImpl implements DeviceCustomTemplateService {

    @Autowired
    private BaseDao<DeviceCustomTemplate> deviceCustomTemplateBaseDao;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private DeviceSubTypeService deviceSubTypeService;
    @Autowired
    private DeviceCustomTemplateFieldService deviceCustomTemplateFieldService;


    @Override
    public DeviceCustomTemplate getById(String deviceCustomTemplateId) {
        return deviceCustomTemplateBaseDao.get(DeviceCustomTemplate.class, deviceCustomTemplateId);
    }

    @Override
    public DeviceCustomTemplate save(DeviceCustomTemplate pageDeviceCustomTemplate, String sectionId) {
        // 生成自定义模板
        String deviceTypeId = pageDeviceCustomTemplate.getDeviceTypeId();
        String name = pageDeviceCustomTemplate.getName();

        DeviceType deviceType = deviceTypeService.getById(deviceTypeId);
        // 如果没有名字，根据设备类型和子类型自动生成
        if (StringUtils.isEmpty(name))
            name = String.format("%s查询记录 %d", deviceType.getName(), System.currentTimeMillis());

        DeviceCustomTemplate deviceCustomTemplate = new DeviceCustomTemplate(sectionId, name, deviceType);

        deviceCustomTemplateBaseDao.saveOrUpdate(deviceCustomTemplate);
        deviceCustomTemplateBaseDao.flush();
        // 根据设备类型生成所有的设备电气特性
        deviceCustomTemplateFieldService.create(deviceCustomTemplate, deviceTypeId);
        return deviceCustomTemplate;
    }

    @Override
    public void delete(String deviceCustomTemplateId) {
        deviceCustomTemplateBaseDao.executeHql(
                "delete from DeviceCustomTemplate where id = :deviceCustomTemplateId",
                Map.of("deviceCustomTemplateId", deviceCustomTemplateId)
        );
    }
}
