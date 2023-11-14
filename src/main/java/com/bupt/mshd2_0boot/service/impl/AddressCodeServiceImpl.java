package com.bupt.mshd2_0boot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.mapper.AddressCodeMapper;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.utils.RedisConstants;
import com.bupt.mshd2_0boot.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AddressCodeServiceImpl extends ServiceImpl<AddressCodeMapper, AddressCode> implements AddressCodeService {

    private final static String VUE3_KEY = "value"; // 前端需要的key值

    private final AddressCodeMapper addressCodeMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public AddressCodeServiceImpl(AddressCodeMapper addressCodeMapper, StringRedisTemplate stringRedisTemplate) {
        this.addressCodeMapper = addressCodeMapper;
        this.stringRedisTemplate = stringRedisTemplate;
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

    @Override
    public Result listProvince() {
        // 直接从redis中返回数据
        // JSONObject value形式,方便前端绑定
        return Result.ok(Objects.requireNonNull(stringRedisTemplate.opsForSet().members(RedisConstants.provinces))
                .stream()
                .map((x) -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(VUE3_KEY, x);
                    return jsonObject;
                }));
    }

    @Override
    public Result listCity(String Province) {
        if (StrUtil.isBlank(Province) || !this.isProvince(Province)) {
            return Result.fail("该省份不合法!");
        }
        return Result.ok();
    }

    @Override
    public Result listCounty(String Province, String City) {
        return null;
    }

    @Override
    public Result listTown(String Province, String City, String County) {
        return null;
    }

    @Override
    public Result listVillage(String Province, String City, String County, String Town) {
        return null;
    }

    /**
     * @param province 查询省份
     * @return 返回该省份是否是一个合法省份
     */
    private boolean isProvince(String province) {
        //Redis中的set查找即可
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(RedisConstants.provinces, province));
    }
}
