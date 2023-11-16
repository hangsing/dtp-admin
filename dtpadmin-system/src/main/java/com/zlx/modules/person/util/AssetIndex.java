package com.zlx.modules.person.util;

import cn.hutool.core.util.CharUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AssetIndex {

    public static String exec(String report_year,
                              String total_assets, String total_equity, String total_sales, String total_profit, String prime_bus_profit, String retained_profit,
                              String total_tax, String total_liability, Long employee_no,
                              String pre_1_total_assets, String pre_1_total_equity, String pre_1_total_sales, String pre_1_total_profit, String pre_1_retained_profit,
                              String context, String province, String city, String industry_name_1, String industry_code_1
    ) {

        //收入增长率
        Double inc_total_sales = 0.0;

        if (isBlankorZH(pre_1_total_sales) || isBlankorZH(total_sales)) {
            inc_total_sales = null;
        } else if (convertToDouble(pre_1_total_sales) == 0) {
            inc_total_sales = null;
        } else {
            inc_total_sales = (convertToDouble(total_sales) - convertToDouble(pre_1_total_sales)) / Math.abs(convertToDouble(pre_1_total_sales));
        }

        //近2年平均营业总收入  pre_1_total_sales 为空就只取 total_sales
        Double avg_total_sales = 0.0;
        if (isBlankorZH(total_sales) && isBlankorZH(pre_1_total_sales)) {
            avg_total_sales = null;
        } else if (isBlankorZH(total_sales) && !isBlankorZH(pre_1_total_sales)) {
            avg_total_sales = convertToDouble(pre_1_total_sales);
        } else if (!isBlankorZH(total_sales) && isBlankorZH(pre_1_total_sales)) {
            avg_total_sales = convertToDouble(total_sales);
        } else {
            avg_total_sales = (convertToDouble(total_sales) + convertToDouble(pre_1_total_sales)) / 2;
        }

        //平均净资产  pre_1_total_equity 为null 只取total_equity
        Double avg_total_equity = 0.0;
        if (isBlankorZH(total_equity)) {
            avg_total_equity = null;
        } else if (isBlankorZH(total_equity) && !isBlankorZH(pre_1_total_equity)) {
            avg_total_equity = convertToDouble(pre_1_total_equity);
        } else if (!isBlankorZH(total_equity) && isBlankorZH(pre_1_total_equity)) {
            avg_total_equity = convertToDouble(total_equity);
        } else {
            avg_total_equity = (convertToDouble(total_equity) + convertToDouble(pre_1_total_equity)) / 2;
        }
        //净资产收益率 pre_1_total_equity 为null  retained_profit/nullif(total_equity,0)
        Double roe = 0.0;
        if (isBlankorZH(retained_profit)) {
            roe = null;
        } else if (isBlankorZH(total_equity) && !isBlankorZH(pre_1_total_equity)) {
            if(convertToDouble(pre_1_total_equity) == 0 || (convertToDouble(pre_1_total_equity) <0 && convertToDouble(retained_profit)<0) || (convertToDouble(pre_1_total_equity) <0 && convertToDouble(retained_profit)>0)){
                roe = null;
            }else {
                roe = convertToDouble(retained_profit) / convertToDouble(pre_1_total_equity);
            }
        } else if (!isBlankorZH(total_equity) && isBlankorZH(pre_1_total_equity)) {
            if(convertToDouble(total_equity) == 0 || (convertToDouble(total_equity) <0 && convertToDouble(retained_profit)<0) || (convertToDouble(total_equity) <0 && convertToDouble(retained_profit)>0)){
                roe = null;
            }else {
                roe = convertToDouble(retained_profit) / convertToDouble(total_equity);
            }
        } else {
            total_equity = isBlankorZH(total_equity) ? "0" : total_equity;
            pre_1_total_equity = isBlankorZH(pre_1_total_equity) ? "0" : pre_1_total_equity;
            Double rs = convertToDouble(total_equity) + convertToDouble(pre_1_total_equity);
            if (rs == 0) {
                roe = null;
            } else {
                if((rs <0 && convertToDouble(retained_profit)<0) || (rs <0 && convertToDouble(retained_profit)>0)){
                    roe = null;
                }else {
                    roe = convertToDouble(retained_profit) / ((convertToDouble(total_equity) + convertToDouble(pre_1_total_equity)) / 2);
                }
            }

        }
        //资产负债率 total_assets null->0
        Double loar = 0.0;
        if (isBlankorZH(total_liability)) {
            loar = null;
        }else if (isBlankorZH(total_assets)){
            loar = null;
        }else if (convertToDouble(total_assets) == 0){
            loar = null;
        }else {
            if (convertToDouble(total_assets) < 0  & convertToDouble(total_liability) >0) {
                loar = null;
            } else if(convertToDouble(total_liability) < 0){
                loar = null;
            }else {
                loar = convertToDouble(total_liability) / convertToDouble(total_assets);
            }
        }
        //净利润率
        Double net_profit_margin = 0.0;
        if (isBlankorZH(retained_profit)) {
            net_profit_margin = null;
        }else if(isBlankorZH(total_sales)){
            net_profit_margin = null;
        }else if(convertToDouble(total_sales) == 0){
            net_profit_margin = null;
        }else {
            if (convertToDouble(retained_profit) < 0 && convertToDouble(total_sales) < 0) {
                net_profit_margin = null;
            }else if(convertToDouble(retained_profit) > 0 && convertToDouble(total_sales) < 0){
                net_profit_margin = null;
            } else {
                net_profit_margin = convertToDouble(retained_profit) / convertToDouble(total_sales);
            }
        }
        //总资产周转率
        Double total_asset_turnover = 0.0;
        if (isBlankorZH(total_sales)) {
            total_asset_turnover = null;
        } else if (isBlankorZH(total_assets) && isBlankorZH(pre_1_total_assets)) {
            total_asset_turnover = null;
        } else if (isBlankorZH(total_assets) && !isBlankorZH(pre_1_total_assets)) {
            if(convertToDouble(pre_1_total_assets) == 0){
                total_asset_turnover = null;
            }else if(convertToDouble(total_sales)<0 && convertToDouble(pre_1_total_assets)<0){
                total_asset_turnover = null;
            }else if(convertToDouble(total_sales)>0 && convertToDouble(pre_1_total_assets)<0){
                total_asset_turnover = null;
            }else {
                total_asset_turnover = convertToDouble(total_sales) / convertToDouble(pre_1_total_assets);
            }
        } else if (!isBlankorZH(total_assets) && isBlankorZH(pre_1_total_assets)) {
            if(convertToDouble(total_assets) == 0){
                total_asset_turnover = null;
            }else if(convertToDouble(total_sales)<0 && convertToDouble(total_assets)<0){
                total_asset_turnover = null;
            }else if(convertToDouble(total_sales)>0 && convertToDouble(total_assets)<0){
                total_asset_turnover = null;
            }else {
                total_asset_turnover = convertToDouble(total_sales) / convertToDouble(total_assets);
            }
        } else {
            total_assets = isBlankorZH(total_assets) ? "0" : total_assets;
            pre_1_total_assets = isBlankorZH(pre_1_total_assets) ? "0" : pre_1_total_assets;
            Double rs = convertToDouble(total_assets) + convertToDouble(pre_1_total_assets);
            if (rs == 0) {
                total_asset_turnover = null;
            } else {
                if(convertToDouble(total_sales)<0 && rs<0){
                    total_asset_turnover = null;
                }else if(convertToDouble(total_sales)>0 && rs<0){
                    total_asset_turnover = null;
                }else {
                    total_asset_turnover = convertToDouble(total_sales) / ((convertToDouble(total_assets) + convertToDouble(pre_1_total_assets)) / 2);
                }
            }
        }
        //总资产收益率
        Double roa = 0.0;
        if (isBlankorZH(retained_profit)) {
            roa = null;
        }else if(isBlankorZH(total_assets) && isBlankorZH(pre_1_total_assets) ){
            roa = null;
        } else if (isBlankorZH(total_assets) && !isBlankorZH(pre_1_total_assets)) {
            if(convertToDouble(pre_1_total_assets) == 0){
                roa = null;
            }else {
                roa = convertToDouble(retained_profit) / convertToDouble(pre_1_total_assets);
            }

        } else if (!isBlankorZH(total_assets) && isBlankorZH(pre_1_total_assets)) {
            if(convertToDouble(total_assets) == 0){
                roa = null;
            }else {
                roa = convertToDouble(retained_profit) / convertToDouble(total_assets);
            }

        } else {
            total_assets = isBlankorZH(total_assets) ? "0" : total_assets;
            pre_1_total_assets = isBlankorZH(pre_1_total_assets) ? "0" : pre_1_total_assets;
            Double rs = convertToDouble(total_assets) + convertToDouble(pre_1_total_assets);
            if (rs == 0) {
                roa = null;
            } else {
                if(convertToDouble(retained_profit) < 0 && rs <0){
                    roa = null;
                }else if(convertToDouble(retained_profit) > 0 && rs <0){
                    roa = null;
                }else {
                    roa = convertToDouble(retained_profit) / ((convertToDouble(total_assets) + convertToDouble(pre_1_total_assets)) / 2);
                }
            }
        }

        //产权比率
        Double debt_to_equity_ratio = 0.0;
        if (isBlankorZH(total_liability)) {
            debt_to_equity_ratio =  null;
        }else if(isBlankorZH(total_equity)) {
            debt_to_equity_ratio = null;
        }else if (convertToDouble(total_equity)==0){
            debt_to_equity_ratio = null;
        }else {
            if(convertToDouble(total_liability) < 0 ){
                debt_to_equity_ratio = null;
            }else if(convertToDouble(total_liability) >0 && convertToDouble(total_equity) <0) {
                debt_to_equity_ratio = null;
            }else {
                debt_to_equity_ratio = convertToDouble(total_liability) / convertToDouble(total_equity);
            }
        }


        //权益乘数
        Double equity_multiplier = 0.0;
        if (isBlankorZH(total_assets)) {
            equity_multiplier = null;
        }else if(isBlankorZH(total_equity)){
            equity_multiplier = null;
        }else if (convertToDouble(total_equity) == 0) {
            equity_multiplier = null;
        }else {
            if(convertToDouble(total_assets) < 0 && convertToDouble(total_equity) <0){
                equity_multiplier = null;
            }else if(convertToDouble(total_assets) > 0 && convertToDouble(total_equity) <0){
                equity_multiplier = null;
            }else if(convertToDouble(total_assets) < 0 && convertToDouble(total_equity) >0){
                equity_multiplier = null;
            }else {
                equity_multiplier = convertToDouble(total_assets) / convertToDouble(total_equity);
            }
        }

        //营业利润率
        Double gross_profit_margin = 0.0;
        if (isBlankorZH(total_profit)) {
            gross_profit_margin = null;
        }else if(isBlankorZH(total_sales)){
            gross_profit_margin = null;
        }else if(convertToDouble(total_sales) == 0){
            gross_profit_margin = null;
        } else {
            if (convertToDouble(total_profit) < 0 && convertToDouble(total_sales) < 0) {
                gross_profit_margin = null;
            }else if (convertToDouble(total_profit) > 0 && convertToDouble(total_sales) < 0) {
                gross_profit_margin = null;
            } else {
                gross_profit_margin = convertToDouble(total_profit) / convertToDouble(total_sales);
            }
        }
        //营业利润增长率
        Double gross_profit_growth_rate = 0.0;
        if (isBlankorZH(total_profit) || isBlankorZH(pre_1_total_profit)) {
            gross_profit_growth_rate = null;
        } else if (convertToDouble(total_profit) == 0) {
            gross_profit_growth_rate = null;
        } else {
            gross_profit_growth_rate = (convertToDouble(total_profit) - convertToDouble(pre_1_total_profit)) / Math.abs(convertToDouble(pre_1_total_profit));
        }
        //资本积累率
        Double capital_accumulation_rate = 0.0;
        if (isBlankorZH(total_equity) || isBlankorZH(pre_1_total_equity)) {
            capital_accumulation_rate = null;
        } else if (convertToDouble(total_equity) == 0) {
            capital_accumulation_rate = null;
        } else {
            capital_accumulation_rate = (convertToDouble(total_equity) - convertToDouble(pre_1_total_equity)) / Math.abs(convertToDouble(pre_1_total_equity));
        }
        //总资产增长率
        Double total_asset_growth_rate = 0.0;
        if (isBlankorZH(total_assets) || isBlankorZH(pre_1_total_assets)) {
            total_asset_growth_rate = null;
        } else if (convertToDouble(total_assets) == 0) {
            total_asset_growth_rate = null;
        } else {
            total_asset_growth_rate = (convertToDouble(total_assets) - convertToDouble(pre_1_total_assets)) / Math.abs(convertToDouble(pre_1_total_assets));
        }
        //净利润增长率
        Double net_profit_growth_rate = 0.0;
        if (isBlankorZH(retained_profit) || isBlankorZH(pre_1_retained_profit)) {
            net_profit_growth_rate = null;
        } else if (convertToDouble(retained_profit) == 0) {
            net_profit_growth_rate = null;
        } else {
            net_profit_growth_rate = (convertToDouble(retained_profit) - convertToDouble(pre_1_retained_profit)) / Math.abs(convertToDouble(pre_1_retained_profit));
        }
        //人均销售收入
        Double p_sales = 0.0;
        if (isBlankIfStr(employee_no)){
            p_sales = null;
        }else if(employee_no <= 0){
            p_sales = null;
        }else if (employee_no != 0 && !isBlankorZH(total_sales)) {
            p_sales = convertToDouble(total_sales) / employee_no;
        }else {
            p_sales = null;
        }
        //人均利润
        Double p_profit = 0.0;
        if (isBlankIfStr(employee_no)){
            p_profit = null;
        }else if(employee_no <= 0){
            p_profit = null;
        }else if (employee_no != 0 && !isBlankorZH(total_profit)) {
            p_profit = convertToDouble(total_profit) / employee_no;
        }else {
            p_profit = null;
        }

        //封装返回指标数据
        JSONArray array = new JSONArray();
        //封装返回结果数据
        JSONObject entries = null;

        try {
            //1.0指标
            array.add(JSONUtil.createObj().put("index_key", "inc_total_sales").put("index_value", convertToDecimal(inc_total_sales,6)).put("comment", "收入增长率"));
            array.add(JSONUtil.createObj().put("index_key", "avg_total_sales").put("index_value", convertToDecimal(avg_total_sales,6)).put("comment", "近2年平均营业总收入"));
            array.add(JSONUtil.createObj().put("index_key", "avg_total_equity").put("index_value", convertToDecimal(avg_total_equity,6)).put("comment", "平均净资产"));
            array.add(JSONUtil.createObj().put("index_key", "roe").put("index_value", convertToDecimal(roe,6)).put("comment", "净资产收益率"));
            array.add(JSONUtil.createObj().put("index_key", "loar").put("index_value", convertToDecimal(loar,6)).put("comment", "资产负债率"));
            array.add(JSONUtil.createObj().put("index_key", "net_profit_margin").put("index_value", convertToDecimal(net_profit_margin,6)).put("comment", "净利润率"));
            array.add(JSONUtil.createObj().put("index_key", "total_asset_turnover").put("index_value", convertToDecimal(total_asset_turnover,6)).put("comment", "总资产周转率"));
            array.add(JSONUtil.createObj().put("index_key", "roa").put("index_value", convertToDecimal(roa,6)).put("comment", "总资产收益率"));
            array.add(JSONUtil.createObj().put("index_key", "p_sales").put("index_value", convertToDecimal(p_sales,6)).put("comment", "人均销售收入"));
            array.add(JSONUtil.createObj().put("index_key", "p_profit").put("index_value", convertToDecimal(p_profit,6)).put("comment", "人均利润"));
            //基础指标
            array.add(JSONUtil.createObj().put("index_key", "total_assets").put("index_value", convertToDecimal(total_assets,6)).put("comment", "资产总额"));
            array.add(JSONUtil.createObj().put("index_key", "total_equity").put("index_value", convertToDecimal(total_equity,6)).put("comment", "所有者权益合计"));
            array.add(JSONUtil.createObj().put("index_key", "total_sales").put("index_value", convertToDecimal(total_sales,6)).put("comment", "销售总额(营业总收入)"));
            array.add(JSONUtil.createObj().put("index_key", "total_profit").put("index_value", convertToDecimal(total_profit,6)).put("comment", "利润总额"));
            array.add(JSONUtil.createObj().put("index_key", "prime_bus_profit").put("index_value", convertToDecimal(prime_bus_profit,6)).put("comment", "主营业务收入"));
            array.add(JSONUtil.createObj().put("index_key", "retained_profit").put("index_value", convertToDecimal(retained_profit,6)).put("comment", "净利润"));
            array.add(JSONUtil.createObj().put("index_key", "total_tax").put("index_value", convertToDecimal(total_tax,6)).put("comment", "纳税总额"));
            array.add(JSONUtil.createObj().put("index_key", "total_liability").put("index_value", convertToDecimal(total_liability,6)).put("comment", "负债总额"));
            //2.0新增指标
            array.add(JSONUtil.createObj().put("index_key", "debt_to_equity_ratio").put("index_value", convertToDecimal(debt_to_equity_ratio,6)).put("comment", "产权比率"));
            array.add(JSONUtil.createObj().put("index_key", "equity_multiplier").put("index_value", convertToDecimal(equity_multiplier,6)).put("comment", "权益乘数"));
            array.add(JSONUtil.createObj().put("index_key", "gross_profit_margin").put("index_value", convertToDecimal(gross_profit_margin,6)).put("comment", "营业利润率"));
            array.add(JSONUtil.createObj().put("index_key", "gross_profit_growth_rate").put("index_value", convertToDecimal(gross_profit_growth_rate,6)).put("comment", "营业利润增长率"));
            array.add(JSONUtil.createObj().put("index_key", "capital_accumulation_rate").put("index_value", convertToDecimal(capital_accumulation_rate,6)).put("comment", "资本积累率"));
            array.add(JSONUtil.createObj().put("index_key", "total_asset_growth_rate").put("index_value", convertToDecimal(total_asset_growth_rate,6)).put("comment", "总资产增长率"));
            array.add(JSONUtil.createObj().put("index_key", "net_profit_growth_rate").put("index_value", convertToDecimal(net_profit_growth_rate,6)).put("comment", "净利润增长率"));

            if (JSONUtil.isTypeJSON(context)) {
                entries = JSONUtil.parseObj(context);
            } else {
                entries = JSONUtil.createObj();

            }
            entries.put("report_year",report_year).put("province", province).put("city", city).put("industry_code_1", industry_code_1).put("industry_name_1", industry_name_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries.put("index", array).toString();
    }

    public static boolean isBlankIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else {
            return obj instanceof CharSequence ? isBlankorZH((CharSequence)obj) : false;
        }
    }

    public static BigDecimal convertToDecimal(Object value, int decimalPlaces) {
        try {
            if (value == null) {
                return null;
            }

            Double doubleValue;
            if (value instanceof Double) {
                doubleValue = (Double) value;
            } else if (value instanceof String) {
                doubleValue = Double.parseDouble((String) value);
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
            }

            DecimalFormat df = new DecimalFormat("#." + new String(new char[decimalPlaces]).replace('\0', '0'));
            String formattedValue = df.format(doubleValue);
            return new BigDecimal(formattedValue);
        } catch (Exception e){
            return null;
        }
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

    public static boolean isNum(String str) {

        return str.matches("[-+]?\\d*\\.?\\d+");
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

}
