package com.zlx.modules.person.util;

import cn.hutool.core.util.CharUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    public static Map<String,String> putMsg(Map<String, String> resultMap, Map<String, String> mp1) {
        String code = mp1.get("code");
        if(resultMap.containsKey(code)){
            resultMap.put(code,resultMap.get(code)+"、"+mp1.get("index"));
        }else{
            resultMap.put(code,mp1.get("index"));
        }
        return resultMap;
    }
    public static String concatResult(String result) {
        if(StringUtils.isBlank(result)){
            return result;
        }else {
            return result+"，";
        }
    }

    public static Map<String,String> getMsg(String index, String pre_index, String pre_pre_index, String msg){
        Map mp = new HashMap<String,String>();
        if(StringUtils.isBlank(index) || StringUtils.isBlank(pre_index) || StringUtils.isBlank(pre_pre_index)) {
            mp.put("code","-1");
            return mp;
        }else if (isValue(index) && isValue(pre_index) && isValue(pre_pre_index)){
            Double avg = getAvg(index);
            Double pre_avg = getAvg(pre_index);
            Double pre_pre_avg = getAvg(pre_pre_index);
            Double compare  = (avg - pre_avg)/pre_avg;
            Double pre_compare  = (pre_avg - pre_pre_avg)/pre_pre_avg;
            if(compare > 0 && pre_compare > 0){
                mp.put("code","1");
                mp.put("index",msg);
                mp.put("msg","逐年增加");
                return mp;
            }else if(compare < 0 && pre_compare < 0){
                mp.put("code","2");
                mp.put("index",msg);
                mp.put("msg","逐年减少");
                return mp;
            }else if(compare > 0 && pre_compare < 0 && compare-pre_compare < 0.5){
                mp.put("code","3");
                mp.put("index",msg);
                mp.put("msg","呈波动趋势");
                return mp;
            }else if(compare > 0 && pre_compare < 0 && compare-pre_compare >= 0.5){
                mp.put("code","4");
                mp.put("index",msg);
                mp.put("msg","呈波动趋势且波动较大");
                return mp;
            }else if(compare < 0 && pre_compare > 0 && compare-pre_compare > -0.5){
                mp.put("code","3");
                mp.put("index",msg);
                mp.put("msg","呈波动趋势");
                return mp;
            }else if(compare < 0 && pre_compare > 0 && compare-pre_compare <= -0.5){
                mp.put("code","4");
                mp.put("index",msg);
                mp.put("msg","呈波动趋势且波动较大");
                return mp;
            }else{
                mp.put("code","-1");
                return mp;
            }

        }else {
            mp.put("code","-1");
            return mp;
        }
    }

    public static Double getAvg(String v) {
        if(v.contains(",")){
            String[] split = v.split(",");
            return  (Double.valueOf(split[0]) + Double.valueOf(split[1]))/2;
        }else{
            return Double.valueOf(v);
        }
    }

    public static boolean isValue(String v) {
        if(v.contains(",")){
            String[] split = v.split(",");
            if(split.length == 2) {
                String a = split[0];
                String b = split[1];
                return isNum(a) && isNum(b);
            }else {
                return false;
            }
        }else{
            return isNum(v);
        }
    }

    public static boolean isNum(String v){
        String pattern = "(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[0-9])|\\d";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(v);
        boolean matches = m.matches();
        return matches;
    }
    public static Double convertToDouble(String str) {
        if (isBlankorZH(str)) {
            return null;
        }

        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static boolean isBlankorZH(CharSequence str) {
        if(str == null) {
            return true;
        }
        int length = str.length();

        if (str != null && length != 0) {
            if(!isNum(str.toString())){
                return true;
            }
            for(int i = 0; i < length; ++i) {
                if (!CharUtil.isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
    public static boolean isNumOrRange(String v) {
        if(v.contains(",")){
            String[] split = v.split(",");
            return  isNum(split[0]) && isNum(split[1]);
        }else{
            return isNum(v);
        }
    }
}
