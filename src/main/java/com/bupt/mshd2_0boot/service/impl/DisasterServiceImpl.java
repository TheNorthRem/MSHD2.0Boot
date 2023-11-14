package com.bupt.mshd2_0boot.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.mapper.DisasterMapper;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import org.springframework.stereotype.Service;


@Service
public class DisasterServiceImpl extends ServiceImpl<DisasterMapper, Disaster> implements DisasterService {

}
