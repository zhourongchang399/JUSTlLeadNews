package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.api.apUser.IApUserClient;
import com.heima.api.wemedia.IWeMediaClient;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApArticleFreemakerService;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApBehaviorService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.article.vos.ApArticleBehavior;
import com.heima.model.article.vos.ApArticleSearchVo;
import com.heima.model.article.vos.ApHotArticleVo;
import com.heima.model.behavior.dtos.BehaviorDto;
import com.heima.model.behavior.pojos.Behavior;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.utils.common.WmThreadLocalUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/12 15:29
 */
@Service
@Slf4j
public class ApArticleServiceImpl implements ApArticleService {

    private static final int PAGE_MAX_SIZE = 50;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleFreemakerService apArticleFreemakerService;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IApUserClient iApUserClient;

    @Autowired
    private IWeMediaClient iWeMediaClient;

    @Override
    @Transactional
    public ResponseResult pageApArticle(Short type, ArticleHomeDto articleHomeDto) {
        // 数量校验
        if (articleHomeDto.getSize() == null || articleHomeDto.getSize() == 0) {
            articleHomeDto.setSize(10);
        }
        articleHomeDto.setSize(Math.min(articleHomeDto.getSize(), PAGE_MAX_SIZE));

        // 类型校验
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 文章频道校验
        if (articleHomeDto.getTag().isEmpty()) {
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 时间校验
        if (articleHomeDto.getMaxBehotTime() == null){
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if (articleHomeDto.getMinBehotTime() == null){
            articleHomeDto.setMinBehotTime(new Date());
        }

        log.info(articleHomeDto.toString());
        PageHelper.startPage(1,articleHomeDto.getSize());
        Page<ApArticle> apArticlePage = apArticleMapper.pageQueryApArticle(type, articleHomeDto);
        log.info("查询到{}条数据！{}", apArticlePage.getTotal(), apArticlePage.getResult());

        return ResponseResult.okResult(apArticlePage.getResult());
    }

    @Override
    @Transactional
    public ResponseResult saveArticle(ArticleDto articleDto) {
        // 参数校验
        if (articleDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 判断是否存在
        Long articleId = null;
        if (articleDto.getId() == null) {
            // 保存文章信息
            articleId = saveArticleInfo(articleDto);
            // 保存文章内容
            saveArticleContent(articleDto, articleId);
            // 保存文章配置
            saveArticleConfig(articleId);
        } else {
            articleId = articleDto.getId();
            // 修改文章信息
            modifyArticle(articleDto);
            // 修改文章内容
            modifyArticleContent(articleDto);
        }

        // 异步生成静态文件
        try {
            apArticleFreemakerService.ganerateStaricFile(articleId);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 操作成功，返回文章ID
        return ResponseResult.okResult(articleId);
    }

    @Override
    public ResponseResult loadArticle() {
        List<ApArticleSearchVo> apArticleSearchVoList = apArticleMapper.loadArticle(null);
        return ResponseResult.okResult(JSON.toJSONString(apArticleSearchVoList));
    }

    @Override
    @Transactional
    public ResponseResult loadArticleBehavior(BehaviorDto behaviorDto) {
        // 参数校验
        if (behaviorDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 获取用户Id
        Integer userId = WmThreadLocalUtil.getCurrentId();

        // 判断当前用户是否登录
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SUCCESS);
        }

        // 查询点赞，不喜欢，阅读数量行为数据
        String o = String.valueOf(redisTemplate.opsForHash().get("0:" + behaviorDto.getArticleId(), String.valueOf(userId)));
        Behavior behavior = JSON.parseObject(o, Behavior.class);

        // 查询文章收藏行为数据
        QueryWrapper<ApCollection> apCollectionQueryWrapper = new QueryWrapper<>();
        apCollectionQueryWrapper.eq("user_id", userId).eq("entry_id", behaviorDto.getArticleId());
        ApCollection apCollection = apCollectionMapper.selectOne(apCollectionQueryWrapper);

        // 查询关注行为数据
        ResponseResult followResult = iApUserClient.behavior(behaviorDto.getAuthorId(), userId);
        ApUserFollow apUserFollow = JSON.parseObject(String.valueOf(followResult.getData()), ApUserFollow.class);

        // 封装对象
        ApArticleBehavior apArticleBehavior = new ApArticleBehavior();
        apArticleBehavior.setIsCollection(apCollection != null ? 1 : 0);
        apArticleBehavior.setIsLike(behavior.getLike());
        apArticleBehavior.setIsUnlike(behavior.getUnlike());
        apArticleBehavior.setIsFollow(apUserFollow != null ? 1 : 0);

        // 返回结果
        return ResponseResult.okResult(apArticleBehavior);

    }

    @Override
    public ResponseResult pageApArticleWithHot(Short type, ArticleHomeDto articleHomeDto, boolean b) {
        // 数量校验
        if (articleHomeDto.getSize() == null || articleHomeDto.getSize() == 0) {
            articleHomeDto.setSize(10);
        }
        articleHomeDto.setSize(Math.min(articleHomeDto.getSize(), PAGE_MAX_SIZE));

        // 类型校验
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 文章频道校验
        List<WmChannel> wmChannelList = new ArrayList<>();
        if (articleHomeDto.getTag().isEmpty() || articleHomeDto.getTag().equals(ArticleConstants.DEFAULT_TAG)) {
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        } else {
            ResponseResult responseResult = iWeMediaClient.getWmChannel();
            if (responseResult.getData() != null && responseResult.getCode() == 200) {
                wmChannelList = JSON.parseArray((String) responseResult.getData(), WmChannel.class);
            }
            List<WmChannel> collect = wmChannelList.stream().filter(c -> String.valueOf(c.getId()).equals(articleHomeDto.getTag())).collect(Collectors.toList());
            articleHomeDto.setTag(collect.get(0).getName());
        }

        // 时间校验
        if (articleHomeDto.getMaxBehotTime() == null){
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if (articleHomeDto.getMinBehotTime() == null){
            articleHomeDto.setMinBehotTime(new Date());
        }

        if (b) {
            String o = String.valueOf(redisTemplate.opsForValue().get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + articleHomeDto.getTag()));
            if (o != null && !o.equals("")) {
                List<ApHotArticleVo> apHotArticleVos = JSON.parseArray(o, ApHotArticleVo.class);
                return ResponseResult.okResult(apHotArticleVos);
            }
        }

        return pageApArticle(type, articleHomeDto);

    }

    /**
     * @author: Zc
     * @description: 更新分数
     * @date: 2025/3/4 13:17
     * @param null
     * @return
     */
    @Override
    public void updateScore(ArticleVisitStreamMess mess) {
        // 参数校验
        if (mess == null || mess.getArticleId() == null || mess.getArticleId() == 0) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询文章
        ApArticle article = apArticleMapper.getById(mess.getArticleId());
        if (article == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        article.setComment(article.getComment() == null ? (Math.max(mess.getComment(), 0)) : article.getComment() + mess.getComment());
        article.setComment(article.getLikes() == null ? (Math.max(mess.getLike(), 0)) : article.getLikes() + mess.getLike());
        article.setComment(article.getCollection() == null ? (Math.max(mess.getCollect(), 0)) : article.getCollection() + mess.getCollect());
        article.setComment(article.getViews() == null ? (Math.max(mess.getView(), 0)) : article.getViews() + mess.getView());

        // 持久化到数据库
        apArticleMapper.updateById(article);

        // 计算原来的分数
        int oriScore = compScore(article.getLikes(), article.getCollection(), article.getComment(), article.getViews());
        // 计算新的行为分数
        int laterScore = compScore(mess.getLike(), article.getCollection(), article.getComment(), article.getViews());
        // 加总分数
        int score = oriScore + laterScore;

        // 封装对象
        ApHotArticleVo newHotArticleVo = new ApHotArticleVo();
        BeanUtils.copyProperties(article, newHotArticleVo);
        newHotArticleVo.setScore(score);

        // 更新当前频道的热门文章
        replaceOrInsertRedisHotArticle(article, newHotArticleVo, article.getChannelName());

        // 更新推荐频道的热门文章
        replaceOrInsertRedisHotArticle(article, newHotArticleVo, ArticleConstants.DEFAULT_TAG);

    }

    private void replaceOrInsertRedisHotArticle(ApArticle article, ApHotArticleVo newHotArticleVo, String channelName) {
        // 获取当前文章频道的 Redis 信息
        List<ApHotArticleVo> apHotArticleVos = new ArrayList<>();
        String hotArticleString = (String) redisTemplate.opsForValue().get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channelName);
        if (StringUtils.isNotBlank(hotArticleString)) {
            apHotArticleVos = JSON.parseArray(hotArticleString, ApHotArticleVo.class);
            // 排序
            apHotArticleVos = apHotArticleVos.stream().sorted(Comparator.comparing(ApHotArticleVo::getScore).reversed()).collect(Collectors.toList());
        }

        // 检索是否存在
        boolean flag = false;
        for (ApHotArticleVo hotArticleVo : apHotArticleVos) {
            if (hotArticleVo.getId().equals(article.getId())) {
                // 替换当前文章
                apHotArticleVos.remove(hotArticleVo);
                apHotArticleVos.add(newHotArticleVo);
                flag = true;
                break;
            }
        }

        if (!flag) {
            // 判断是否小于30条
            if (apHotArticleVos.size() < 30) {
                apHotArticleVos.add(newHotArticleVo);
            } else {
                // 判断是否在 Redis 中存在低于当前分数文章
                if (apHotArticleVos.get(apHotArticleVos.size() - 1).getScore() < newHotArticleVo.getScore()) {
                    // 移除最低分文章
                    apHotArticleVos.remove(apHotArticleVos.size() - 1);
                    // 插入最新文章，并排序
                    apHotArticleVos.add(newHotArticleVo);
                }
            }
        }

        // 排序
        apHotArticleVos = apHotArticleVos.stream().sorted(Comparator.comparing(ApHotArticleVo::getScore).reversed()).collect(Collectors.toList());

        // 替换 Redis 中数据
        redisTemplate.opsForValue().set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channelName, JSON.toJSONString(apHotArticleVos));
    }

    public int compScore(Integer likes, Integer collections, Integer comments, Integer views) {
        int likeScore = 0, collectScore = 0, commentScore = 0, viewScore = 0;
        if (likes != null) {
            likeScore += likes * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (collections != null) {
            collectScore += collections * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (comments != null) {
            commentScore += comments * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (views != null) {
            viewScore += views * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        return likeScore + collectScore + commentScore + viewScore;
    }

    private void modifyArticle(ArticleDto articleDto) {
        // 参数拷贝
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);
        // 修改文章信息
        apArticleMapper.updateById(apArticle);
    }

    private void saveArticleConfig(Long articleId) {
        // 设置文章默认配置
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setIsComment(ArticleConstants.DEFAULT_IS_COMMENT);
        apArticleConfig.setIsForward(ArticleConstants.DEFAULT_IS_FORWARD);
        apArticleConfig.setIsDown(ArticleConstants.DEFAULT_NOT_IS_DOWN);
        apArticleConfig.setIsDelete(ArticleConstants.DEFAULT_NOT_IS_DELETE);
        apArticleConfig.setArticleId(articleId);
        // 保存文章配置
        apArticleConfigMapper.insert(apArticleConfig);
    }

    private void modifyArticleContent(ArticleDto articleDto) {
        // 参数拷贝
        ApArticleContent apArticleContent = new ApArticleContent();
        BeanUtils.copyProperties(articleDto, apArticleContent);
        apArticleContent.setArticleId(articleDto.getId());
        // 保存文章内容
        apArticleContentMapper.update(apArticleContent);
    }

    private void saveArticleContent(ArticleDto articleDto, Long articleId) {
        // 参数拷贝
        ApArticleContent apArticleContent = new ApArticleContent();
        BeanUtils.copyProperties(articleDto, apArticleContent);
        apArticleContent.setArticleId(articleId);
        // 保存文章内容
        apArticleContentMapper.insert(apArticleContent);
    }

    private Long saveArticleInfo(ArticleDto articleDto) {
        // 参数拷贝
        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);
        // 保存文章信息
        apArticleMapper.insertArticle(apArticle);
        return apArticle.getId();
    }

}
