package com.bupt.mshd2_0boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import org.apache.ibatis.annotations.Select;

public interface DisasterMapper extends BaseMapper<Disaster> {
    @Select("select * from disaster order by upload_time")
    Page<Disaster> listDisaster(Page<Disaster> page);


    @Select("select * from disaster where SUBSTRING(id, 31, 1) = #{type}")
    Page<Disaster> selectType(Page<Disaster> page,Integer type);



}
