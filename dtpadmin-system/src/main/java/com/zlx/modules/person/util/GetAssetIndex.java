package com.zlx.modules.person.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Iterator;

public class GetAssetIndex {
    public static String evaluate(String context, String index) {
        JSONArray arr = JSONUtil.parseArray(JSONUtil.parseObj(context).get("index"));
        Iterator<Object> iterator = arr.iterator();
        while(iterator.hasNext()){
            Object next = iterator.next();
            JSONObject entries = JSONUtil.parseObj(next);
            if(index.equals(entries.get("index_key"))){
                Object index_value = entries.get("index_value");
                if (StrUtil.isBlankIfStr(index_value)){
                    return null;
                }else {
                    return entries.get("index_value").toString();
                }
            }
        }

        return null;
    }
}
