package com.jxust.qq.superquestionlib.service.admin;

import com.alibaba.fastjson.JSONObject;
import com.jxust.qq.superquestionlib.dao.mapper.QuestionLibMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsQuestionLibMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.EsQuestionMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.UtilMapper;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsLibTagRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionLibRepository;
import com.jxust.qq.superquestionlib.dao.mapper.admin.repository.EsQuestionRepository;
import com.jxust.qq.superquestionlib.dto.Question;
import com.jxust.qq.superquestionlib.dto.QuestionLib;
import com.jxust.qq.superquestionlib.dto.admin.EsLibTag;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestion;
import com.jxust.qq.superquestionlib.dto.admin.EsQuestionLib;
import com.jxust.qq.superquestionlib.service.DefaultBreakQuestion;
import com.jxust.qq.superquestionlib.service.QuestionService;
import com.jxust.qq.superquestionlib.service.UserService;
import com.jxust.qq.superquestionlib.vo.QuestionLibVO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EsQuestionLibService
{
    @Autowired
    EsQuestionLibRepository questionLibRepository;
    @Autowired
    EsQuestionRepository questionRepository;
    @Autowired
    EsLibTagRepository libTagRepository;
    private final static String FILE_DIR = "/home/jiangzeen/project_log/upload_question_lib/";
    private final static String FILE_DIR1 = "D:/demo/";
    private final EsQuestionLibMapper questionLibMapper;
    private final EsQuestionMapper questionMapper;
    private final UtilMapper utilMapper;
    private final QuestionLibMapper libMapper;
    private final UserService userService;
    public static int pagesum;

    public EsQuestionLibService(EsQuestionLibMapper questionLibMapper, EsQuestionMapper questionMapper, UtilMapper utilMapper, QuestionLibMapper libMapper, UserService userService) {
        this.questionLibMapper = questionLibMapper;
        this.questionMapper = questionMapper;
        this.utilMapper = utilMapper;
        this.libMapper = libMapper;
        this.userService = userService;
    }

    //进行模糊匹配查找
    public List<EsQuestionLib> matchQuestionLib(String queryString, int pagenum, int pagesize)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty())
        {
        MultiMatchQueryBuilder matchQuery= QueryBuilders.multiMatchQuery(queryString,
                "questionLibName","questionLibMark");
        boolQueryBuilder.filter(matchQuery);
        }
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        Iterable<EsQuestionLib> questionLibs=questionLibRepository.search(boolQueryBuilder,pageRequest);
        pagesum= (int) questionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        List<EsQuestionLib> questionLibList=new ArrayList<>();
        for (EsQuestionLib questionLib : questionLibs) {
            if (libTagRepository.existsById((long)questionLib.getQuestionLibTagId())) {
                libTagRepository.findById((long) questionLib.getQuestionLibTagId());
                questionLib.setLibTagInfo(libTagRepository.findById((long) questionLib.getQuestionLibTagId()).get().getTagName());
            }
            questionLibList.add(questionLib);
        }
        return questionLibList;
    }
    //根据id完全匹配查找
    public EsQuestionLib termQuestionLib(int id)
    {
        EsQuestionLib questionLib=null;
        if(questionLibRepository.findById((long)id).isPresent())
        {
            questionLib=questionLibRepository.findById((long)id).get();
            pagesum=1;
        }
        else pagesum=0;
        return questionLib;
    }
    //hasPrivate为0私有,为1公有,-1不筛选  LibTagId为0不筛选
    public List<EsQuestionLib> boolQuestionLib(String queryString,int pagenum, int pagesize,int hasPrivate,int questionLibLevelBegin,
                                            int questionLibLevelEnd,int LibTagId)
    {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        if(!queryString.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(queryString, "questionLibName", "questionLibMark"));
        }
        if(hasPrivate>=0)
        {
            TermQueryBuilder termQuery=QueryBuilders.termQuery("hasPrivate",hasPrivate);
            boolQueryBuilder.filter(termQuery);
        }
        if(questionLibLevelEnd!=0)
        {
            RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery("questionLibLevel").gte(questionLibLevelBegin)
                    .lte(questionLibLevelEnd);
            boolQueryBuilder.filter(rangeQuery);
        }
        if(LibTagId!=0) {
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("parentIds", LibTagId);
            boolQueryBuilder.filter(matchQuery);
        }
        //分页
        PageRequest pageRequest=PageRequest.of(pagenum-1,pagesize);
        pagesum= (int) questionLibRepository.search(boolQueryBuilder,pageRequest).getTotalElements();
        Iterable<EsQuestionLib> iterables=questionLibRepository.search(boolQueryBuilder,pageRequest);
        Iterator<EsQuestionLib> iterator=iterables.iterator();
        ArrayList<EsQuestionLib> questionLibs=new ArrayList<>();
        while(iterator.hasNext())
        {
            EsQuestionLib questionLib=iterator.next();
            questionLibs.add(questionLib);
        }
        return questionLibs;
    }
    public long count()
    {
        return questionLibRepository.count();
    }
    //删
    public int deleteById(long id)
    {
        if(questionLibRepository.existsById(id))
            questionLibRepository.deleteById(id);
        return questionLibMapper.deleteById(id);
    }
    //修改
    public int updateById(EsQuestionLib questionLib)
    {
        int status=questionLibMapper.updateQuestionLib(questionLib);
        if(status>0)
        {
            if(libTagRepository.findById((long)questionLib.getQuestionLibTagId()).isPresent()) {
                EsLibTag libTag=libTagRepository.findById((long)questionLib.getQuestionLibTagId()).get();
                questionLib.setLibTagInfo(libTag.getTagName());
                questionLib.setParentIds(utilMapper.getTagParent(questionLib.getQuestionLibTagId()));
                questionLibRepository.save(questionLib);
            }
        }
        return status;
    }
    //增加
    public int creatQuestionLib(EsQuestionLib questionLib)
    {
        int status;
        status=questionLibMapper.createQuestionLib(questionLib);
        if(status>0)
        {
            if(libTagRepository.findById((long)questionLib.getQuestionLibTagId()).isPresent()) {
                EsLibTag libTag=libTagRepository.findById((long)questionLib.getQuestionLibTagId()).get();
                questionLib.setLibTagInfo(libTag.getTagName());
                questionLib.setParentIds(utilMapper.getTagParent(questionLib.getQuestionLibTagId()));
                questionLibRepository.save(questionLib);
            }
        }
        return status;
    }

    /**
     * 移植的方法
     */


    public int createQuestionLibByUser (String username, String libName,
                                        int tagId, String libUrl, String mark, int hasPrivate) {
        // todo 添加userQuestionLib
        EsQuestionLib lib=new EsQuestionLib();
        lib.setQuestionLibLevel(0);
        lib.setQuestionLibCreateTime(new Date());
        lib.setQuestionLibTagId(tagId);
        lib.setQuestionLibUrl(libUrl);
        lib.setQuestionLibMark(mark);
        lib.setQuestionLibName(libName);
        if(libTagRepository.existsById((long)tagId))
        lib.setLibTagInfo(libTagRepository.findById((long)tagId).get().getTagName());
        lib.setHasPrivate(hasPrivate);
        questionLibMapper.createQuestionLib(lib);
        questionLibRepository.save(lib);
        return (int) lib.getQuestionLibId();
    }

    /**
     *
     * @return
     */
    public QuestionLib findQuestionLibById(int id) {
        return libMapper.selectQuestionLibById(id);
    }

    /**
     * 保存题库文件到服务器
     * 题库文件必须以 .doc || .docx || .pdf 结尾
     * 题库文件命名:userame+"-" + lib + UUID + 后缀名
     * @param questionLibFile 题库文件
     * @param username 上传题库用户名
     */
    public String saveOriginLibFile(MultipartFile questionLibFile, String username) throws IllegalArgumentException, NullPointerException{
        if (questionLibFile == null || questionLibFile.getOriginalFilename() == null) {
            throw new NullPointerException();
        }
        log.info("获取到上传的题库文件:{}", questionLibFile.getOriginalFilename());
        String originname = questionLibFile.getOriginalFilename();
        Pattern rex = Pattern.compile(".*(.doc)$|.*(.docx)$|.*(.pdf)$");
        if (!rex.matcher(originname).find()) {
            throw new IllegalArgumentException();
        }
        String[] suffix = originname.split("//.");
        String saveName = username + "-lib-" + produceTime() + "." + suffix[suffix.length - 1];
        try {
            InputStream in = questionLibFile.getInputStream();
            FileOutputStream out = new FileOutputStream(FILE_DIR1 + saveName);
            byte[] bb = new byte[1024];
            while (in.read(bb) != -1) {
                out.write(bb);
            }
            return saveName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileUrl(String filename) {
        return FILE_DIR1 + filename;
    }


    public JSONObject createQuestionByLibFile(String filename, int libId) throws IOException {
        DefaultBreakQuestion bqUtil = new DefaultBreakQuestion(filename);
        List<Question> cqList = bqUtil.breakQuestion();
        assert cqList != null;
        QuestionLibVO libVO = new QuestionLibVO();
        cqList.forEach(cq->{
            EsQuestion cq_question=new EsQuestion();
            cq_question.setQuestionContent(cq.getQuestionContent());
            System.out.println(cq.getQuestionContent());
            cq_question.setKeyword("");
            cq_question.setRightTime(0);
            cq_question.setRightTime(0);
            cq_question.setQuestionLibId(libId);
            cq_question.setQuestionLevel(0);
            cq_question.setCreateTime(new Date());
            cq_question.setLastModify(new Date());
            questionMapper.createQuestion(cq_question);
            questionRepository.save(cq_question);
        });
        JSONObject data = new JSONObject();
        data.put("isComplete", bqUtil.isComplete());
        data.put("data", cqList);
        return data;
    }

    private String produceTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }


    public List<QuestionLibVO> findPublicQuestionLibVOSPages(int page, int limit, Integer typeId) {
        assert page > 0;
        int total = (page - 1) * limit;
        return libMapper.selectPublicLibByPage(total, page, typeId);
    }
}
