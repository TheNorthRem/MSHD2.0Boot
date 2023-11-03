package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.mapper.AddressCodeMapper;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.utils.Result;
import org.springframework.stereotype.Service;

@Service
public class AddressCodeServiceImpl extends ServiceImpl<AddressCodeMapper, AddressCode> implements AddressCodeService {
    @Override
    public Result getCode(String province, String city, String county, String town, String village) {
        // 数据不合规直接报错
        if (!StrUtil.isAllNotBlank(province, city, county, town, village)) {
            return Result.fail("输入地址不能为空!");
        }
        // 条件查询
        QueryWrapper<AddressCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("province", province);
        queryWrapper.eq("city", city);
        queryWrapper.eq("county", county);
        queryWrapper.eq("town", town);
        queryWrapper.eq("village", village);
        AddressCode addressCode = this.getOne(queryWrapper);
        // 若查询不到则返回错误信息，否则返回地区编码
        return addressCode != null ? Result.ok(addressCode.getId()) : Result.fail("没有该地区数据!");
    }

    @Override
    public Result getAddress(String code) {
        // 数据不合规直接报错
        if (StrUtil.isBlank(code)) {
            return Result.fail("编码不能为空!");
        }
        // 根据地区代码(主键)查询
        AddressCode addressCode = this.getById(code);
        // 若查询不到则返回错误信息，否则返回地区信息
        return addressCode != null ? Result.ok(addressCode) : Result.fail("查询不到该信息!");
    }
}
