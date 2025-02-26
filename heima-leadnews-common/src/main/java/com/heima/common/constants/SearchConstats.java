package com.heima.common.constants;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/26 13:24
 */
public class SearchConstats {

    public static final String ARTICLE_INDEX = "app_article_info";

    public static final String ASSOCIATION_INDEX = "app_association_info";

    public static final String MAPPING_TEMPLATE = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "            \"id\":{\n" +
            "                \"type\":\"long\"\n" +
            "            },\n" +
            "            \"publishTime\":{\n" +
            "                \"type\":\"date\"\n" +
            "            },\n" +
            "            \"layout\":{\n" +
            "                \"type\":\"integer\"\n" +
            "            },\n" +
            "            \"images\":{\n" +
            "                \"type\":\"keyword\",\n" +
            "                \"index\": false\n" +
            "            },\n" +
            "            \"staticUrl\":{\n" +
            "                \"type\":\"keyword\",\n" +
            "                \"index\": false\n" +
            "            },\n" +
            "            \"authorId\": {\n" +
            "                \"type\": \"long\"\n" +
            "            },\n" +
            "            \"authorName\": {\n" +
            "                \"type\": \"text\"\n" +
            "            },\n" +
            "            \"title\":{\n" +
            "                \"type\":\"text\",\n" +
            "                \"analyzer\":\"ik_smart\"\n" +
            "            },\n" +
            "            \"content\":{\n" +
            "                \"type\":\"text\",\n" +
            "                \"analyzer\":\"ik_smart\"\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

}
