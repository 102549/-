package com.baymax.exam.center.vo;

import com.baymax.exam.center.enums.CheatRiskLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "CheatRiskResultVo", description = "作弊风险检测结果VO")
public class CheatRiskResultVo {

    @Schema(description = "风险等级")
    private CheatRiskLevelEnum riskLevel;

    @Schema(description = "异常特征数量")
    private Integer anomalyCount;

    @Schema(description = "异常特征列表")
    private List<String> anomalies;

    @Schema(description = "是否需要预警")
    private Boolean needAlert;

    @Schema(description = "预警信息")
    private String alertMessage;

    @Schema(description = "检测时间")
    private LocalDateTime checkTime;
}