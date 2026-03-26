package com.baymax.exam.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import com.baymax.exam.center.enums.QuestionTypeEnum;
import com.baymax.exam.center.model.*;
import com.baymax.exam.center.mapper.ExamAnswerResultMapper;
import com.baymax.exam.center.mapper.ExamQuestionMapper;
import com.baymax.exam.center.mapper.QuestionItemMapper;
import com.baymax.exam.center.mapper.QuestionMapper;
import com.baymax.exam.center.service.ISubjectiveGradingService;
import com.baymax.exam.center.utils.HanLPTextSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class SubjectiveGradingServiceImpl implements ISubjectiveGradingService {

    @Autowired
    private HanLPTextSimilarity hanLPTextSimilarity;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionItemMapper questionItemMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamAnswerResultMapper examAnswerResultMapper;

    @Override
    public Map<String, Object> gradeSubjectiveQuestion(Integer examInfoId, Integer userId, Integer questionId, String studentAnswer) {
        Map<String, Object> result = new HashMap<>();

        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getType() != QuestionTypeEnum.SUBJECTIVE) {
            result.put("error", "题目不存在或不是主观题");
            return result;
        }

        String standardAnswer = getStandardAnswer(questionId);
        float maxScore = question.getScore();
        float score = (float) hanLPTextSimilarity.calculateScore(standardAnswer, studentAnswer, maxScore);
        String feedback = hanLPTextSimilarity.generateFeedback(standardAnswer, studentAnswer, maxScore);

        QuestionResultTypeEnum resultType;
        if (score / maxScore >= 0.9) {
            resultType = QuestionResultTypeEnum.CORRECT;
        } else if (score / maxScore >= 0.5) {
            resultType = QuestionResultTypeEnum.PARTIAL;
        } else {
            resultType = QuestionResultTypeEnum.ERROR;
        }

        result.put("questionId", questionId);
        result.put("maxScore", maxScore);
        result.put("score", score);
        result.put("resultType", resultType);
        result.put("feedback", feedback);
        result.put("standardAnswer", standardAnswer);
        result.put("studentAnswer", studentAnswer);

        ExamAnswerResult answerResult = getOrCreateAnswerResult(examInfoId, userId, questionId);
        answerResult.setAnswer(studentAnswer);
        answerResult.setResultType(resultType);
        examAnswerResultMapper.updateById(answerResult);

        return result;
    }

    @Override
    public Map<String, Object> batchGradeSubjectiveQuestions(Integer examInfoId, Integer userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> gradingDetails = new ArrayList<>();
        float totalScore = 0f;
        float totalMaxScore = 0f;

        LambdaQueryWrapper<ExamQuestion> examQuestionWrapper = new LambdaQueryWrapper<>();
        ExamInfo examInfo = new ExamInfo();
        examInfo.setId(examInfoId);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(examQuestionWrapper);

        for (ExamQuestion examQuestion : examQuestions) {
            Question question = questionMapper.selectById(examQuestion.getQuestionId());
            if (question != null && question.getType() == QuestionTypeEnum.SUBJECTIVE) {
                ExamAnswerResult answerResult = getOrCreateAnswerResult(examInfoId, userId, question.getId());
                if (answerResult.getAnswer() != null && !answerResult.getAnswer().isEmpty()) {
                    Map<String, Object> gradingResult = gradeSubjectiveQuestion(
                            examInfoId, userId, question.getId(), answerResult.getAnswer());
                    gradingDetails.add(gradingResult);
                    totalScore += (float) gradingResult.getOrDefault("score", 0f);
                    totalMaxScore += (float) gradingResult.getOrDefault("maxScore", 0f);
                }
            }
        }

        result.put("totalScore", totalScore);
        result.put("totalMaxScore", totalMaxScore);
        result.put("gradingDetails", gradingDetails);
        result.put("gradingTime", new Date());

        return result;
    }

    @Override
    public String generatePersonalizedComment(Integer questionId, float score, float maxScore, String studentAnswer, String standardAnswer) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return "题目信息不存在，无法生成评语。";
        }

        float percentage = (score / maxScore) * 100;
        StringBuilder comment = new StringBuilder();

        comment.append("【").append(question.getContent()).append("】\n");

        if (percentage >= 90) {
            comment.append("优秀：答案准确完整，要点齐全，表达清晰流畅。");
        } else if (percentage >= 80) {
            comment.append("良好：答案基本正确，主要知识点都已涵盖，仅有轻微瑕疵。");
        } else if (percentage >= 70) {
            comment.append("中等：答案基本正确，但对部分关键知识点的理解还不够深入。");
        } else if (percentage >= 60) {
            comment.append("及格：答案存在一定偏差，需要加强对基础知识的理解。");
        } else if (percentage >= 40) {
            comment.append("较差：答案偏离主题较多，建议重新学习相关章节。");
        } else {
            comment.append("不及格：答案存在重大错误，需要系统性地复习相关知识点。");
        }

        if (question.getAnalysis() != null && !question.getAnalysis().isEmpty()) {
            comment.append("\n\n【题目解析】").append(question.getAnalysis());
        }

        List<String> standardKeywords = hanLPTextSimilarity.extractKeywords(standardAnswer, 5);
        if (!standardKeywords.isEmpty()) {
            comment.append("\n\n【参考答案关键词】").append(String.join("、", standardKeywords));
        }

        List<String> missingKeywords = new ArrayList<>();
        List<String> studentKeywords = hanLPTextSimilarity.extractKeywords(studentAnswer, 10);
        for (String keyword : standardKeywords) {
            if (!studentKeywords.contains(keyword)) {
                missingKeywords.add(keyword);
            }
        }

        if (!missingKeywords.isEmpty()) {
            comment.append("\n\n【建议重点复习】").append(String.join("、", missingKeywords));
        }

        return comment.toString();
    }

    private String getStandardAnswer(Integer questionId) {
        LambdaQueryWrapper<QuestionItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionItem::getQuestionId, questionId)
                .isNotNull(QuestionItem::getAnswer);
        List<QuestionItem> correctItems = questionItemMapper.selectList(wrapper);

        if (!correctItems.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (QuestionItem item : correctItems) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }
                sb.append(item.getContent());
            }
            return sb.toString();
        }

        Question question = questionMapper.selectById(questionId);
        return question != null ? question.getAnalysis() : "";
    }

    private ExamAnswerResult getOrCreateAnswerResult(Integer examInfoId, Integer userId, Integer questionId) {
        LambdaQueryWrapper<ExamAnswerResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswerResult::getExamInfoId, examInfoId)
                .eq(ExamAnswerResult::getUserId, userId)
                .eq(ExamAnswerResult::getQuestionId, questionId);

        ExamAnswerResult existing = examAnswerResultMapper.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }

        ExamAnswerResult newResult = new ExamAnswerResult();
        newResult.setExamInfoId(examInfoId);
        newResult.setUserId(userId);
        newResult.setQuestionId(questionId);
        examAnswerResultMapper.insert(newResult);

        return newResult;
    }
}