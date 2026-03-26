package com.baymax.exam.center.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HanLPTextSimilarity {

    public List<String> extractKeywords(String text, int topK) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return HanLP.extractKeyword(text, topK);
    }

    public List<String> extractPhrase(String text, int topK) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return HanLP.extractPhrase(text, topK);
    }

    public double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }
        if (text1.trim().isEmpty() || text2.trim().isEmpty()) {
            return 0.0;
        }

        List<String> keywords1 = extractKeywords(text1, 10);
        List<String> keywords2 = extractKeywords(text2, 10);

        if (keywords1.isEmpty() || keywords2.isEmpty()) {
            return calculateCharacterLevelSimilarity(text1, text2);
        }

        Set<String> set1 = new HashSet<>(keywords1);
        Set<String> set2 = new HashSet<>(keywords2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        double jaccardSimilarity = (double) intersection.size() / union.size();

        double weight1 = (double) intersection.size() / set1.size();
        double weight2 = (double) intersection.size() / set2.size();
        double coverageScore = (weight1 + weight2) / 2.0;

        return jaccardSimilarity * 0.6 + coverageScore * 0.4;
    }

    private double calculateCharacterLevelSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        List<Term> terms1 = HanLP.segment(text1);
        List<Term> terms2 = HanLP.segment(text2);

        if (terms1.isEmpty() || terms2.isEmpty()) {
            return 0.0;
        }

        Set<String> wordSet1 = new HashSet<>();
        Set<String> wordSet2 = new HashSet<>();

        for (Term term : terms1) {
            if (term.word.length() > 1) {
                wordSet1.add(term.word);
            }
        }

        for (Term term : terms2) {
            if (term.word.length() > 1) {
                wordSet2.add(term.word);
            }
        }

        Set<String> intersection = new HashSet<>(wordSet1);
        intersection.retainAll(wordSet2);

        double jaccard = (double) intersection.size() / (wordSet1.size() + wordSet2.size() - intersection.size());
        return jaccard;
    }

    public Map<String, Object> analyzeText(String text) {
        Map<String, Object> result = new HashMap<>();
        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        result.put("keywords", extractKeywords(text, 10));
        result.put("phrases", extractPhrase(text, 5));

        List<Term> terms = HanLP.segment(text);
        int wordCount = 0;
        int charCount = text.length();
        for (Term term : terms) {
            if (term.word.length() > 1) {
                wordCount++;
            }
        }
        result.put("wordCount", wordCount);
        result.put("charCount", charCount);
        result.put("terms", terms.stream().map(t -> t.word).toArray());

        return result;
    }

    public double calculateScore(String standardAnswer, String studentAnswer, float maxScore) {
        if (standardAnswer == null || studentAnswer == null) {
            return 0.0;
        }

        double similarity = calculateSimilarity(standardAnswer, studentAnswer);

        List<String> standardKeywords = extractKeywords(standardAnswer, 5);
        List<String> studentKeywords = extractKeywords(studentAnswer, 5);

        Set<String> standardSet = new HashSet<>(standardKeywords);
        Set<String> studentSet = new HashSet<>(studentKeywords);

        int matchedKeywords = 0;
        for (String keyword : standardSet) {
            if (studentSet.contains(keyword)) {
                matchedKeywords++;
            }
        }

        double keywordScore = standardSet.isEmpty() ? 0.0 :
                (double) matchedKeywords / standardSet.size();

        double finalScore = (similarity * 0.7 + keywordScore * 0.3);

        return Math.round(finalScore * maxScore * 100.0) / 100.0;
    }

    public String generateFeedback(String standardAnswer, String studentAnswer, float maxScore) {
        double similarity = calculateSimilarity(standardAnswer, studentAnswer);
        double score = calculateScore(standardAnswer, studentAnswer, maxScore);
        double percentage = (score / maxScore) * 100;

        List<String> standardKeywords = extractKeywords(standardAnswer, 5);
        List<String> studentKeywords = extractKeywords(studentAnswer, 5);

        Set<String> standardSet = new HashSet<>(standardKeywords);
        Set<String> missingKeywords = new HashSet<>(standardSet);
        missingKeywords.removeAll(new HashSet<>(studentKeywords));

        StringBuilder feedback = new StringBuilder();
        feedback.append(String.format("本题得分: %.2f/%d (%.1f%%)\n", score, maxScore, percentage));

        if (percentage >= 90) {
            feedback.append("答案准确，表达清晰，知识点掌握良好。");
        } else if (percentage >= 70) {
            feedback.append("答案基本正确，但对某些关键知识点的理解还不够深入。");
            if (!missingKeywords.isEmpty()) {
                feedback.append(String.format("建议加强对以下知识点的理解: %s", String.join("、", missingKeywords)));
            }
        } else if (percentage >= 50) {
            feedback.append("答案存在明显偏差，建议重新学习相关知识点。");
            if (!missingKeywords.isEmpty()) {
                feedback.append(String.format("缺失的关键知识点: %s", String.join("、", missingKeywords)));
            }
        } else {
            feedback.append("答案偏离主题或存在重大错误，需要认真复习相关内容。");
            feedback.append(String.format("标准答案关键词: %s", String.join("、", standardKeywords)));
        }

        return feedback.toString();
    }
}