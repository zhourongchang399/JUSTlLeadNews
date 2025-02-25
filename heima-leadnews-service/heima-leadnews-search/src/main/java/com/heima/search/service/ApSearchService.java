package com.heima.search.service;

import java.io.IOException;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/25 14:49
 */
public interface ApSearchService {
    void getMappingInfo(String mapping) throws IOException;

    void initMapping(String mapping) throws IOException;
}
