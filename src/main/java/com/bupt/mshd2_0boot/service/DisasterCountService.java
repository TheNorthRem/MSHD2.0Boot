package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.CountEntity;

import java.util.List;
import java.util.Map;

public interface DisasterCountService extends IService<DisasterCount> {
    Map<String,Integer> getDisasterProvinceCount();
    List<CountEntity> getTimeCount();

    List<CountEntity> getTypeCount();
    List<CountEntity> getLoaderCount();
}
