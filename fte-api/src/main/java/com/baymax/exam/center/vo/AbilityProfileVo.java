package com.baymax.exam.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "AbilityProfileVo", description = "学生能力画像VO")
public class AbilityProfileVo {

    @Schema(description = "学生ID")
    private Integer userId;

    @Schema(description = "课程ID")
    private Integer courseId;

    @Schema(description = "总答题数量")
    private Integer totalAnswers;

    @Schema(description = "正确率")
    private Double accuracyRate;

    @Schema(description = "能力等级(1-5)")
    private Integer abilityLevel;

    @Schema(description = "知识点掌握情况")
    private List<AbilityProfileVo.KnowledgePoint> masteryList;

    @Schema(description = "推荐难度")
    private Integer recommendedDifficulty;

    @Schema(description = "历史表现趋势")
    private String performanceTrend;

    @Data
    public static class KnowledgePoint {
        private Integer tagId;
        private String tagName;
        private Double masteryRate;
        private Integer questionCount;
        private Integer correctCount;
    }
}