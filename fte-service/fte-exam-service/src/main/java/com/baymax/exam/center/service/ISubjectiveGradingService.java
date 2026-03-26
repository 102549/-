package com.baymax.exam.center.service;

import com.baymax.exam.center.vo.StudentReviewVo;

import java.util.Map;

public interface ISubjectiveGradingService {

    Map<String, Object> gradeSubjectiveQuestion(Integer examInfoId, Integer userId, Integer questionId, String studentAnswer);

    Map<String, Object> batchGradeSubjectiveQuestions(Integer examInfoId, Integer userId);

    String generatePersonalizedComment(Integer questionId, float score, float maxScore, String studentAnswer, String standardAnswer);
}