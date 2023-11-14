package com.bupt.mshd2_0boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.mapper.DisasterMapper;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisasterServiceImpl extends ServiceImpl<DisasterMapper, Disaster> implements DisasterService {

    private final DisasterMapper disasterMapper;
    @Autowired
    public DisasterServiceImpl(DisasterMapper disasterMapper){
        this.disasterMapper=disasterMapper;
    }
    @Override
    public Map<String, Integer> getDisasterCount() {
        List<Disaster> disasters = this.list();
        Map<String,Integer> map=new HashMap<>();
        for(Disaster d:disasters){
            String id = d.getId();
            String addressCode = id.substring(0, 5);
            addressCode+='0';
            if(map.containsKey(addressCode)){
                Integer cnt = map.get(addressCode);
                map.put(addressCode,cnt+1);
            }else{
                map.put(addressCode,1);
            }
        }
        return map;
    }
}
