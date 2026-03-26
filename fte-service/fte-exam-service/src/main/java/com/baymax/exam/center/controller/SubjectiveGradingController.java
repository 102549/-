package com.baymax.exam.center.controller;

import com.baymax.exam.center.service.impl.SubjectiveGradingServiceImpl;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/grading")
@Tag(name = "主观题批改服务")
public class SubjectiveGradingController {

    @Autowired
    private SubjectiveGradingServiceImpl subjectiveGradingService;

    @Operation(summary = "批改单道主观题")
    @PostMapping("/question/{examInfoId}/{questionId}")
    public Result gradeQuestion(@PathVariable Integer examInfoId,
                               @PathVariable Integer questionId,
                               @RequestBody Map<String, String> answerData) {
        Integer userId = UserAuthUtil.getUserId();
        String studentAnswer = answerData.get("answer");
        Map<String, Object> result = subjectiveGradingService.gradeSubjectiveQuestion(
                examInfoId, userId, questionId, studentAnswer);
        return Result.success(result);
    }

    @Operation(summary = "批量批改主观题")
    @PostMapping("/batch/{examInfoId}")
    public Result batchGrade(@PathVariable Integer examInfoId) {
        Integer userId = UserAuthUtil.getUserId();
        Map<String, Object> result = subjectiveGradingService.batchGradeSubjectiveQuestions(examInfoId, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取个性化评语")
    @GetMapping("/comment/{examInfoId}/{questionId}")
    public Result getPersonalizedComment(@PathVariable Integer examInfoId,
                                        @PathVariable Integer questionId) {
        return Result.success("个性化评语");
    }
}