package com.bupt.mshd2_0boot.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.mapper.DisasterMapper;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.EnvironmentValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DisasterServiceImpl extends ServiceImpl<DisasterMapper, Disaster> implements DisasterService {
    private final DisasterMapper mapper;

    @Autowired
    public DisasterServiceImpl(DisasterMapper mapper){
        this.mapper=mapper;
    }
    @Override
    public Page<Disaster> listAll(Integer page) {
        Page<Disaster> disasterPage = new Page<>(page, Long.parseLong(EnvironmentValue.getParamSettings("pageSize")));
        return mapper.listDisaster(disasterPage);
    }

    @Override
    public Page<Disaster> selectByType(Integer page, Integer type) {
        Page<Disaster> disasterPage = new Page<>(page, Long.parseLong(EnvironmentValue.getParamSettings("pageSize")));
        return mapper.selectType(disasterPage,type);
    }
}
