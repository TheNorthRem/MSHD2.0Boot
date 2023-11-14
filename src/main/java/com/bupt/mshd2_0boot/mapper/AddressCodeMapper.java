package com.bupt.mshd2_0boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.mshd2_0boot.entity.AddressCode;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AddressCodeMapper extends BaseMapper<AddressCode> {
    @Select("select distinct county from addressCode where province=#{Province} and city=#{City}")
    List<String> getCountyByProvinceAndCity(String Province, String City);

    @Select("select distinct town from addressCode where province=#{Province} and city=#{City} and county=#{County}")
    List<String> getTownByProvinceAndCityAndCounty(String Province, String City, String County);

    @Select("select distinct village from addressCode where province=#{Province} and city=#{City} and county=#{County} and town=#{Town}")
    List<String> getVillageByPCCT(String Province, String City, String County, String Town);
}
