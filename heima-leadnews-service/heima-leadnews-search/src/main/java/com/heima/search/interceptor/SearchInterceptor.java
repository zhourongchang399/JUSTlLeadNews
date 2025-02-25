package com.heima.search.interceptor;

import com.heima.utils.common.WmThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 15:46
 */
@Slf4j
public class SearchInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从header中取出userId
        String userId = request.getHeader("userId");
        // 存入threadLocal中
        if (userId != null && !userId.isEmpty()) {
            log.info("当前用户：{}", userId);
            WmThreadLocalUtil.setCurrentId(Integer.valueOf(userId));
        }
        // 放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 清理threadlocal中的userid
        WmThreadLocalUtil.removeCurrentId();
    }
}
