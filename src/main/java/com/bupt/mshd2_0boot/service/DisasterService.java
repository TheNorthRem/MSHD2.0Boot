package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.Disaster;

import java.util.Map;

public interface DisasterService extends IService<Disaster> {
    public Map<String,Integer> getDisasterCount();

}
