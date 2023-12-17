package com.bupt.mshd2_0boot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.TimeCountEntity;

import java.util.List;
import java.util.Map;

public interface DisasterCountService extends IService<DisasterCount> {
    Map<String,Integer> getDisasterProvinceCount();
    List<TimeCountEntity> getTimeCount();
}
