package com.yingda.lkj.service.impl.backstage.measurement;

import com.yingda.lkj.beans.entity.backstage.measurement.MeasurementTaskExecuteUser;
import com.yingda.lkj.dao.BaseDao;
import com.yingda.lkj.service.backstage.measurement.MeasurementTaskExecuteUserService;
import com.yingda.lkj.utils.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hood  2020/4/15
 */
@Service("measurementTaskExecuteUserServiceImpl")
public class MeasurementTaskExecuteUserServiceImpl implements MeasurementTaskExecuteUserService {

    @Autowired
    private BaseDao<MeasurementTaskExecuteUser> measurementTaskExecuteUserBaseDao;

    @Override
    public List<String> getMeasurementTaskIdsByUserId(String userId) {
        List<MeasurementTaskExecuteUser> measurementTaskExecuteUsers = measurementTaskExecuteUserBaseDao.find(
                "from MeasurementTaskExecuteUser where executeUserId = :executeUserId",
                Map.of("executeUserId", userId)
        );

        return StreamUtil.getList(measurementTaskExecuteUsers, MeasurementTaskExecuteUser::getMeasurementTaskId);
    }
}
