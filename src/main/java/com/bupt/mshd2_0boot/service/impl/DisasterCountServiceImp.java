package com.bupt.mshd2_0boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.CountEntity;
import com.bupt.mshd2_0boot.mapper.DisasterCountMapper;
import com.bupt.mshd2_0boot.service.DisasterCountService;
import com.bupt.mshd2_0boot.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DisasterCountServiceImp extends ServiceImpl<DisasterCountMapper, DisasterCount> implements DisasterCountService {


    private final DisasterCountMapper disasterCountMapper;

    @Autowired
    public DisasterCountServiceImp(DisasterCountMapper disasterCountMapper){ this.disasterCountMapper=disasterCountMapper;}


    @Override
    public  Map<String,Integer> getDisasterProvinceCount() {
        List<DisasterCount> list = list();
        Map<String,Integer> cnt =new HashMap<>();
        for (DisasterCount dc:list
             ) {
            String id = dc.getId();
            Integer count = dc.getCount();
            String provinceCode = id.substring(0, 2);
            provinceCode+="0000";
            cnt.put(provinceCode,cnt.getOrDefault(provinceCode,0)+count);
        }

        return cnt;
    }

    @Override
    public List<CountEntity> getTimeCount() {
        List<CountEntity> timeCountEntities = disasterCountMapper.selectCountByTime();

        for (CountEntity entity:
                timeCountEntities) {
            entity.setCategory(Tools.formatDate(entity.getCategory()));
        }

        return timeCountEntities;
    }

    @Override
    public List<CountEntity> getTypeCount() {
        return disasterCountMapper.selectCountByType();
    }

    @Override
    public List<CountEntity> getLoaderCount() {
        return disasterCountMapper.selectCountByLoader();
    }
}
