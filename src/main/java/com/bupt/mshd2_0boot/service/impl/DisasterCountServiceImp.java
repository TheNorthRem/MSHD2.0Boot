package com.bupt.mshd2_0boot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.mapper.DisasterCountMapper;
import com.bupt.mshd2_0boot.service.DisasterCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DisasterCountServiceImp extends ServiceImpl<DisasterCountMapper, DisasterCount> implements DisasterCountService {
}
