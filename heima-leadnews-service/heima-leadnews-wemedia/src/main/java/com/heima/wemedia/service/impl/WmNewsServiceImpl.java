package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.api.schedule.IScheduleClient;
import com.heima.common.constants.KafkaConstants;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.kafka.KafkaService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/14 0:55
 */
@Service
public class WmNewsServiceImpl implements WmNewsService {

    private static final Logger log = LoggerFactory.getLogger(WmNewsServiceImpl.class);
    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmAutoScanService wmAutoScanService;

    @Autowired
    private IScheduleClient iScheduleClient;

    @Autowired
    private KafkaService kafkaService;

    final static Integer DEFAULT_PAGE_SIZE = 20;
    final static Integer DEFAULT_PAGE_NUM = 1;

    @Override
    @Transactional
    public ResponseResult listNewsByConditions(WmNewsPageReqDto wmNewsPageReqDto) {
        // 参数校验
        if (wmNewsPageReqDto.getSize() == null || wmNewsPageReqDto.getSize() == 0) {
            wmNewsPageReqDto.setSize(DEFAULT_PAGE_SIZE);
        }
        if (wmNewsPageReqDto.getPage() == null || wmNewsPageReqDto.getPage() <= 0) {
            wmNewsPageReqDto.setPage(DEFAULT_PAGE_NUM);
        }

        // 获取当前用户信息
        Integer id = WmThreadLocalUtil.getCurrentId();
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 开始分页
        PageHelper.startPage(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());

        // 查询数据库
        Page<WmNews> wmNewsPage = wmNewsMapper.listByConditions(id, wmNewsPageReqDto);

        // 整合实体
        ResponseResult responseResult = new PageResponseResult(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize(), (int) wmNewsPage.getTotal());
        responseResult.setData(wmNewsPage.getResult());

        // 返回结果
        return  responseResult;
    }

    @Override
    @Transactional
    public ResponseResult submitNews(WmNewsDto wmNewsDto) {

        // 校验参数
        if (wmNewsDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取文章内容中的素材
        List<String> materials = getMaterials(wmNewsDto);

        // 获取文章封面中的素材
        List<String> images = getCoversImages(wmNewsDto, materials);
        wmNewsDto.setImages(images);

        // 提交或修改文章
        Integer newsId = saveOrModifyArticle(wmNewsDto);
        wmNewsDto.setId(newsId);

        // 判断当前文章是否为草稿,满足条件则结束当前方法
        if (wmNewsDto.getStatus() == 0) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 保存文章与素材关系
        saveNewsAndMaterialsRelativation(newsId, materials, WemediaConstants.WM_CONTENT_REFERENCE);

        // 保存文章封面和素材关系
        saveNewsAndMaterialsRelativation(newsId, images, WemediaConstants.WM_COVER_REFERENCE);

        // 发起文章自动审核的延迟任务
        addPublishNewsTaskToSchedule(wmNewsDto);
//        wmAutoScanService.scanNews(wmNewsDto.getId());

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    public void addPublishNewsTaskToSchedule(WmNewsDto wmNewsDto) {
        log.info("添加发布新闻任务到延迟服务中!!!");
        Task task = new Task();
        task.setExecuteTime(wmNewsDto.getPublishTime().toInstant().toEpochMilli());
        task.setParameters(JSON.toJSONString(wmNewsDto).getBytes(StandardCharsets.UTF_8));
        task.setTaskType(ScheduleConstants.NEWS_AUTO_SCAN_TASK);
        task.setPriority(ScheduleConstants.FIRST_PRIORITY);
        iScheduleClient.addTask(task);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void consumePublishNewsTaskSchedule() {
        log.info("执行发布新闻的定时任务！！！");
        ResponseResult responseResult = iScheduleClient.pull(ScheduleConstants.NEWS_AUTO_SCAN_TASK, ScheduleConstants.FIRST_PRIORITY);
        if (responseResult != null && responseResult.getCode().equals(200)) {
            String data = JSON.toJSONString(responseResult.getData());
            if (data != null) {
                List<Task> taskList = JSON.parseArray(data, Task.class);
                for (Task task : taskList) {
                    log.info("exec task: {}", task);
                    String parameters = new String(task.getParameters());
                    WmNewsDto wmNewsDto = JSON.parseObject(parameters, WmNewsDto.class);
                    // 异步执行文章自动审核
                    wmAutoScanService.scanNews(wmNewsDto.getId());
                }
            }
        }
    }

    private static List<String> getMaterials(WmNewsDto wmNewsDto) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(wmNewsDto.getContent(), Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String materialUrl = (String) map.get("value");
                materials.add(materialUrl);
            }
        }
        return materials;
    }

    private static List<String> getCoversImages(WmNewsDto wmNewsDto, List<String> materials) {
        List<String> images = new ArrayList<>();
        if (wmNewsDto.getType() != WemediaConstants.WM_NEWS_TYPE_AUTO) {
            images = wmNewsDto.getImages();
        } else {
            if (materials.size() >= WemediaConstants.WM_NEWS_SINGLE_IMAGE && materials.size() < WemediaConstants.WM_NEWS_MANY_IMAGE) {
                images = materials.subList(WemediaConstants.WM_NEWS_NONE_IMAGE, WemediaConstants.WM_NEWS_SINGLE_IMAGE);
            } else if (materials.size() >= WemediaConstants.WM_NEWS_MANY_IMAGE) {
                images = materials.subList(WemediaConstants.WM_NEWS_NONE_IMAGE, WemediaConstants.WM_NEWS_MANY_IMAGE);
            }
        }
        return images;
    }

    private void updateNewsImages(List<String> images, Integer newsId) {
        WmNews needToUpdateNews = new WmNews();
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s : images) {
            stringJoiner.add(s);
        }
        needToUpdateNews.setImages(stringJoiner.toString());
        needToUpdateNews.setId(newsId);
        needToUpdateNews.setSubmitedTime(new Date());
        wmNewsMapper.updateNews(needToUpdateNews);
    }

    @Override
    @Transactional
    public ResponseResult getOneNews(Integer id) {
        // 校验参数
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 根据Id查询文章
        WmNews wmNews = wmNewsMapper.getById(id);
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        return  ResponseResult.okResult(wmNews);
    }

    @Override
    @Transactional
    public ResponseResult downOrUp(WmNewsDto wmNewsDto) {
        // 参数校验
        if (wmNewsDto == null || wmNewsDto.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 根据Id查询文章
        WmNews wmNews = wmNewsMapper.getById(wmNewsDto.getId());
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.NOT_EXIST_NEWS);
        }

        // 判断当前文章是否已经发布
        if (wmNews.getStatus() != WemediaConstants.WM_NEWS_PUBLISHED) {
            throw new CustomException(AppHttpCodeEnum.NOT_PUBLISHED_THEN_NOT_DOWN_OR_UP);
        }

        // 修改文章信息
        WmNews needToUpdateNews = new WmNews();
        needToUpdateNews.setId(wmNewsDto.getId());
        needToUpdateNews.setEnable(wmNewsDto.getEnable());
        wmNewsMapper.updateNews(needToUpdateNews);

        // 向Kafka发送消息，通知article服务上架或下架文章
        Map<String, Object> map = new HashMap<>();
        map.put("articleId", wmNews.getArticleId());
        map.put("enable", wmNewsDto.getEnable());
        String jsonString = JSON.toJSONString(map);
        kafkaService.sendAsync(KafkaConstants.UP_OR_DOWN_ARTICLE_TOPIC, jsonString);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult lsitVo(NewsAuthDto newsAuthDto) {
        // 参数校验
        if (newsAuthDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 设置size的上下限
        if (newsAuthDto.getSize() > 20) {
            newsAuthDto.setSize(20);
        } else if (newsAuthDto.getSize() < 5) {
            newsAuthDto.setSize(5);
        }

        // 设置page的下限
        if (newsAuthDto.getPage() <= 0) {
            newsAuthDto.setPage(1);
        }

        // 封装对象
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(newsAuthDto, wmNews);

        // 开始分页
        PageHelper.startPage(newsAuthDto.getPage(), newsAuthDto.getSize());
        // 开始查询
        Page<WmNews> wmNewsPage = wmNewsMapper.list(wmNews);

        // 封装结果
        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setCurrentPage(wmNewsPage.getPageNum());
        pageResponseResult.setTotal((int) wmNewsPage.getTotal());
        pageResponseResult.setData(wmNewsPage.getResult());
        pageResponseResult.setSize(wmNewsPage.getPageSize());

        // 返回结果
        return pageResponseResult;
    }

    private void saveNewsAndMaterialsRelativation(Integer newsId, List<String> materials, Short type) {
        // 判断素材是否失效，并获取素材信息
        if (!materials.isEmpty()) {
            List<WmNewsMaterial> wmNewsMaterials = new ArrayList<>();
            for (int i = 0; i < materials.size(); i++) {
                WmNewsMaterial wmNewsMaterial = new WmNewsMaterial();
                Integer materialId = wmMaterialMapper.selectOne(materials.get(i));
                if (materialId == null) {
                    throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
                }
                wmNewsMaterial.setMaterialId(materialId);
                wmNewsMaterial.setOrd((short) i);
                wmNewsMaterial.setType(type);
                wmNewsMaterial.setNewsId(newsId);
                wmNewsMaterials.add(wmNewsMaterial);
            }
            // 删除原先文章素材关系
            wmNewsMaterialMapper.deleteByNewsIdAndType(newsId, type);
            // 新增文章素材关系
            wmNewsMaterialMapper.addNewsMaterial(wmNewsMaterials);
        }
    }

    // 提交或修改文章
    private Integer saveOrModifyArticle(WmNewsDto wmNewsDto) {
        // 拷贝对象
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(wmNewsDto, wmNews);

        // 补全WnNews对象
        wmNews.setUserId(WmThreadLocalUtil.getCurrentId());
        if (wmNews.getId() == null) {
            wmNews.setCreatedTime(new Date());
        }

        // 转存images
        List<String> images = wmNewsDto.getImages();
        if (images != null && images.size() > 0) {
            StringJoiner sb = new StringJoiner(",");
            for (int i = 0; i < images.size(); i++) {
                sb.add(images.get(i));
            }
            wmNews.setImages(sb.toString());
        }

        // 如果封面类型为自动则type存储为null
        if (wmNewsDto.getType() == WemediaConstants.WM_NEWS_TYPE_AUTO) {
            wmNews.setType(null);
        }

        // 判断是否是第一次保存/修改文章
        if (wmNews.getId() != null) {
            // 修改文章
            wmNewsMapper.updateNews(wmNews);
            log.info("修改的文章ID：{}", wmNews.getId());
        } else {
            // 保存文章
            wmNewsMapper.saveNews(wmNews);
            log.info("新增的文章ID：{}", wmNews.getId());
        }
        return wmNews.getId();
    }

}
