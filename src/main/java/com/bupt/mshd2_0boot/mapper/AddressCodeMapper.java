package com.bupt.mshd2_0boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.mshd2_0boot.entity.AddressCode;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AddressCodeMapper extends BaseMapper<AddressCode> {

    @Select("select distinct city from addressCode where province=#{province}")
    List<String> getCityByProvince(String province);

    @Select("select distinct county from addressCode where province=#{province} and city=#{city}")
    List<String> getCountyByProvinceAndCity(String province, String city);

    @Select("select distinct town from addressCode where province=#{province} and city=#{city} and county=#{county}")
    List<String> getTownByProvinceAndCityAndCounty(String province, String city, String county);

    @Select("select distinct village from addressCode where province=#{province} and city=#{city} and county=#{county} and town=#{town}")
    List<String> getVillageByPCCT(String province, String city, String county, String town);
}
