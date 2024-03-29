package com.bupt.mshd2_0boot.controller;

import com.bupt.mshd2_0boot.service.DisasterCountService;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/DisasterCount")
@Slf4j
@Tag(name = "灾情统计")
public class DisasterCountController {

    private final DisasterCountService disasterCountService;

    public DisasterCountController(DisasterCountService disasterCountService){
        this.disasterCountService=disasterCountService;
    }

    @GetMapping("/getDisasterCount")
    @Operation(summary = "获取灾情地域统计信息")
    public Result getDisasterCount() {
        return Result.ok(disasterCountService.list());
    }

    @GetMapping("/getDisasterDateCount")
    @Operation(summary = "时间统计")
    public Result getDisasterDateCount() {
        return Result.ok(disasterCountService.getTimeCount());
    }

    @GetMapping("/getProvinceDisasterCount")
    @Operation(summary = "获取灾情地域统计信息")
    public Result getProvinceDisasterCount() {
        return Result.ok(disasterCountService.getDisasterProvinceCount());
    }

    @GetMapping("/getLoaderCount")
    @Operation(summary = "获取灾情载体统计信息")
    public Result getLoaderCount() {
        return Result.ok(disasterCountService.getLoaderCount());
    }

    @GetMapping("/getTypeCount")
    @Operation(summary = "获取灾情载体统计信息")
    public Result getTypeCount() {
        return Result.ok(disasterCountService.getTypeCount());
    }

}
