package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private WmMaterialMapper wmMaterialMapper;

    final static Integer DEFAULT_PAGE_SIZE = 20;
    final static Integer DEFAULT_PAGE_NUM = 1;

    @Override
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
    public ResponseResult submitNews(WmNewsDto wmNewsDto) {

        // 校验参数
        if (wmNewsDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 提交或修改文章
        Integer newsId = saveOrModifyArticle(wmNewsDto);

        // 判断当前文章是否为草稿,满足条件则结束当前方法
        if (wmNewsDto.getStatus() == 0) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 获取文章中的素材
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(wmNewsDto.getContent(), Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String materialUrl = (String) map.get("value");
                materials.add(materialUrl);
            }
        }

        // 保存文章与素材关系
        saveNewsAndMaterialsRelativation(newsId, materials, WemediaConstants.WM_CONTENT_REFERENCE);

        // 获取文章封面图片信息
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

        // 更新文章信息表
        WmNews needToUpdateNews = new WmNews();
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s : images) {
            stringJoiner.add(s);
        }
        needToUpdateNews.setImages(stringJoiner.toString());
        needToUpdateNews.setId(newsId);
        needToUpdateNews.setSubmitedTime(new Date());
        wmNewsMapper.updateNews(needToUpdateNews);

        // 保存文章封面和素材关系
        saveNewsAndMaterialsRelativation(newsId, images, WemediaConstants.WM_COVER_REFERENCE);

        // 返回结果
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
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
            wmNewsMapper.addNewsMaterial(wmNewsMaterials);
        }
    }

    // 提交或修改文章
    private Integer saveOrModifyArticle(WmNewsDto wmNewsDto) {
        WmNews wmNews = new WmNews();
        // 拷贝对象
        BeanUtils.copyProperties(wmNewsDto, wmNews);
        // 补全WnNews对象
        wmNews.setUserId(WmThreadLocalUtil.getCurrentId());
        wmNews.setCreatedTime(new Date());
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
            wmNewsMapper.delectNews(wmNews.getId());
        }
        // 保存文章
        wmNewsMapper.saveNews(wmNews);
        log.info("新增文章ID：{}", wmNews.getId());
        return wmNews.getId();
    }

}
