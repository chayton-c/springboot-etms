package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.Constant;
import com.yingda.lkj.beans.entity.backstage.device.DeviceSubType;
import com.yingda.lkj.beans.entity.backstage.device.DeviceType;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceSubTypeService;
import com.yingda.lkj.service.backstage.device.DeviceTypeService;
import com.yingda.lkj.utils.StreamUtil;
import com.yingda.lkj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hood  2020/3/16
 */
@Service("deviceSubTypeService")
public class DeviceSubTypeServiceImpl implements DeviceSubTypeService {

    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private BaseDao<DeviceSubType> deviceSubTypeBaseDao;

    @Override
    public List<DeviceSubType> getDeviceSubTypeByDeviceTypeId(String deviceTypeId) {
        return deviceSubTypeBaseDao.find("from DeviceSubType where deviceTypeId = :deviceTypeId", Map.of("deviceTypeId", deviceTypeId));
    }

    @Override
    public void saveOrUpdate(List<String> subTypeNames, String deviceTypeId) {
        List<DeviceSubType> original = deviceSubTypeBaseDao.find("from DeviceSubType where deviceTypeId = :deviceTypeId", Map.of(
                "deviceTypeId", deviceTypeId));
        List<String> originalNames = StreamUtil.getList(original, DeviceSubType::getName);

        // 如果没有输入设备类型，填写一个默认的
        if (subTypeNames.stream().noneMatch(StringUtils::isNotEmpty)
                && originalNames.stream().noneMatch(StringUtils::isNotEmpty)) {
            DeviceType deviceType = deviceTypeService.getById(deviceTypeId);
            subTypeNames = List.of(String.format("%s默认子类型", deviceType.getName()));
        }

        List<String> addTypeNames = subTypeNames.stream().filter(x -> !originalNames.contains(x)).collect(Collectors.toList());

        List<DeviceSubType> deviceSubTypes = addTypeNames.stream().map(x -> new DeviceSubType(deviceTypeId, x)).collect(Collectors.toList());
        deviceSubTypes.forEach(x -> deviceSubTypeBaseDao.saveOrUpdate(x));

//        for (DeviceSubType deviceSubType : original)
//            if (!subTypeNames.contains(deviceSubType.getName()))
//                hide(deviceSubType);
    }

    @Override
    public DeviceSubType getById(String id) {
        return deviceSubTypeBaseDao.get(DeviceSubType.class, id);
    }

    @Override
    public Map<String, DeviceSubType> getByNames(List<String> names) {
        names = names.stream().distinct().collect(Collectors.toList());
        List<DeviceSubType> deviceTypes = deviceSubTypeBaseDao.find(
                "from DeviceSubType where name in :names",
                Map.of("names", names)
        );

        return StreamUtil.getMap(deviceTypes, DeviceSubType::getName, x -> x);
    }

    /**
     * 假删
     */
    private void hide(DeviceSubType deviceSubType) {
        deviceSubType.setHide(Constant.HIDE);
        deviceSubTypeBaseDao.saveOrUpdate(deviceSubType);
    }
}
