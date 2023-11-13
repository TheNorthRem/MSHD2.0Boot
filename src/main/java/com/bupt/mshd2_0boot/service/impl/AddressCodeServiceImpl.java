package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.mapper.AddressCodeMapper;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressCodeServiceImpl extends ServiceImpl<AddressCodeMapper, AddressCode> implements AddressCodeService {

    private final AddressCodeMapper addressCodeMapper;

    @Autowired
    public AddressCodeServiceImpl(AddressCodeMapper addressCodeMapper){
        this.addressCodeMapper=addressCodeMapper;
    }

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
        // 一旦有重复数据污染数据库记录到日志，并且返回null
        try {
            addressCode = this.getOne(queryWrapper);
        } catch (Exception exception) {
            log.error("数据库查询有误，可能出现了数据污染的情况:{}", exception.getMessage());
            return null;
        }
        // 若查询不到编码则返回null，否则返回地区编码
        return addressCode != null ? addressCode.getId() : null;
    }

    @Override
    public AddressCode getAddress(String code) {
        // 数据不合规直接报错
        if (StrUtil.isBlank(code)) {
            return null;
        }
        // 根据地区代码(主键)查询
        // 若查询不到则返回null，否则返回地区信息
        return this.getById(code);
    }

    public List<String> listProvince(){
        return addressCodeMapper.listProvince();
    }

    @Override
    public List<String> listCity(String Province) {
        return addressCodeMapper.getCityByProvince(Province);
    }

    @Override
    public List<String> listCounty(String Province, String City) {
        return addressCodeMapper.getCountyByProvinceAndCity(Province,City);
    }

    @Override
    public List<String> listTown(String Province, String City, String County) {
        return addressCodeMapper.getTownByProvinceAndCityAndCounty(Province, City, County);
    }

    @Override
    public List<String> listVillage(String Province, String City, String County, String Town) {
        return addressCodeMapper.getVillageByPCCT(Province, City, County, Town);
    }
}
