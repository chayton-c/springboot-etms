package com.yingda.lkj.service.impl.backstage.device;

import com.yingda.lkj.beans.entity.backstage.device.DeviceLocation;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.device.DeviceLocationService;
import com.yingda.lkj.service.base.BaseService;
import com.yingda.lkj.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/7/20
 */
@Service("deviceLocationService")
public class DeviceLocationServiceImpl implements DeviceLocationService {

    @Autowired
    private BaseDao<DeviceLocation> deviceLocationBaseDao;

    @Override
    public void saveOrUpdate(DeviceLocation pageDeviceLocation) {
        String id = pageDeviceLocation.getId();
        if (StringUtils.isNotEmpty(id)) {
            DeviceLocation deviceLocation = deviceLocationBaseDao.get(DeviceLocation.class, id);
            BeanUtils.copyProperties(pageDeviceLocation, deviceLocation, "id", "addTime");
            deviceLocation.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            deviceLocationBaseDao.saveOrUpdate(deviceLocation);
        }
        if (StringUtils.isEmpty(id))
            deviceLocationBaseDao.saveOrUpdate(new DeviceLocation(pageDeviceLocation));
    }

    @Override
    public void updateSeq(DeviceLocation pageDeviceLocation) {
        String id = pageDeviceLocation.getId();
        DeviceLocation deviceLocation = deviceLocationBaseDao.get(DeviceLocation.class, id);
        deviceLocation.setSeq(pageDeviceLocation.getSeq());
        deviceLocation.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        deviceLocationBaseDao.saveOrUpdate(deviceLocation);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        deviceLocationBaseDao.executeHql("delete from DeviceLocation where id in :ids", Map.of("ids", ids));
    }

    @Override
    public void updateSeq(String id, int seq) {
        deviceLocationBaseDao.executeHql(
                "update from DeviceLocation set seq = :seq, updateTime = now() where id = :id", Map.of("seq", seq, "id", id)
        );
    }

    @Override
    public List<DeviceLocation> getLocationsByDeviceId(String deviceId) throws Exception {
        return deviceLocationBaseDao.find(
                "from DeviceLocation where deviceId = :deviceId order by seq",
                Map.of("deviceId", deviceId)
        );
    }

    @Override
    public List<DeviceLocation> getLocationsByDeviceMaintenancePlanId(String deviceMaintenancePlanId) {

        Map<String, Object> params = new HashMap<>();
        params.put("deviceMaintenancePlanId", deviceMaintenancePlanId);

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT\n");
        sqlBuilder.append("  location.longitude,\n");
        sqlBuilder.append("  location.latitude,\n");
        sqlBuilder.append("  location.name AS name,\n");
        sqlBuilder.append("  device.name AS deviceName \n");
        sqlBuilder.append("FROM\n");
        sqlBuilder.append("  device_location location\n");
        sqlBuilder.append("  LEFT JOIN device_maintenance_plan_device planDevice ON planDevice.device_id = location.device_id\n");
        sqlBuilder.append("  LEFT JOIN device ON device.id = location.device_id \n");
        sqlBuilder.append("WHERE\n");
        sqlBuilder.append("  planDevice.device_maintenance_plan_id = :deviceMaintenancePlanId \n");
        sqlBuilder.append("ORDER BY\n");
        sqlBuilder.append("  planDevice.seq,\n");
        sqlBuilder.append("  location.seq");

        String sql = sqlBuilder.toString();
        List<DeviceLocation> deviceLocations = deviceLocationBaseDao.findSQL(sql, params, DeviceLocation.class);
        return deviceLocations;
    }
}
