package com.baymax.exam.center.controller;

import com.baymax.exam.center.model.ExamPaper;
import com.baymax.exam.center.service.impl.AdaptivePaperServiceImpl;
import com.baymax.exam.center.vo.AbilityProfileVo;
import com.baymax.exam.center.vo.AdaptivePaperRuleVo;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/adaptive-paper")
@Tag(name = "自适应组卷服务")
public class AdaptivePaperController {

    @Autowired
    private AdaptivePaperServiceImpl adaptivePaperService;

    @Operation(summary = "获取学生能力画像")
    @GetMapping("/profile/{courseId}")
    public Result getAbilityProfile(@PathVariable Integer courseId) {
        Integer userId = UserAuthUtil.getUserId();
        AbilityProfileVo profile = adaptivePaperService.buildAbilityProfile(userId, courseId);
        return Result.success(profile);
    }

    @Operation(summary = "生成自适应试卷")
    @PostMapping("/generate")
    public Result generateAdaptivePaper(@RequestBody AdaptivePaperRuleVo ruleVo) {
        Integer userId = UserAuthUtil.getUserId();
        ExamPaper paper = adaptivePaperService.generateAdaptivePaper(userId, ruleVo);
        return Result.success(paper);
    }

    @Operation(summary = "获取推荐难度")
    @GetMapping("/difficulty/{courseId}")
    public Result getRecommendedDifficulty(@PathVariable Integer courseId) {
        Integer userId = UserAuthUtil.getUserId();
        Integer difficulty = adaptivePaperService.calculateRecommendedDifficulty(userId, courseId);
        return Result.success(difficulty);
    }

    @Operation(summary = "更新能力画像")
    @PostMapping("/profile/update/{examInfoId}")
    public Result updateAbilityProfile(@PathVariable Integer examInfoId,
                                     @RequestParam Integer courseId) {
        Integer userId = UserAuthUtil.getUserId();
        adaptivePaperService.updateAbilityProfile(userId, courseId, examInfoId);
        return Result.success("能力画像已更新");
    }
}