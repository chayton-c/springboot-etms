package com.yingda.lkj.service.backstage.measurement;

import com.yingda.lkj.beans.entity.backstage.measurement.MeasurementItem;

import java.util.List;

/**
 * @author hood  2020/6/17
 */
public interface MeasurementItemService {

    MeasurementItem getById(String id);

    List<MeasurementItem> getMeasurementItemsByMeasurementTemplateId(String measurementTemplateId);
}
