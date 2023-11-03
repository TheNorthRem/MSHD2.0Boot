package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.mapper.AddressCodeMapper;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressCodeServiceImpl extends ServiceImpl<AddressCodeMapper, AddressCode> implements AddressCodeService {
    @Override
    public String getCode(String province, String city, String county, String town, String village) {
        // 数据不合规直接报错
        if (!StrUtil.isAllNotBlank(province, city, county, town, village)) {
            return null;
        }
        // 条件查询
        QueryWrapper<AddressCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("province", province);
        queryWrapper.eq("city", city);
        queryWrapper.eq("county", county);
        queryWrapper.eq("town", town);
        queryWrapper.eq("village", village);
        AddressCode addressCode;
        // 一旦有重复数据污染数据库记录到日志，同时提醒用户上报给数据库管理员
        try {
            addressCode = this.getOne(queryWrapper);
        } catch (Exception exception) {
            log.error("数据库查询有误:{}", exception.getMessage());
            return null;
        }
        // 若查询不到则返回错误信息，否则返回地区编码
        return addressCode != null ? addressCode.getId() : null;
    }

    @Override
    public AddressCode getAddress(String code) {
        // 数据不合规直接报错
        if (StrUtil.isBlank(code)) {
            return null;
        }
        // 根据地区代码(主键)查询
        AddressCode addressCode = this.getById(code);
        // 若查询不到则返回错误信息，否则返回地区信息
        return addressCode;
    }
}
