package com.bupt.mshd2_0boot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.mshd2_0boot.entity.AddressCode;

import java.util.List;

public interface AddressCodeService extends IService<AddressCode> {
    /**
     * 根据地区地理信息来获取地区编码
     *
     * @param province 省份
     * @param city     城市
     * @param county   县/区
     * @param town     镇/街道
     * @param village  村庄/居委会
     * @return 地区编码/错误时返回null
     */
    String getCode(String province, String city, String county, String town, String village);

    /**
     * 根据地区编码来获取地区信息
     *
     * @param code 地区编码
     * @return 地区值，以实体类的模式返回/错误时返回null
     */
    AddressCode getAddress(String code);

    List<String> listProvince();
    List<String> listCity(String Province);

    List<String> listCounty(String Province,String City);

    List<String> listTown(String Province,String City,String County);

    List<String> listVillage(String Province,String City,String County,String Town );
}
