package com.jxust.qq.superquestionlib.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.exception.QuestionException;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.util.QuestionBreakUtil;
import com.jxust.qq.superquestionlib.util.QuestionTypeEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultBreakQuestion {
    @Getter
    @Setter
    private String filepath;
    // 题库中是否包含答案
    private boolean hasAnswer = true;
    @Getter
    private boolean isComplete = true;
    private Pattern titleRegex = Pattern.compile("[一|二|三|四|五|六]、(选择题|填空题|解答题|判断题)");
    /**
     * TODO 选择题的正则表达式 : 暂时完成初始版本 -> 待多样本检测
     * TODO 填空题的正则表达式 :
     * TODO 判断题的正则表达式
     */
    // 解析出来每一个题目的题干,
    // TODO 待解决,没有考虑到多行题目的情况,(word解析好像都是解析成一行？)
    private Pattern questionTitleRegex = Pattern.compile("\\d+([.、]).+");
    //解析单个题目的全部选项，随后再对单个题目的全部选项进行解析，可以防止出现题目选项数量不一致的情况
    private Pattern choseOptionsRegex = Pattern.compile("(?<=\\n)[A-Z]([.|、][\\s|\\S]+?)(?=(\\d+[.|、]|答案)|\\s$)");
    //解析出来单个题目的全部选项中每一个单个选项
    private Pattern optionRegex = Pattern.compile("[A-Z][.|、][\\s|\\S]+?(?=([A-Z][.|、])|\\s$)");
    // 匹配单独列出来的答案的形式：例如-> 答案：ABC|答案、ABC|答案  ABC
    private Pattern choseAnswerRegexOne = Pattern.compile("(?<=\\n答案[:|：|\\s|、]\\s{0,5})[A-Z]+");
    // 匹配题干中的答案例如：(A)|(AB)|(ABC)等
    private Pattern choseAnswerRegexTwo = Pattern.compile("(?<=\\()[A-Z]+(?=\\))");
    private Pattern fillAnswerRegexOne = Pattern.compile("(?<=\\n答案[:|：|\\s|、]\\s{0,5}).*(?=(\\d+[.|、])|\\s$)");
    private Pattern judgeAnswerRegex = Pattern.compile("(?<=\\n答案[:|：|\\s|、]\\s{0,5})[对|错]+");

    public DefaultBreakQuestion(String filepath) {
        this.filepath = filepath;
    }

    public DefaultBreakQuestion(String filepath, boolean flag) {
        this.filepath = filepath;
        this.hasAnswer = flag;
    }

    /**
     * TODO 分解题目实现类
     * @return List<Question>
     */
    public List<Question> breakQuestion() throws IllegalArgumentException, IOException{
        assert filepath != null;
        String text = QuestionBreakUtil.init(filepath);
        List<QuestionPoint> pointList = compileTitle(text);
        List<Question> questionList = new ArrayList<>();
        pointList.forEach(point -> {
            switch (point.typeEnum) {
                case TYPEONE:
                    questionList.addAll(breakTypeOneQuestion(point));
                    break;
                case TYPETWO:
                    questionList.addAll(breakTypeTwoQuestion(point));
                    break;
                case TYPETHREE:
                    questionList.addAll(breakTypeThreeQuestion(point));
                    break;
                case TYPEFOUR:

                    break;
                default:
                    break;
            }
        });
        return questionList;
    }


    /**
     * 分解判断题的题型
     * @param point
     * @return
     */
    private List<? extends Question> breakTypeThreeQuestion(QuestionPoint point) {
        List<String> contentList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();
        List<String> answerList = new ArrayList<>();
        String contents = point.getText();
        Matcher titleMatcher = questionTitleRegex.matcher(contents);
        Matcher answerMatcher = judgeAnswerRegex.matcher(contents);
        while (true) {
            if (titleMatcher.find()) {
                contentList.add(titleMatcher.group());
            }else  if (answerMatcher.find()){
                answerList.add(answerMatcher.group());
            }else {
                break;
            }
        }
        if (contentList.size() != answerList.size() && hasAnswer) {
            isComplete = false;
        }
        contentList.forEach(content -> {
            Question judgeQuestion = new Question();
            JSONObject data = new JSONObject();
            data.put("title", content);
            data.put("type", QuestionTypeEnum.TYPETHREE.getId());
            data.put("answer", null);
            judgeQuestion.setQuestionContent(data.toJSONString());
            judgeQuestion.setQuestionTypeId(QuestionTypeEnum.TYPETHREE.getId());
            questionList.add(judgeQuestion);
        });
        return questionList;
    }

    /**
     * 分解出TypeOne类型即选择题的题目
     * @param point 详见{@code QuestionPoint}
     * @return list
     * @throws IllegalArgumentException 若出现解析不一致，例如解析出15道题干,14道题目选项这种情况，需要进行后续处理，可以通知用户进行检查，
     * 并且自行添加缺失的选项等
     */
    @NotNull
    private List<Question> breakTypeOneQuestion(QuestionPoint point) throws IllegalArgumentException, QuestionException {
        String content = point.getText();
        List<Question> questionList = new ArrayList<>();
        Matcher choseMatcher = questionTitleRegex.matcher(content);
        Matcher optionMatcher = choseOptionsRegex.matcher(content);
        Matcher answerMatcher = choseAnswerRegexOne.matcher(content);
        List<String> contents = new ArrayList<>();
        List<String> options = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        while (true) {
            // TODO 改进,改成多线程执行
            if (choseMatcher.find()) {
                contents.add(choseMatcher.group());
            }else if (optionMatcher.find()) {
                options.add(optionMatcher.group());
                // 当题库中有答案的时候再解析答案
            }else if (answerMatcher.find() && hasAnswer){
                answers.add(answerMatcher.group());
            }else {
                break;
            }
        }
        if (contents.size() != options.size()) {
            isComplete = false;
        }
        if (contents.size() != answers.size() && hasAnswer) {
            isComplete = false;
        }
        // 将题干和选项结合进行题目的组装
        List<Map<String ,String>> mapList = getSingleOption(options);
        JSONArray jsonData = buildTypeOneQuestionContent(contents, mapList, answers);
        jsonData.forEach(it->{
            if (it instanceof JSONObject) {
                Question question = new Question();
                question.setQuestionContent(((JSONObject) it).toJSONString());
                question.setQuestionTypeId(QuestionTypeEnum.TYPEONE.getId());
                questionList.add(question);
            }
        });
        return questionList;
    }


    /**
     * 分解出TypeTwo类型即填空题的题目
     * @param point 详见{@code QuestionPoint}
     * @return List
     */
    @NotNull
    private List<Question> breakTypeTwoQuestion(QuestionPoint point) {
        String content = point.getText();
        Matcher titleMatcher = questionTitleRegex.matcher(content);
        Matcher answerMatcher = fillAnswerRegexOne.matcher(content);
        List<String> titles = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        while (true) {
            if (titleMatcher.find()) {
                titles.add(titleMatcher.group());
            }else if (answerMatcher.find()) {
                answers.add(answerMatcher.group());
            }else {
                break;
            }
        }
        if (titles.size() != answers.size() && hasAnswer) {
            isComplete = false;
        }
        // todo 添加答案
        JSONArray questionArray = buildTypeTwoQuestion(titles, answers);
        List<Question> questionList = new ArrayList<>();
        questionArray.forEach(ob->{
            if (ob instanceof JSONObject) {
                Question question = new Question();
                question.setQuestionTypeId(QuestionTypeEnum.TYPETWO.getId());
                question.setQuestionContent(((JSONObject) ob).toJSONString());
                questionList.add(question);
            }
        });
        return questionList;
    }

    /**
     * 构建填空题的JSONArray
     * @param titles 题干list
     * @param answers 答案list
     * @return 包含全部选择题的JSONArray
     */
    private JSONArray buildTypeTwoQuestion(List<String> titles, List<String> answers) {
        Iterator<String> titleIter = titles.iterator();
        Iterator<String> answerIter = answers.iterator();
        JSONArray array = new JSONArray();
        while (titleIter.hasNext()) {
            JSONObject ob = new JSONObject();
            boolean answerFlag = answerIter.hasNext();
            String title = titleIter.next();
            ob.put("type", QuestionTypeEnum.TYPETWO.getId());
            ob.put("title", title);
            ob.put("answer", answerFlag?answerIter.next():"");
            array.add(ob);
        }
        return array;
    }

    /**
     * 解析出每一道题目与题干的json数组
     * 将每一个题干与题目结合解析成如下格式的json封装成array
     * {
     * 	"options":
     * 	{
     * 	"A": " surround",
     * 	"B": " substance",
     * 	"C": " stretch",
     * 	"D": " substitute"
     *  },
     * 	"type": 1,
     * 	"title": "1. When making modern cameras, people began to _______ plastics for metal"
     * }
     * @param questions 题干
     * @param allOptions 题目选项
     * @return JSONArray
     */
    private JSONArray buildTypeOneQuestionContent(List<String> questions, List<Map<String ,String>> allOptions, List<String> answers) {
        JSONArray array = new JSONArray();
        Iterator<String> question = questions.iterator();
        Iterator<String> answer = answers.iterator();
        Iterator<Map<String,String>> options = allOptions.iterator();
        while (question.hasNext()) {
            // 容错--如果不存在的话则存入空的数据
            boolean answerFlag = answer.hasNext();
            boolean optionFlag = options.hasNext();
            JSONObject data = new JSONObject();
            JSONObject optionData = new JSONObject();
            data.put("type", QuestionTypeEnum.TYPEONE.getId());
            data.put("title", question.next());
            optionData.putAll(optionFlag?options.next():new HashMap<>());
            data.put("options", optionData);
            // 当存在答案并且解析到答案的时候存入答案，否则的话存入空字符串""
            data.put("answer", answerFlag && hasAnswer?answer.next():"");
            array.add(data);
        }
        return array;
    }

    /**
     * 将单个题目的完整选项解析成多个单个选项
     * 如：A. rational   B. recreational   C. radical   D. recurrent
     * 解析后：
     * {
     *     "A": " surround",
     * 	   "B": " substance",
     * 	   "C": " stretch",
     * 	   "D": " substitute"
     * }
     * @param options 待分解的整体选项
     * @return 分解完毕的每一个选项组
     */
    private List<Map<String, String>> getSingleOption(List<String> options) {
        List<Map<String, String>> optionMaps = new ArrayList<>();
        options.forEach(option->{
            //先去除前后的空格，对于最后一个要加上空格
            option = option.trim();
            option += "  ";
            Matcher matcher = optionRegex.matcher(option);
            Map<String, String> map = new HashMap<>();
            while (matcher.find()) {
                String op = matcher.group();
                String title = op.substring(0, 1);
                String content = op.substring(1);
                //去掉选项中的.或者、
                content = content.replaceFirst("[.|、]", "");
                map.put(title, content);
            }
            optionMaps.add(map);
        });
        return optionMaps;
    }

    /**
     * 根据title取出相应的题目，做好标记（题目类型的内容在整个文本中的起始点与终止点）以及相应的文本段
     * eg:已经通过english.docx的简单的单元测试
     * TODO：需要进行更多的题库的测试
     * @param text 完整的一个题库
     * @return List<Point>
     */
    private List<QuestionPoint> compileTitle(String text) {
        Matcher titleMatcher = titleRegex.matcher(text);
        List<QuestionPoint> points = new ArrayList<>();
        while (titleMatcher.find()) {
            String titleName = titleMatcher.group();
            QuestionPoint point = new QuestionPoint();
            int start = titleMatcher.start();
            int end = titleMatcher.end();
            if (titleName.endsWith(QuestionTypeEnum.TYPEONE.getMark())) {
                point.setTypeEnum(QuestionTypeEnum.TYPEONE);
            }else if (titleName.endsWith(QuestionTypeEnum.TYPETWO.getMark())) {
                point.setTypeEnum(QuestionTypeEnum.TYPETWO);
            }else if (titleName.endsWith(QuestionTypeEnum.TYPETHREE.getMark())) {
                point.setTypeEnum(QuestionTypeEnum.TYPETHREE);
            }else if (titleName.endsWith(QuestionTypeEnum.TYPEFOUR.getMark())) {
                point.setTypeEnum(QuestionTypeEnum.TYPEFOUR);
            }
            point.setStart(start);
            point.setEnd(end);
            points.add(point);
        }
        changePoint(points, text);
        return points;
    }

    /**
     * 修改point，匹配相应的内容，记录起始点与终止点
     * eg:修改后为
     * {
     *  start:2,
     *  end:1000,
     *  name:"选择题",
     *  text:
     *  "
     *      1.xxx
     *       A. B. C. D.
     *      2.xxx
     *       A. B. C. D.
     *  "
     * }
     * @param pointList 待添加相应的待解析的内容的Point
     * @param text 原文本
     */
    private void changePoint(List<QuestionPoint> pointList, String text) {
        for (int i = 0; i < pointList.size(); i++) {
            QuestionPoint point = pointList.get(i);
            if (i < pointList.size() - 1){
                point.setStart(point.getEnd());
                point.setEnd(pointList.get(i + 1).start - 1);
            }else {
                point.setStart(pointList.get(i).getEnd());
                point.setEnd(text.length() - 1);
            }
            point.setText(text.substring(point.start, point.end));
        }
    }

    @Data
    static class QuestionPoint {
        private int start;
        private int end;
        private QuestionTypeEnum typeEnum;
        private String text;
    }
}
