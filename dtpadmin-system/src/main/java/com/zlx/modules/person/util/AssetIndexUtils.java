package com.zlx.modules.person.util;

import cn.hutool.core.util.CharUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetIndexUtils {

    //收入增长率
    public static Double getIncTotalSales(String total_sales, String pre_1_total_sales) {
        Double inc_total_sales = 0.0;

        if (isBlankorZH(pre_1_total_sales) || isBlankorZH(total_sales)) {
            inc_total_sales = null;
        } else if (convertToDouble(pre_1_total_sales) == 0) {
            inc_total_sales = null;
        } else {
            inc_total_sales = (convertToDouble(total_sales) - convertToDouble(pre_1_total_sales)) / Math.abs(convertToDouble(pre_1_total_sales));
        }
        return inc_total_sales;
    }
    //近2年平均营业总收入
    public static Double getAvgTotalSales(String total_sales, String pre_1_total_sales){
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
        return avg_total_sales;
    }
    //平均净资产
    public static Double getAvgTotalEquity(String total_equity, String pre_1_total_equity){
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
        return avg_total_equity;
    }

    //净资产收益率

    public static Double getRoe(String retained_profit, String total_equity,String pre_1_total_equity){
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
        return roe;
    }

    //资产负债率
    public static Double getLoar(String total_liability,String total_assets){
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
        return loar;

    }

    //净利润率
    public static Double getNetProfitMargin(String retained_profit, String total_sales){

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
        return net_profit_margin;
    }

    //总资产周转率
    public static Double getTotalAssetTurnover(String total_sales, String total_assets, String pre_1_total_assets){
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
        return total_asset_turnover;
    }

    //总资产收益率
    public static Double getRoa(String retained_profit, String total_assets,String pre_1_total_assets){

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
        return roa;
    }
    //产权比率
    public static Double getDebtToEquityRatio(String total_liability, String total_equity){
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
        return debt_to_equity_ratio;
    }
    //权益乘数
    public static Double getEquityMultiplier(String total_assets, String total_equity){
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
        return equity_multiplier;
    }
    //营业利润率
    public static Double getGrossProfitMargin(String total_profit, String total_sales){
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
        return gross_profit_margin;
    }
    //营业利润增长率
    public static Double getGrossProfitGrowthRate(String total_profit, String pre_1_total_profit){
        Double gross_profit_growth_rate = 0.0;
        if (isBlankorZH(total_profit) || isBlankorZH(pre_1_total_profit)) {
            gross_profit_growth_rate = null;
        } else if (convertToDouble(total_profit) == 0) {
            gross_profit_growth_rate = null;
        } else {
            gross_profit_growth_rate = (convertToDouble(total_profit) - convertToDouble(pre_1_total_profit)) / Math.abs(convertToDouble(pre_1_total_profit));
        }
        return  gross_profit_growth_rate;
    }
    //资本积累率
    public static Double getCapitalAccumulationRate(String total_equity, String pre_1_total_equity){
        Double capital_accumulation_rate = 0.0;
        if (isBlankorZH(total_equity) || isBlankorZH(pre_1_total_equity)) {
            capital_accumulation_rate = null;
        } else if (convertToDouble(total_equity) == 0) {
            capital_accumulation_rate = null;
        } else {
            capital_accumulation_rate = (convertToDouble(total_equity) - convertToDouble(pre_1_total_equity)) / Math.abs(convertToDouble(pre_1_total_equity));
        }
        return capital_accumulation_rate;
    }
    //总资产增长率
    public static Double getTotalAssetGrowthRate(String total_assets,String pre_1_total_assets){
        Double total_asset_growth_rate = 0.0;
        if (isBlankorZH(total_assets) || isBlankorZH(pre_1_total_assets)) {
            total_asset_growth_rate = null;
        } else if (convertToDouble(total_assets) == 0) {
            total_asset_growth_rate = null;
        } else {
            total_asset_growth_rate = (convertToDouble(total_assets) - convertToDouble(pre_1_total_assets)) / Math.abs(convertToDouble(pre_1_total_assets));
        }
        return total_asset_growth_rate;
    }
    //净利润增长率
    public static Double getNetProfitGrowthRate(String retained_profit, String pre_1_retained_profit){

        Double net_profit_growth_rate = 0.0;
        if (isBlankorZH(retained_profit) || isBlankorZH(pre_1_retained_profit)) {
            net_profit_growth_rate = null;
        } else if (convertToDouble(retained_profit) == 0) {
            net_profit_growth_rate = null;
        } else {
            net_profit_growth_rate = (convertToDouble(retained_profit) - convertToDouble(pre_1_retained_profit)) / Math.abs(convertToDouble(pre_1_retained_profit));
        }
        return net_profit_growth_rate;
    }
    //人均销售收入  这个指标没有意义
    public static Double getPSales(){
        return null;

    }
    //人均利润
    public static Double getPProfit(){
        return null;
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

}
