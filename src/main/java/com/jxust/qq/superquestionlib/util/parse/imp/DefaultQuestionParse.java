package com.jxust.qq.superquestionlib.util.parse.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.util.QuestionTypeEnum;
import com.jxust.qq.superquestionlib.util.parse.fileparse.DocxParse;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认的QuestionParse是读取Docx文本内容并且分解出题目
 */
public class DefaultQuestionParse extends AbstractQuestionParse {

    private final Pattern QUESTION_TYPE_PATTERN = Pattern.compile("[一二三四五六][、.](选择题|填空题|解答题|判断题)");
    private final Pattern QUESTION_NUMBER_PATTERN = Pattern.compile("\\d+[.、][\\S\\s]*?(?=(\\n+\\d+[.、]))|\\d+[.、][\\s\\S]*");
    private final Pattern QUESTION_OPTION_PATTERN = Pattern.compile("(?<=\\n)[A-Z]([.|、][\\s|\\S]+?)(?=(\\d+[.|、]|答案)|$)");
    private final Pattern QUESTION_SINGLE_OPTION_PATTERN = Pattern.compile("[A-Z][.|、][\\s|\\S]+?(?=([A-Z][.|、])|\\n)");
    private final Pattern QUESTION_STEM_PATTERN = Pattern.compile("\\d+([.、])[\\S\\s]+?(?=((答案[:：])|(解答[:：])|([A-Z][.、])))");
    private final Pattern QUESTION_ANSWER_PATTERN = Pattern.compile("((答案)|(解答))[：:.、][\\s\\S]+");

    public DefaultQuestionParse(String filepath) throws IOException {
        this.filepath = filepath;
        this.parse = new DocxParse();
        this.hasAnswer = false;
        init();
    }

    public DefaultQuestionParse(String filepath, boolean hasAnswer) throws IOException {
        this(filepath);
        this.hasAnswer = hasAnswer;
    }

    @Override
    public List<Question> parseQuestions() {
        List<QuestionPart> questionPartList = divideContent();
        List<Question> questionList = new ArrayList<>();
        questionPartList.forEach(part -> {
            List<String> questionContents = dividePartContent(part);
            List<Question> tempQuestions = new ArrayList<>();
            for (String questionContent : questionContents) {
                Question question = new Question();
                String questionJsonInfo = convertQuestionInfoToJson(questionContent, part.type);
                question.setQuestionContent(questionJsonInfo);
                tempQuestions.add(question);
            }
            questionList.addAll(tempQuestions);
        });
        return questionList;
    }


    private String convertQuestionInfoToJson(String questionContent, QuestionTypeEnum typeEnum) {
        JSONObject data = new JSONObject();
        String stem = divideQuestionStem(questionContent, typeEnum);
        data.put("title", stem);
        data.put("type", typeEnum.getId());
        if (typeEnum == QuestionTypeEnum.TYPEONE) {
            Map<String, Object> optionMap = divideOptionContent(questionContent);
            JSONObject options = new JSONObject(optionMap);
            data.put("options", options);
        }
        if (hasAnswer) {
            String answer = divideAnswerFromContent(questionContent);
            data.put("answer", answer);
        } else {
            data.put("answer", "");
        }
        return JSON.toJSONString(data, SerializerFeature.WriteMapNullValue);
    }


    /**
     * 将每一个part内的content分解为单个的题目
     * @param part 某一个题型的全部content持有者
     * @return 分解出单一的题目
     */
    private List<String> dividePartContent(QuestionPart part) {
        List<String> questionContents = new ArrayList<>();
        Matcher numberMatcher = QUESTION_NUMBER_PATTERN.matcher(part.getContent());
        while (numberMatcher.find()) {
            questionContents.add(numberMatcher.group());
        }
        return questionContents;
    }

    /**
     * 从一个题目里面匹配出题干信息
     * @param partContent 题目信息（可能包含答案或者是选项等）
     * @return List 题干信息
     */
    private String divideQuestionStem(String partContent, QuestionTypeEnum type) {
        // 当给定题目信息不是选择题并且不包含答案时可以直接保存
        if (type != QuestionTypeEnum.TYPEONE && !hasAnswer) {
            return partContent;
        }
        if (type != QuestionTypeEnum.TYPEONE) {
            // 提取题干的标志
            String STEM_FLAG = "答案:";
            partContent += STEM_FLAG;
        }
        Matcher stemMatcher = QUESTION_STEM_PATTERN.matcher(partContent);
        if (stemMatcher.find()) {
            return stemMatcher.group();
        }
        return null;
    }

    /**
     * 选择题->获取到单个的选项
     * @param partContent 题目信息->包含多个选项糅合在一起的字符串
     * @return map
     */
    private Map<String, Object> divideOptionContent(String partContent) {
        Matcher optionMatcher = QUESTION_OPTION_PATTERN.matcher(partContent);
        if (optionMatcher.find()) {
            String options = optionMatcher.group();
            return divideIntegralOption(options);
        }
        return new HashMap<>();
    }


    /**
     * 分解出一个选择题所有的选项
     * @param options 选择的题选项
     * @return map => {"A":"xx", "B":"xx"}
     */
    private Map<String, Object> divideIntegralOption(String options) {
        // 拼接一个换行符,以便可以匹配最后一个选项
        if (!options.endsWith("\n")) {
            options += "\n";
        }
        Matcher singleOptionMatcher = QUESTION_SINGLE_OPTION_PATTERN.matcher(options);
        Map<String, Object> optionMap = new HashMap<>();
        while (singleOptionMatcher.find()) {
            String option = singleOptionMatcher.group();
            String prefix = option.substring(0, 1);
            option = option.replaceFirst("[A-Z][.、]", "");
            optionMap.putIfAbsent(prefix, option);
        }
        return optionMap;
    }

    /**
     * 从一个单个的题目中分解出答案(如果存在的话)
     * @param content 单个的题干信息
     * @return 答案
     */
    private String divideAnswerFromContent(String content) {
        Matcher answerMatch = QUESTION_ANSWER_PATTERN.matcher(content);
        if (answerMatch.find()) {
            return answerMatch.group().replaceFirst("((答案:)|(解答:)|(答案：)|(解答：))|(答案.)|(解答.)", "");
        }
        return "";
    }

    /**
     * todo 待删除
     * test函数入口
     * @return parts
     */
    public List<QuestionPart> showParts() {
        return divideContent();
    }

    /**
     * todo 测试
     */
    public String showContent() {
        return content;
    }

    /**
     * todo test
     */
    public List<String> showDividePart(QuestionPart part) {
        return dividePartContent(part);
    }

    private List<QuestionPart> divideContent() {
        Matcher typeMatcher = QUESTION_TYPE_PATTERN.matcher(content);
        List<QuestionPart> questionPartList = new ArrayList<>();
        while (typeMatcher.find()) {
            QuestionPart part = new QuestionPart();
            String match = typeMatcher.group();
            part.setType(getTypeEndsWithMatch(match));
            part.setCursor(typeMatcher.start(), typeMatcher.end());
            questionPartList.add(part);
        }
        setPartContent(questionPartList);
        return questionPartList;
    }

    /**
     * 根据匹配到的内容确定所属的题型
     * @param match 匹配到的字符串 如:1.选择题, 2.填空题 ....
     * @return type
     */
    private QuestionTypeEnum getTypeEndsWithMatch(String match) {
        for (QuestionTypeEnum type : QuestionTypeEnum.values()) {
            if (match.endsWith(type.getMark())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据start与end标志,填充part的content内容
     * 这里的<p>start<p/>与</p>end</p>是题型标志的"一.选择题"的开始index与结束index
     * 而真正的具体content则是每两个的part的[current.end, next.start]的中间内容
     * @param partList 匹配得到的parts
     */
    private void setPartContent(List<QuestionPart> partList) {
        for (int i = 0; i < partList.size(); i++) {
            QuestionPart current = partList.get(i);
            if (i < partList.size() - 1) {
                current.setContent(
                        content.substring(current.getEnd() + 1, partList.get(i + 1).getStart() - 1)
                );
            } else {
                // 当不存在next的时候,直接将end设为整个content的尾部index
                current.setContent(content.substring(current.getEnd() + 1));
            }
        }
    }

    @Data
    static class QuestionPart {
        String content;
        int start;
        int end;
        QuestionTypeEnum type;
        public void setCursor(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
