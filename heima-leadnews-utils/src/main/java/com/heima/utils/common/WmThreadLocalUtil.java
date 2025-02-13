package com.heima.utils.common;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 15:43
 */
public class WmThreadLocalUtil {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
