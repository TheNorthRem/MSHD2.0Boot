package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.Disaster;

public interface DisasterService extends IService<Disaster> {
    Page<Disaster> listAll(Integer page);
}
