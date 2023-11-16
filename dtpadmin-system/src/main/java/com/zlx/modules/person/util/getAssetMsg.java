package com.zlx.modules.person.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getAssetMsg {

    public  static String evaluate (String index1, String pre_index1, String pre_pre_index1,String msg1,
                              String index2, String pre_index2, String pre_pre_index2,String msg2,
                              String index3, String pre_index3, String pre_pre_index3,String msg3) {
        Map<String, String> mp1 = getMsg(index1, pre_index1, pre_pre_index1, msg1);
        Map<String, String> mp2 = getMsg(index2, pre_index2, pre_pre_index2, msg2);
        Map<String, String> mp3 = getMsg(index3, pre_index3, pre_pre_index3, msg3);

        Map<String, String> initMap = new HashMap<String,String>();

        Map<String, String> stringStringMap = putMsg(initMap, mp1);
        Map<String, String> stringStringMap1 = putMsg(stringStringMap, mp2);
        Map<String, String> resultMap = putMsg(stringStringMap1, mp3);
        String result = "在企业资产方面";
        if(resultMap.size() == 1 && resultMap.containsKey("-1")){
            return "";
        }else {
            if(resultMap.containsKey("1")) {
                result = concatResult(result) + resultMap.get("1")+"逐年增加";
            }

            if(resultMap.containsKey("2")) {
                result = concatResult(result) + resultMap.get("2")+"逐年减少";
            }

            if(resultMap.containsKey("3") && resultMap.containsKey("4")) {
                result = concatResult(result) + resultMap.get("3")+"、"+resultMap.get("4")+
                        "呈波动趋势，其中"+resultMap.get("4")+"波动较大,请关注";
            }else if(!resultMap.containsKey("3") && resultMap.containsKey("4")){
                result = concatResult(result) + resultMap.get("4")+
                        "呈波动趋势，其中"+resultMap.get("4")+"波动较大,请关注";
            }else if(resultMap.containsKey("3") && !resultMap.containsKey("4")){
                result = concatResult(result) + resultMap.get("3")+
                        "呈波动趋势";
            }
            return result+"；";
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

    public static Map<String,String> putMsg(Map<String, String> resultMap, Map<String, String> mp1) {
        String code = mp1.get("code");
        if(resultMap.containsKey(code)){
            resultMap.put(code,resultMap.get(code)+"、"+mp1.get("index"));
        }else{
            resultMap.put(code,mp1.get("index"));
        }
        return resultMap;
    }

    public static Double getAvg(String v) {
        if(v.contains(",")){
            String[] split = v.split(",");
            return  (Double.valueOf(split[0]) + Double.valueOf(split[1]))/2;
        }else{
            return Double.valueOf(v);
        }
    }

    public static String concatResult(String result) {
        if(StringUtils.isBlank(result)){
            return result;
        }else {
            return result+"，";
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

}
