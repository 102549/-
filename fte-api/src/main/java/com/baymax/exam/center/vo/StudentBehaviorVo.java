package com.baymax.exam.center.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "StudentBehaviorVo", description = "学生行为数据提交VO")
public class StudentBehaviorVo {

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
}