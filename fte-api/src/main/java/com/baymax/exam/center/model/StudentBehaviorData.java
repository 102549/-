package com.baymax.exam.center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baymax.exam.base.BaseEntity;
import com.baymax.exam.center.enums.CheatRiskLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("ed_student_behavior_data")
@Schema(name = "StudentBehaviorData", description = "学生行为数据表")
public class StudentBehaviorData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "学生id")
    private Integer userId;

    @Schema(description = "考试id")
    private Integer examInfoId;

    @Schema(description = "班级id")
    private Integer classId;

    @Schema(description = "鼠标平均移动速度(像素/秒)")
    private Double mouseAvgSpeed;

    @Schema(description = "鼠标点击频率(次/分钟)")
    private Double clickFrequency;

    @Schema(description = "切屏次数")
    private Integer screenSwitchCount;

    @Schema(description = "答题时长(秒)")
    private Integer answerDuration;

    @Schema(description = "答案修改次数")
    private Integer answerModifyCount;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "答题时段(0-23)")
    private Integer answerHour;

    @Schema(description = "风险等级:0低风险,1中风险,2高风险")
    private CheatRiskLevelEnum riskLevel;

    @Schema(description = "异常特征数量")
    private Integer anomalyCount;

    @Schema(description = "异常特征详情(JSON格式)")
    private String anomalyDetails;

    @Schema(description = "是否已预警")
    private Boolean isAlerted;

    @Schema(description = "预警时间")
    private LocalDateTime alertTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}