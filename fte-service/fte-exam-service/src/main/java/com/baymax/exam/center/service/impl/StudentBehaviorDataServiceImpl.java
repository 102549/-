package com.baymax.exam.center.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.center.enums.CheatRiskLevelEnum;
import com.baymax.exam.center.mapper.StudentBehaviorDataMapper;
import com.baymax.exam.center.model.StudentBehaviorData;
import com.baymax.exam.center.service.ExamMessageService;
import com.baymax.exam.center.service.IStudentBehaviorDataService;
import com.baymax.exam.center.vo.CheatRiskResultVo;
import com.baymax.exam.center.vo.StudentBehaviorVo;
import com.baymax.exam.message.MessageResult;
import com.baymax.exam.message.feign.MessageServiceClient;
import com.baymax.exam.message.model.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StudentBehaviorDataServiceImpl extends ServiceImpl<StudentBehaviorDataMapper, StudentBehaviorData> implements IStudentBehaviorDataService {

    private static final double MOUSE_SPEED_THRESHOLD = 500.0;
    private static final double CLICK_FREQUENCY_THRESHOLD = 60.0;
    private static final int SCREEN_SWITCH_THRESHOLD = 5;
    private static final int ANSWER_MODIFY_THRESHOLD = 10;

    @Autowired
    private MessageServiceClient messageServiceClient;

    @Autowired
    private ExamMessageService examMessageService;

    @Value("${spring.rabbitmq.enabled:false}")
    private boolean rabbitMQEnabled;

    @Override
    public CheatRiskResultVo analyzeCheatRisk(Integer examInfoId, Integer userId, StudentBehaviorVo behaviorVo) {
        CheatRiskResultVo result = new CheatRiskResultVo();
        List<String> anomalies = new ArrayList<>();

        if (behaviorVo.getMouseAvgSpeed() != null && behaviorVo.getMouseAvgSpeed() > MOUSE_SPEED_THRESHOLD) {
            anomalies.add("鼠标移动速度异常(过快，可能为脚本操作)");
        }

        if (behaviorVo.getClickFrequency() != null && behaviorVo.getClickFrequency() > CLICK_FREQUENCY_THRESHOLD) {
            anomalies.add("点击频率异常(过高，可能为机器作答)");
        }

        if (behaviorVo.getScreenSwitchCount() != null && behaviorVo.getScreenSwitchCount() >= SCREEN_SWITCH_THRESHOLD) {
            anomalies.add("切屏次数过多(疑似查阅外部资料)");
        }

        if (behaviorVo.getAnswerDuration() != null) {
            int hour = LocalDateTime.now().getHour();
            if (hour >= 0 && hour <= 5) {
                anomalies.add("异常答题时段(深夜作答)");
            }
        }

        if (behaviorVo.getAnswerModifyCount() != null && behaviorVo.getAnswerModifyCount() >= ANSWER_MODIFY_THRESHOLD) {
            anomalies.add("答案修改次数过多(疑似抄袭比对)");
        }

        int anomalyCount = anomalies.size();
        CheatRiskLevelEnum riskLevel;
        boolean needAlert = false;

        if (anomalyCount >= 4) {
            riskLevel = CheatRiskLevelEnum.HIGH;
            needAlert = true;
        } else if (anomalyCount >= 2) {
            riskLevel = CheatRiskLevelEnum.MEDIUM;
            needAlert = true;
        } else {
            riskLevel = CheatRiskLevelEnum.LOW;
        }

        result.setRiskLevel(riskLevel);
        result.setAnomalyCount(anomalyCount);
        result.setAnomalies(anomalies);
        result.setNeedAlert(needAlert);
        result.setCheckTime(LocalDateTime.now());

        if (needAlert) {
            String alertMessage = String.format("检测到考生%d存在%d项异常行为，风险等级：%s",
                    userId, anomalyCount, riskLevel.getLabel());
            result.setAlertMessage(alertMessage);
        } else {
            result.setAlertMessage("未检测到明显作弊行为");
        }

        return result;
    }

    @Override
    public void saveBehaviorData(Integer examInfoId, Integer userId, Integer classId, StudentBehaviorVo behaviorVo, String userIp) {
        StudentBehaviorData behaviorData = new StudentBehaviorData();
        behaviorData.setUserId(userId);
        behaviorData.setExamInfoId(examInfoId);
        behaviorData.setClassId(classId);
        behaviorData.setMouseAvgSpeed(behaviorVo.getMouseAvgSpeed());
        behaviorData.setClickFrequency(behaviorVo.getClickFrequency());
        behaviorData.setScreenSwitchCount(behaviorVo.getScreenSwitchCount());
        behaviorData.setAnswerDuration(behaviorVo.getAnswerDuration());
        behaviorData.setAnswerModifyCount(behaviorVo.getAnswerModifyCount());
        behaviorData.setIpAddress(userIp);
        behaviorData.setAnswerHour(LocalDateTime.now().getHour());

        CheatRiskResultVo riskResult = analyzeCheatRisk(examInfoId, userId, behaviorVo);
        behaviorData.setRiskLevel(riskResult.getRiskLevel());
        behaviorData.setAnomalyCount(riskResult.getAnomalyCount());
        behaviorData.setAnomalyDetails(JSON.toJSONString(riskResult.getAnomalies()));
        behaviorData.setIsAlerted(riskResult.getNeedAlert());
        if (riskResult.getNeedAlert()) {
            behaviorData.setAlertTime(LocalDateTime.now());
        }

        save(behaviorData);

        if (riskResult.getNeedAlert() && riskResult.getRiskLevel() == CheatRiskLevelEnum.HIGH) {
            checkAndAlert(examInfoId, userId);
        }
    }

    @Override
    public void checkAndAlert(Integer examInfoId, Integer userId) {
        LambdaQueryWrapper<StudentBehaviorData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentBehaviorData::getExamInfoId, examInfoId)
                .eq(StudentBehaviorData::getUserId, userId)
                .eq(StudentBehaviorData::getIsAlerted, true);

        List<StudentBehaviorData> alertList = list(queryWrapper);

        if (!alertList.isEmpty()) {
            StudentBehaviorData latestAlert = alertList.get(alertList.size() - 1);
            if (latestAlert.getRiskLevel() == CheatRiskLevelEnum.HIGH) {
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("examInfoId", examInfoId);
                messageData.put("userId", userId);
                messageData.put("riskLevel", latestAlert.getRiskLevel().getLabel());
                messageData.put("anomalyDetails", latestAlert.getAnomalyDetails());

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setTitle("考试作弊预警");
                messageInfo.setIntroduce(String.format("考生%d在考试%d中被检测到高风险作弊行为，请及时处理",
                        userId, examInfoId));
                messageInfo.setPath(JSON.toJSONString(messageData));

                if (rabbitMQEnabled) {
                    examMessageService.sendCheatAlertMessage(messageData);
                    log.info("已通过RabbitMQ发送作弊预警消息: examInfoId={}, userId={}", examInfoId, userId);
                } else {
                    try {
                        messageServiceClient.sendMessage(MessageResult.message(messageInfo));
                        log.info("已通过Feign发送作弊预警消息给监考人员: examInfoId={}, userId={}", examInfoId, userId);
                    } catch (Exception e) {
                        log.error("发送预警消息失败: examInfoId={}, userId={}, error={}", examInfoId, userId, e.getMessage());
                    }
                }
            }
        }
    }
}