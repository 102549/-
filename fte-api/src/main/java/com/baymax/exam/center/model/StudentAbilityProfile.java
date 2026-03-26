package com.baymax.exam.center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("ed_student_ability_profile")
@Schema(name = "StudentAbilityProfile", description = "学生能力画像表")
public class StudentAbilityProfile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "学生ID")
    private Integer userId;

    @Schema(description = "课程ID")
    private Integer courseId;

    @Schema(description = "总答题数量")
    private Integer totalAnswers;

    @Schema(description = "正确答题数量")
    private Integer correctAnswers;

    @Schema(description = "正确率(0-100)")
    private Double accuracyRate;

    @Schema(description = "平均答题时间(秒)")
    private Double avgAnswerTime;

    @Schema(description = "知识点掌握情况(JSON格式)")
    private String knowledgePointStatus;

    @Schema(description = "能力等级(1-5)")
    private Integer abilityLevel;

    @Schema(description = "最近考试ID")
    private Integer lastExamId;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;
}