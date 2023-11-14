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
public class AddressController {
    private final AddressCodeService addressCodeService;

    @Autowired
    public AddressController(AddressCodeService addressCodeService) {
        this.addressCodeService = addressCodeService;
    }

    @GetMapping("/ListProvince")
    @Operation(summary = "查询省份")
    public Result ListProvince() {
        return addressCodeService.listProvince();
    }

    @GetMapping("/ListCity")
    @Operation(summary = "查询城市")
    @Parameter(name = "province", description = "省份名")
    public Result ListCity(@RequestParam(name = "province") String province) {
        return addressCodeService.listCity(province);
    }

    @GetMapping("/ListCounty")
    @Operation(summary = "查询县/区")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市")})
    public Result ListCounty(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city) {
        return addressCodeService.listCounty(province, city);
    }

    @GetMapping("/ListTown")
    @Operation(summary = "查询镇/街道")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市"),
            @Parameter(name = "county", description = "县/区")})
    public Result ListCounty(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city, @RequestParam(name = "county") String county) {
        return addressCodeService.listTown(province, city, county);
    }

    @GetMapping("/ListVillage")
    @Operation(summary = "查询村/居委会")
    @Parameters({@Parameter(name = "province", description = "省份名"),
            @Parameter(name = "city", description = "市"),
            @Parameter(name = "county", description = "县/区"),
            @Parameter(name = "town", description = "镇/街道")})
    public Result ListVillage(@RequestParam(name = "province") String province, @RequestParam(name = "city") String city, @RequestParam(name = "county") String county, @RequestParam(name = "town") String town) {
        return addressCodeService.listVillage(province, city, county, town);
    }
}
