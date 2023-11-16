package com.zlx.modules.person.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AssetIndexCovContext {

    public static String evaluate(String context,String pre_1_context,String pre_2_context){

        if(StrUtil.isBlank(context)){
            return context;
        }

        JSONObject result = JSONUtil.parseObj(context);
        JSONArray objects = JSONUtil.parseArray(result.get("index"));

        JSONObject pre_1_entries = null;
        JSONObject pre_2_entries = null;
        JSONArray pre_1_objects = null;
        JSONArray pre_2_objects = null;

        if(!StrUtil.isBlank(pre_1_context)){
            pre_1_entries = JSONUtil.parseObj(pre_1_context);
            pre_1_objects = JSONUtil.parseArray(pre_1_entries.get("index"));
        }

        if(!StrUtil.isBlank(pre_2_context)){
            pre_2_entries = JSONUtil.parseObj(pre_2_context);
            pre_2_objects = JSONUtil.parseArray(pre_2_entries.get("index"));
        }

        JSONArray array = JSONUtil.createArray();
        for(int i =0;i< objects.size();i++) {
            JSONObject entries = JSONUtil.parseObj(objects.get(i));
            Double value = null;
            Double pre_1_value = null;
            Double pre_2_value = null;

            Object index_value = entries.get("index_value");
            if(index_value != null){
                value = Double.valueOf(index_value.toString());
            }
            String point = "";
            if(value !=null){
                point = result.get("report_year")+"";
            }

            if(pre_1_objects !=null){
                Object index_value1 = JSONUtil.parseObj(pre_1_objects.get(i)).get("index_value");
                if(index_value1 != null){
                    pre_1_value = Double.valueOf(index_value1.toString());
                    if(StrUtil.isBlank(point)){
                        point = pre_1_entries.get("report_year")+"";
                    }else {
                        point = point + "," + pre_1_entries.get("report_year");
                    }
                }
            }
            if(pre_2_objects !=null){
                Object index_value2 = JSONUtil.parseObj(pre_2_objects.get(i)).get("index_value");
                if(index_value2 != null){
                    pre_2_value = Double.valueOf(index_value2.toString());
                    if(StrUtil.isBlank(point)){
                        point = pre_2_entries.get("report_year")+"";
                    }else {
                        point = point+","+pre_2_entries.get("report_year");
                    }
                }
            }
            if(value != null && pre_1_value !=null && pre_2_value != null) {
                entries.put("cov", getCov(value, pre_1_value, pre_2_value));
            }
            entries.put("point",point);
            entries.put("is_flu",getFlu(value, pre_1_value, pre_2_value));
            array.add(entries);
        }


        return result.put("index",array).toString();

    }

    // "is_flu":"1"   //平稳 0 下降 1  波动 2 上升 3 一个点 4 没有点 5
    public static int getFlu(Double a, Double b, Double c){
        if(a==null && b==null && c==null){
            return 5;
        }else if((a!=null && b==null && c==null) || (a==null && b!=null && c==null) || (a==null && b==null && c!=null)){
            return 4;
        }else if((a!=null && b!=null && c==null) ){
            if(a>b){
                return 3;
            }else if(a==b){
                return 0;
            }else{
                return 1;
            }
        }else if((a==null && b!=null && c!=null)){
            if(b>c){
                return 3;
            }else if(c==b){
                return 0;
            }else{
                return 1;
            }

        }else if((a!=null && b==null && c!=null)){
            if(a>c){
                return 3;
            }else if(a==c){
                return 0;
            }else{
                return 1;
            }
        }else {
            if(a>b && b>c){
                return 3;
            }else if(a<b && b <c){
                return 1;
            }else if(a==b && b==c){
                return 0;
            }else{
                return 2;
            }
        }

    }

    //
    public static Double getCov(Double a, Double b, Double c) {
        List<Double> data = new ArrayList<Double>();

        data.add(a);
        data.add(b);
        data.add(c);


        // 计算变异系数，代码同上
        if (data.size() < 3) {
            return null;
        }

        Double sum = 0.0;
        for (Double d : data) {
            sum += d;
        }
        Double mean = sum / data.size();

        Double variance = 0.0;
        for (Double d : data) {
            variance += Math.pow(d - mean, 2);
        }
        variance /= (data.size() - 1);
        Double stdDev = Math.sqrt(variance);

        if (mean == 0) {
            return null;
        } else {
            double abs = Math.abs(stdDev / mean);
            DecimalFormat df = new DecimalFormat("#.######");
            String formatted = df.format(abs);
            double result = Double.parseDouble(formatted);
            return result;
        }
    }
}
