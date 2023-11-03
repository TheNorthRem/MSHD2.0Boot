package com.bupt.mshd2_0boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("addressCode")
@Schema(name = "地区编码")
@NoArgsConstructor
@AllArgsConstructor
public class AddressCode {
    @TableId(value = "id", type = IdType.INPUT)
    @Schema(description = "主键:地区代码")
    private String id;

    @TableField("province")
    @Schema(description = "省份")
    private String province;

    @TableField("city")
    @Schema(description = "城市")
    private String city;

    @TableField("county")
    @Schema(description = "县/区")
    private String county;

    @TableField("town")
    @Schema(description = "镇/街道")
    private String town;

    @TableField("village")
    @Schema(description = "村/居委会")
    private String village;
}
