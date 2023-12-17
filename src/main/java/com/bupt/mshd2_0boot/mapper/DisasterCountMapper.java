package com.bupt.mshd2_0boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.TimeCountEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface DisasterCountMapper extends BaseMapper<DisasterCount> {

    @Select("select COUNT(*) as cnt,SUBSTRING(id, 13, 8) as time from disaster group by time")
    List<TimeCountEntity> selectCountByTime();
}
