package com.heima.utils.common;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/13 15:43
 */
public class WmThreadLocalUtil {

    public static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }

    public static Integer getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
