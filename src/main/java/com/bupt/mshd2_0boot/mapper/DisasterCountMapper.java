package com.bupt.mshd2_0boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.CountEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface DisasterCountMapper extends BaseMapper<DisasterCount> {

    @Select("select COUNT(*) as cnt,SUBSTRING(id, 13, 8) as category from disaster group by category")
    List<CountEntity> selectCountByTime();

    @Select("select COUNT(*) as cnt,SUBSTRING(id, 30, 1) as category from disaster group by category")
    List<CountEntity> selectCountByLoader();

    @Select("select COUNT(*) as cnt,SUBSTRING(id, 31, 1) as category from disaster group by category")
    List<CountEntity> selectCountByType();
}
