package com.zlx.modules.person.util;

public class getScore {
    public  static String evaluate (String inc_total_sales, String avg_total_sales, String avg_total_equity,
                              String roe, String loar, String net_profit_margin,String total_asset_turnover) {


        // 近2年平均营业总收入
        double score_avg_total_sales = 0;
        if(avg_total_sales != null && avg_total_sales != "" && CommonUtil.isNumOrRange(avg_total_sales)){
            Double bigDecimal_avg_total_sales = CommonUtil.getAvg(avg_total_sales);
            if (bigDecimal_avg_total_sales <= 500) {
                score_avg_total_sales = 1;
            }else if (bigDecimal_avg_total_sales > 500 && bigDecimal_avg_total_sales <= 1000) {
                score_avg_total_sales = 2;
            }else if (bigDecimal_avg_total_sales > 1000 && bigDecimal_avg_total_sales <= 5000) {
                score_avg_total_sales = 3;
            }else if (bigDecimal_avg_total_sales > 5000 && bigDecimal_avg_total_sales <= 10000) {
                score_avg_total_sales = 4;
            }else {
                score_avg_total_sales = 5;
            }
        }
        // 平均净资产
        double score_avg_total_equity = 0;
        if(avg_total_equity != null && avg_total_equity != "" && CommonUtil.isNumOrRange(avg_total_equity)){
            Double bigDecimal_avg_total_equity = CommonUtil.getAvg(avg_total_equity);
            if (bigDecimal_avg_total_equity <= 500) {
                score_avg_total_equity = 1;
            }else if (bigDecimal_avg_total_equity > 500 && bigDecimal_avg_total_equity <= 5000) {
                score_avg_total_equity = 2;
            }else if (bigDecimal_avg_total_equity > 5000 && bigDecimal_avg_total_equity <= 10000) {
                score_avg_total_equity = 4;
            }else {
                score_avg_total_equity = 5;
            }
        }
        // 资产负债率
        double score_loar = 0;
        if(loar != null && loar != "" && CommonUtil.isNumOrRange(loar)){
            Double bigDecimal_loar = CommonUtil.getAvg(loar);
            if(bigDecimal_loar > 1) {
                score_loar = 0;
            }else if (bigDecimal_loar > 0.5 && bigDecimal_loar <= 1) {
                score_loar =  8-8*bigDecimal_loar;
            }else {
                score_loar = 4;
            }
        }

        // 净利润率
        double score_net_profit_margin = 0;
        if(net_profit_margin != null && net_profit_margin != "" && CommonUtil.isNumOrRange(net_profit_margin)) {
            Double bigDecimal_net_profit_margin = CommonUtil.getAvg(net_profit_margin);
            if(bigDecimal_net_profit_margin < 0) {
                score_net_profit_margin = 0;
            }else if(bigDecimal_net_profit_margin >=0 && bigDecimal_net_profit_margin <0.11){
                score_net_profit_margin = 5*bigDecimal_net_profit_margin/0.11;
            }else {
                score_net_profit_margin = 5;
            }
        }

        // 净资产收益率
        double score_roe = 0;
        if(roe != null && roe != "" && CommonUtil.isNumOrRange(roe)) {
            Double bigDecimal_roe = CommonUtil.getAvg(roe);
            if(bigDecimal_roe < 0 ) {
                score_roe = 0;
            }else if(bigDecimal_roe >= 0 && bigDecimal_roe < 0.13 ) {
                score_roe = 4*bigDecimal_roe/0.13;
            }else {
                score_roe = 4;
            }
        }
        // 总资产周转率
        double score_total_asset_turnover = 0;
        if(total_asset_turnover != null && total_asset_turnover != "" && CommonUtil.isNumOrRange(total_asset_turnover)) {
            Double bigDecimal_total_asset_turnover = CommonUtil.getAvg(total_asset_turnover);
            if(bigDecimal_total_asset_turnover < 0 ) {
                score_total_asset_turnover = 0;
            }else if(bigDecimal_total_asset_turnover >= 0 && bigDecimal_total_asset_turnover < 0.5 ) {
                score_total_asset_turnover = 12*bigDecimal_total_asset_turnover;
            }else {
                score_total_asset_turnover = 6;
            }
        }
        // 收入增长率
        double score_inc_total_sales = 0;
        if(inc_total_sales != null && inc_total_sales != "" && CommonUtil.isNumOrRange(inc_total_sales)) {
            Double bigDecimal_inc_total_sales = CommonUtil.getAvg(inc_total_sales);
            if(bigDecimal_inc_total_sales < 0 ) {
                score_inc_total_sales = 0;
            }else if(bigDecimal_inc_total_sales >= 0 && bigDecimal_inc_total_sales < 0.5 ) {
                score_inc_total_sales = 10*bigDecimal_inc_total_sales;
            }else {
                score_inc_total_sales = 5;
            }
        }
        double total_score = score_avg_total_sales + score_avg_total_equity + score_loar + score_net_profit_margin + score_roe + score_total_asset_turnover
                + score_inc_total_sales;

        long round = Math.round(total_score * 100 / 34);


        return round+"";
    }
}
