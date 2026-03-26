package com.baymax.exam.center.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.center.enums.QuestionResultTypeEnum;
import com.baymax.exam.center.enums.QuestionTypeEnum;
import com.baymax.exam.center.mapper.*;
import com.baymax.exam.center.model.*;
import com.baymax.exam.center.service.IAdaptivePaperService;
import com.baymax.exam.center.vo.AbilityProfileVo;
import com.baymax.exam.center.vo.AdaptivePaperRuleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdaptivePaperServiceImpl extends ServiceImpl<StudentAbilityProfileMapper, StudentAbilityProfile> implements IAdaptivePaperService {

    @Autowired
    private StudentAbilityProfileMapper abilityProfileMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionItemMapper questionItemMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamAnswerResultMapper answerResultMapper;

    @Autowired
    private ExamScoreRecordMapper scoreRecordMapper;

    @Autowired
    private TagsMapper tagMapper;

    @Override
    public AbilityProfileVo buildAbilityProfile(Integer userId, Integer courseId) {
        AbilityProfileVo profileVo = new AbilityProfileVo();
        profileVo.setUserId(userId);
        profileVo.setCourseId(courseId);

        List<ExamAnswerResult> answerResults = getAnswerResults(userId, courseId);

        int totalAnswers = answerResults.size();
        int correctAnswers = (int) answerResults.stream()
                .filter(r -> r.getResultType() == QuestionResultTypeEnum.CORRECT)
                .count();

        double accuracyRate = totalAnswers > 0 ? (correctAnswers * 100.0 / totalAnswers) : 0.0;

        profileVo.setTotalAnswers(totalAnswers);
        profileVo.setAccuracyRate(Math.round(accuracyRate * 100.0) / 100.0);

        int abilityLevel = calculateAbilityLevel(accuracyRate);
        profileVo.setAbilityLevel(abilityLevel);

        int recommendedDifficulty = calculateRecommendedDifficulty(userId, courseId);
        profileVo.setRecommendedDifficulty(recommendedDifficulty);

        List<AbilityProfileVo.KnowledgePoint> knowledgePointMasteryList = buildKnowledgePointMastery(userId, courseId);
        profileVo.setMasteryList(knowledgePointMasteryList);

        String performanceTrend = analyzePerformanceTrend(userId, courseId);
        profileVo.setPerformanceTrend(performanceTrend);

        StudentAbilityProfile profile = abilityProfileMapper.findByUserAndCourse(userId, courseId);
        if (profile != null) {
            profile.setTotalAnswers(totalAnswers);
            profile.setCorrectAnswers(correctAnswers);
            profile.setAccuracyRate(profileVo.getAccuracyRate());
            profile.setAbilityLevel(abilityLevel);
            profile.setKnowledgePointStatus(JSON.toJSONString(knowledgePointMasteryList));
            profile.setUpdatedAt(LocalDateTime.now());
            abilityProfileMapper.updateById(profile);
        } else {
            profile = new StudentAbilityProfile();
            profile.setUserId(userId);
            profile.setCourseId(courseId);
            profile.setTotalAnswers(totalAnswers);
            profile.setCorrectAnswers(correctAnswers);
            profile.setAccuracyRate(profileVo.getAccuracyRate());
            profile.setAbilityLevel(abilityLevel);
            profile.setKnowledgePointStatus(JSON.toJSONString(knowledgePointMasteryList));
            profile.setUpdatedAt(LocalDateTime.now());
            abilityProfileMapper.insert(profile);
        }

        return profileVo;
    }

    @Override
    public void updateAbilityProfile(Integer userId, Integer courseId, Integer examInfoId) {
        StudentAbilityProfile profile = abilityProfileMapper.findByUserAndCourse(userId, courseId);

        if (profile == null) {
            profile = new StudentAbilityProfile();
            profile.setUserId(userId);
            profile.setCourseId(courseId);
            profile.setLastExamId(examInfoId);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            abilityProfileMapper.insert(profile);
        } else {
            profile.setLastExamId(examInfoId);
            profile.setUpdatedAt(LocalDateTime.now());
            abilityProfileMapper.updateById(profile);
        }

        buildAbilityProfile(userId, courseId);
    }

    @Override
    public ExamPaper generateAdaptivePaper(Integer userId, AdaptivePaperRuleVo ruleVo) {
        Integer recommendedDifficulty = calculateRecommendedDifficulty(userId, ruleVo.getCourseId());
        int actualDifficulty = ruleVo.getEnableAdaptiveDifficulty() ? recommendedDifficulty : ruleVo.getTargetDifficulty();

        ExamPaper examPaper = new ExamPaper();
        examPaper.setTitle(ruleVo.getPaperTitle());
        examPaper.setCourseId(ruleVo.getCourseId());
        examPaper.setTotalScore(0f);
        examPaper.setCreatedAt(LocalDateTime.now());
        examPaper.setUpdatedAt(LocalDateTime.now());

        List<ExamQuestion> selectedQuestions = new ArrayList<>();
        float totalScore = 0f;

        if (ruleVo.getSingleChoiceCount() != null && ruleVo.getSingleChoiceCount() > 0) {
            List<Integer> singleChoiceIds = selectQuestionsByDifficulty(
                    ruleVo.getCourseId(), actualDifficulty, ruleVo.getSingleChoiceCount());
            for (Integer questionId : singleChoiceIds) {
                ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                Question q = questionMapper.selectById(questionId);
                if (q != null) {
                    eq.setScore(q.getScore());
                    totalScore += q.getScore();
                }
                selectedQuestions.add(eq);
            }
        }

        if (ruleVo.getMultipleChoiceCount() != null && ruleVo.getMultipleChoiceCount() > 0) {
            List<Integer> multipleChoiceIds = selectQuestionsByDifficulty(
                    ruleVo.getCourseId(), actualDifficulty, ruleVo.getMultipleChoiceCount());
            for (Integer questionId : multipleChoiceIds) {
                ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                Question q = questionMapper.selectById(questionId);
                if (q != null) {
                    eq.setScore(q.getScore());
                    totalScore += q.getScore();
                }
                selectedQuestions.add(eq);
            }
        }

        if (ruleVo.getJudgmentCount() != null && ruleVo.getJudgmentCount() > 0) {
            List<Integer> judgmentIds = selectQuestionsByDifficulty(
                    ruleVo.getCourseId(), actualDifficulty, ruleVo.getJudgmentCount());
            for (Integer questionId : judgmentIds) {
                ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                Question q = questionMapper.selectById(questionId);
                if (q != null) {
                    eq.setScore(q.getScore());
                    totalScore += q.getScore();
                }
                selectedQuestions.add(eq);
            }
        }

        if (ruleVo.getCompletionCount() != null && ruleVo.getCompletionCount() > 0) {
            List<Integer> completionIds = selectQuestionsByDifficulty(
                    ruleVo.getCourseId(), actualDifficulty + 1, ruleVo.getCompletionCount());
            for (Integer questionId : completionIds) {
                ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                Question q = questionMapper.selectById(questionId);
                if (q != null) {
                    eq.setScore(q.getScore());
                    totalScore += q.getScore();
                }
                selectedQuestions.add(eq);
            }
        }

        if (ruleVo.getSubjectiveCount() != null && ruleVo.getSubjectiveCount() > 0) {
            List<Integer> subjectiveIds = selectQuestionsByDifficulty(
                    ruleVo.getCourseId(), actualDifficulty, ruleVo.getSubjectiveCount());
            for (Integer questionId : subjectiveIds) {
                ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                Question q = questionMapper.selectById(questionId);
                if (q != null) {
                    eq.setScore(q.getScore());
                    totalScore += q.getScore();
                }
                selectedQuestions.add(eq);
            }
        }

        examPaper.setTotalScore(totalScore);

        Map<Integer, Integer> knowledgePointReq = ruleVo.getKnowledgePointRequirements();
        if (knowledgePointReq != null && !knowledgePointReq.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : knowledgePointReq.entrySet()) {
                Integer tagId = entry.getKey();
                Integer requiredCount = entry.getValue();
                List<Integer> tagQuestions = selectQuestionsByKnowledgePoint(
                        ruleVo.getCourseId(), tagId, actualDifficulty, requiredCount);
                for (Integer questionId : tagQuestions) {
                    boolean alreadySelected = selectedQuestions.stream()
                            .anyMatch(q -> q.getQuestionId().equals(questionId));
                    if (!alreadySelected) {
                        ExamQuestion eq = createExamQuestion(questionId, selectedQuestions.size() + 1);
                        Question q = questionMapper.selectById(questionId);
                        if (q != null) {
                            eq.setScore(q.getScore());
                            totalScore += q.getScore();
                        }
                        selectedQuestions.add(eq);
                    }
                }
            }
            examPaper.setTotalScore(totalScore);
        }

        return examPaper;
    }

    @Override
    public Integer calculateRecommendedDifficulty(Integer userId, Integer courseId) {
        StudentAbilityProfile profile = abilityProfileMapper.findByUserAndCourse(userId, courseId);

        if (profile == null) {
            return 3;
        }

        double accuracyRate = profile.getAccuracyRate();
        if (accuracyRate >= 90) {
            return 5;
        } else if (accuracyRate >= 80) {
            return 4;
        } else if (accuracyRate >= 70) {
            return 3;
        } else if (accuracyRate >= 60) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public List<Integer> selectQuestionsByDifficulty(Integer courseId, Integer difficulty, Integer count) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getCourseId, courseId)
                .eq(Question::getDifficulty, difficulty)
                .in(Question::getType, QuestionTypeEnum.SIGNAL_CHOICE, QuestionTypeEnum.MULTIPLE_CHOICE,
                        QuestionTypeEnum.JUDGMENTAL, QuestionTypeEnum.COMPLETION, QuestionTypeEnum.SUBJECTIVE)
                .orderByDesc(Question::getId)
                .last("LIMIT " + count);

        List<Question> questions = questionMapper.selectList(wrapper);
        if (questions.size() < count) {
            int additionalCount = count - questions.size();
            LambdaQueryWrapper<Question> additionalWrapper = new LambdaQueryWrapper<>();
            additionalWrapper.eq(Question::getCourseId, courseId)
                    .between(Question::getDifficulty, Math.max(1, difficulty - 1), Math.min(5, difficulty + 1))
                    .notIn(Question::getId, questions.stream().map(Question::getId).collect(Collectors.toList()))
                    .orderByDesc(Question::getId)
                    .last("LIMIT " + additionalCount);
            List<Question> additionalQuestions = questionMapper.selectList(additionalWrapper);
            questions.addAll(additionalQuestions);
        }

        Collections.shuffle(questions);
        return questions.stream().limit(count).map(Question::getId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> selectQuestionsByKnowledgePoint(Integer courseId, Integer tagId, Integer difficulty, Integer count) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getCourseId, courseId)
                .eq(Question::getTagId, tagId)
                .eq(Question::getDifficulty, difficulty)
                .orderByDesc(Question::getId)
                .last("LIMIT " + count);

        List<Question> questions = questionMapper.selectList(wrapper);
        if (questions.size() < count) {
            int additionalCount = count - questions.size();
            LambdaQueryWrapper<Question> additionalWrapper = new LambdaQueryWrapper<>();
            additionalWrapper.eq(Question::getCourseId, courseId)
                    .eq(Question::getTagId, tagId)
                    .between(Question::getDifficulty, Math.max(1, difficulty - 1), Math.min(5, difficulty + 1))
                    .notIn(Question::getId, questions.stream().map(Question::getId).collect(Collectors.toList()))
                    .orderByDesc(Question::getId)
                    .last("LIMIT " + additionalCount);
            List<Question> additionalQuestions = questionMapper.selectList(additionalWrapper);
            questions.addAll(additionalQuestions);
        }

        Collections.shuffle(questions);
        return questions.stream().limit(count).map(Question::getId).collect(Collectors.toList());
    }

    private ExamQuestion createExamQuestion(Integer questionId, Integer sort) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestionId(questionId);
        examQuestion.setSort(sort);
        examQuestion.setCreatedAt(LocalDateTime.now());
        examQuestion.setUpdatedAt(LocalDateTime.now());
        return examQuestion;
    }

    private List<ExamAnswerResult> getAnswerResults(Integer userId, Integer courseId) {
        LambdaQueryWrapper<ExamAnswerResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswerResult::getUserId, userId);
        return answerResultMapper.selectList(wrapper);
    }

    private int calculateAbilityLevel(double accuracyRate) {
        if (accuracyRate >= 90) return 5;
        if (accuracyRate >= 80) return 4;
        if (accuracyRate >= 70) return 3;
        if (accuracyRate >= 60) return 2;
        return 1;
    }

    private List<AbilityProfileVo.KnowledgePoint> buildKnowledgePointMastery(Integer userId, Integer courseId) {
        List<AbilityProfileVo.KnowledgePoint> masteryList = new ArrayList<>();

        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Question::getCourseId, courseId);
        List<Question> questions = questionMapper.selectList(questionWrapper);

        Map<Integer, List<Question>> questionsByTag = questions.stream()
                .collect(Collectors.groupingBy(q -> q.getTagId() != null ? q.getTagId() : 0));

        for (Map.Entry<Integer, List<Question>> entry : questionsByTag.entrySet()) {
            Integer tagId = entry.getKey();
            List<Question> tagQuestions = entry.getValue();

            AbilityProfileVo.KnowledgePoint kp = new AbilityProfileVo.KnowledgePoint();
            kp.setTagId(tagId);
            kp.setQuestionCount(tagQuestions.size());

            Tags tag = null;
            if (tagId > 0) {
                LambdaQueryWrapper<Tags> tagWrapper = new LambdaQueryWrapper<>();
                tagWrapper.eq(Tags::getId, tagId);
                tag = tagMapper.selectOne(tagWrapper);
            }
            kp.setTagName(tag != null ? tag.getTag() : "未分类");

            masteryList.add(kp);
        }

        return masteryList;
    }

    private String analyzePerformanceTrend(Integer userId, Integer courseId) {
        return "stable";
    }
}