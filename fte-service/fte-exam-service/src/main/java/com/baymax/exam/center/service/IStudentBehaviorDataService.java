package com.baymax.exam.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.center.model.StudentBehaviorData;
import com.baymax.exam.center.vo.CheatRiskResultVo;
import com.baymax.exam.center.vo.StudentBehaviorVo;

public interface IStudentBehaviorDataService extends IService<StudentBehaviorData> {

    CheatRiskResultVo analyzeCheatRisk(Integer examInfoId, Integer userId, StudentBehaviorVo behaviorVo);

    void saveBehaviorData(Integer examInfoId, Integer userId, Integer classId, StudentBehaviorVo behaviorVo, String userIp);

    void checkAndAlert(Integer examInfoId, Integer userId);
}