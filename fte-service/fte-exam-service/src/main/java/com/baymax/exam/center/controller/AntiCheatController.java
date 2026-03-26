package com.baymax.exam.center.controller;

import com.baymax.exam.center.service.impl.StudentBehaviorDataServiceImpl;
import com.baymax.exam.center.vo.CheatRiskResultVo;
import com.baymax.exam.center.vo.StudentBehaviorVo;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/anti-cheat")
@Tag(name = "防作弊服务")
public class AntiCheatController {

    @Autowired
    private StudentBehaviorDataServiceImpl behaviorDataService;

    @Operation(summary = "提交学生行为数据")
    @PostMapping("/behavior/{examInfoId}")
    public Result submitBehaviorData(@PathVariable Integer examInfoId,
                                      @RequestBody StudentBehaviorVo behaviorVo,
                                      @RequestHeader(value = "X-Real-IP", required = false) String clientIp) {
        Integer userId = UserAuthUtil.getUserId();
        String userIp = clientIp != null ? clientIp : UserAuthUtil.getUserIp();
        behaviorDataService.saveBehaviorData(examInfoId, userId, null, behaviorVo, userIp);
        return Result.success("行为数据已提交");
    }

    @Operation(summary = "获取当前考生风险等级")
    @GetMapping("/risk/{examInfoId}")
    public Result getRiskLevel(@PathVariable Integer examInfoId) {
        Integer userId = UserAuthUtil.getUserId();
        CheatRiskResultVo riskResult = behaviorDataService.analyzeCheatRisk(examInfoId, userId, new StudentBehaviorVo());
        return Result.success(riskResult);
    }

    @Operation(summary = "监考人员获取所有预警考生")
    @GetMapping("/alerts/{examInfoId}")
    public Result getAlerts(@PathVariable Integer examInfoId) {
        return Result.success("预警列表");
    }
}