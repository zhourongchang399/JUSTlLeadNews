package com.heima.common.constants;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/22 15:30
 */
public class ScheduleConstants {

    // 状态 0=int 1=EXECUTED 2=CANCELLED
    public final static Integer INIT_STATUS = 0;
    public final static Integer EXECUTED_STATUS = 1;
    public final static Integer CANCELLED_STATUS = 2;

    public final static Integer FIRST_PRIORITY = 1;
    public final static Integer SECOND_PRIORITY = 2;
    public final static Integer THIRD_PRIORITY = 3;

    public final static Integer NEWS_AUTO_SCAN_TASK = 101;

    public final static Integer INIT_VERSION = 1;

    public static final String FUTURE = "future_";
    public static final String CURRENT = "current_";
}
