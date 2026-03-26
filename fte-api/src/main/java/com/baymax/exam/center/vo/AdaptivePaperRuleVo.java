package com.baymax.exam.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(name = "AdaptivePaperRuleVo", description = "自适应组卷规则VO")
public class AdaptivePaperRuleVo {

    @Schema(description = "课程ID")
    private Integer courseId;

    @Schema(description = "目标难度(0-5)")
    private Integer targetDifficulty;

    @Schema(description = "试卷标题")
    private String paperTitle;

    @Schema(description = "题目总数")
    private Integer totalQuestions;

    @Schema(description = "单选题数量")
    private Integer singleChoiceCount;

    @Schema(description = "多选题数量")
    private Integer multipleChoiceCount;

    @Schema(description = "判断题数量")
    private Integer judgmentCount;

    @Schema(description = "填空题数量")
    private Integer completionCount;

    @Schema(description = "主观题数量")
    private Integer subjectiveCount;

    @Schema(description = "是否启用自适应难度")
    private Boolean enableAdaptiveDifficulty;

    @Schema(description = "难度调整阈值(正确率)")
    private Double difficultyThreshold;

    @Schema(description = "知识点覆盖要求(JSON格式)")
    private Map<Integer, Integer> knowledgePointRequirements;
}