package com.baymax.exam.center.service;

import com.baymax.exam.center.model.ExamPaper;
import com.baymax.exam.center.model.StudentAbilityProfile;
import com.baymax.exam.center.vo.AbilityProfileVo;
import com.baymax.exam.center.vo.AdaptivePaperRuleVo;

import java.util.List;

public interface IAdaptivePaperService {

    AbilityProfileVo buildAbilityProfile(Integer userId, Integer courseId);

    void updateAbilityProfile(Integer userId, Integer courseId, Integer examInfoId);

    ExamPaper generateAdaptivePaper(Integer userId, AdaptivePaperRuleVo ruleVo);

    Integer calculateRecommendedDifficulty(Integer userId, Integer courseId);

    List<Integer> selectQuestionsByDifficulty(Integer courseId, Integer difficulty, Integer count);

    List<Integer> selectQuestionsByKnowledgePoint(Integer courseId, Integer tagId, Integer difficulty, Integer count);
}