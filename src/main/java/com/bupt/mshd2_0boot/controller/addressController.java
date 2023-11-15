package com.bupt.mshd2_0boot.controller;


import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Address")
@Slf4j
@Tag(name = "地址")
public class addressController {
    private final AddressCodeService addressCodeService;

    @Autowired
    public addressController(AddressCodeService addressCodeService) {
        this.addressCodeService = addressCodeService;
    }

    @GetMapping("/listProvince")
    @Operation(summary = "查询省份")
    public Result listProvince() {
        return addressCodeService.listProvince();
    }

    @GetMapping("/listCity")
    @Operation(summary = "查询城市")
    @Parameter(name = "province", description = "省份名")
    public Result listCity(@RequestParam(name = "province") String province) {
        return addressCodeService.listCity(province);
    }

    @GetMapping("/listCounty")
    @Operation(summary = "查询县/区")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市")})
    public Result listCounty(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city) {
        return addressCodeService.listCounty(province, city);
    }

    @GetMapping("/listTown")
    @Operation(summary = "查询镇/街道")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市"),
            @Parameter(name = "county", description = "县/区")})
    public Result listTown(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city, @RequestParam(name = "county") String county) {
        return addressCodeService.listTown(province, city, county);
    }

    @GetMapping("/listVillage")
    @Operation(summary = "查询村/居委会")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市"),
            @Parameter(name = "county", description = "县/区"),
            @Parameter(name = "town", description = "镇/街道")})
    public Result listVillage(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city, @RequestParam(name = "county") String county, @RequestParam(name = "town") String town) {
        return addressCodeService.listVillage(province, city, county, town);
    }
}
