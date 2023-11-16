package com.zlx.modules.person.service;

import cn.hutool.core.util.StrUtil;
import com.zlx.modules.person.domain.DwdFixAsset;
import com.zlx.modules.person.domain.DwmAsset;
import com.zlx.modules.person.domain.DwsAsset;
import com.zlx.modules.person.util.*;
import com.zlx.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class AssetService {

    //将修改记录的数据写入到dwd和dws  应用接口调用
    public void inserAsset2Dwm(DwdFixAsset dwdFixAsset) {
        // 获取修改后的指标，在获取原始指标

        String companyId = dwdFixAsset.getCompanyId();
        int reportYear = dwdFixAsset.getReportYear();

        String sql = "select * from default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_id = ? and report_year = ?";

        List<DwmAsset> s_dwmAssets = SrUtil.executeDynamicQuery(sql,
                DwmAsset.class, "nofix",dwdFixAsset.getCompanyId(),dwdFixAsset.getReportYear());

        DwmAsset dwmAsset = new DwmAsset();
        if(s_dwmAssets.size() == 0) {
            BeanUtils.copyProperties(dwdFixAsset, dwmAsset);
        }else {
            BeanUtils.copyProperties(s_dwmAssets.get(0), dwmAsset);
            dwmAsset.setTotalAssets(dwdFixAsset.getTotalAssets());
            dwmAsset.setTotalEquity(dwdFixAsset.getTotalEquity());
            dwmAsset.setTotalSales(dwdFixAsset.getTotalSales());
            dwmAsset.setTotalProfit(dwdFixAsset.getTotalProfit());
            dwmAsset.setPrimeBusProfit(dwdFixAsset.getPrimeBusProfit());
            dwmAsset.setRetainedProfit(dwdFixAsset.getRetainedProfit());
            dwmAsset.setTotalTax(dwdFixAsset.getTotalTax());
            dwmAsset.setTotalLiability(dwdFixAsset.getTotalLiability());
        }
        // 将数据写入到dwm
        String insertSql = "INSERT INTO `assets`.`dwm_oi_company_finance_asset_year`\n" +
                "    (`uscd`, `report_year`, `company_id`, `company_name`, `total_assets`, `total_equity`,\n" +
                "    `total_sales`, `total_profit`, `prime_bus_profit`, `retained_profit`, `total_tax`, \n" +
                "    `total_liability`, `province`, `city`, `industry_l1_name`, `industry_l4_code`,\n" +
                "    `is_pub`, `qa_flag`, `company_major_type`, `stock_status`, `extend_info`)\n" +
                "VALUES\n" +
                "    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n";

        int rowsInserted = SrUtil.executeDynamicInsert(insertSql, dwmAsset);

        String s_insertSql = "INSERT INTO dwd_oi_company_fix_app_finance_asset_year " +
                "(uscd, fix_version, report_year, company_id, company_name, total_assets, total_equity, total_sales, " +
                "total_profit, prime_bus_profit, retained_profit, total_tax, total_liability, province, " +
                "city, industry_l1_name, industry_l4_code, is_pub, qa_flag, company_major_type, stock_status, " +
                "fix_owner, fix_time, fix_app, extend_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        dwdFixAsset.setFixApp("1");
        dwdFixAsset.setFixVersion("1");
        int s_rowsInserted = SrUtil.executeDynamicInsert(s_insertSql, dwdFixAsset);
    }

    //根据dwm重新计算dws的数据
    public  void insertAsset2Dws(String companyId) {

        // 从dwm获取该企业的所有数并计算
        String sql = "select * from default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_id = ? ";
        List<DwmAsset> s_dwmAssets = SrUtil.executeDynamicQuery(sql,
                DwmAsset.class, "nofix",companyId);
        // 获取近四年的数据重新计算
        s_dwmAssets.sort(Comparator.comparing(DwmAsset::getReportYear).reversed());
        //开始计算指标
        ArrayList<DwsAsset> dwsAssets = new ArrayList<>();
        for(int i = 0; i < s_dwmAssets.size();i++) {
            DwsAsset dwsAsset = new DwsAsset();
            DwmAsset currentYear = s_dwmAssets.get(i);
            BeanUtils.copyProperties(currentYear,dwsAsset);
            DwmAsset previousYear = null;

            if(i != s_dwmAssets.size()-1){
                previousYear = s_dwmAssets.get(i+1);
            }else{
                previousYear = new DwmAsset();
            }
            //开始插入指标计算了
            String result = AssetIndex.exec(currentYear.getReportYear()+"", convertBigDecimalToString(currentYear.getTotalAssets()), convertBigDecimalToString(currentYear.getTotalEquity()), convertBigDecimalToString(currentYear.getTotalSales()), convertBigDecimalToString(currentYear.getTotalProfit()), convertBigDecimalToString(currentYear.getPrimeBusProfit()), convertBigDecimalToString(currentYear.getRetainedProfit()),
                    convertBigDecimalToString(currentYear.getTotalTax()), convertBigDecimalToString(currentYear.getTotalLiability()), null,
                    convertBigDecimalToString(previousYear.getTotalAssets()), convertBigDecimalToString(previousYear.getTotalEquity()), convertBigDecimalToString(previousYear.getTotalSales()), convertBigDecimalToString(previousYear.getTotalProfit()), convertBigDecimalToString(previousYear.getRetainedProfit()),
                    currentYear.getExtendInfo(), currentYear.getProvince(), currentYear.getCity(), currentYear.getIndustryL1Name(), currentYear.getIndustryL4Code());
            dwsAsset.setExtendInfo(result);

            dwsAsset.setIncTotalSales(convertToBigDecimal(GetAssetIndex.evaluate(result,"inc_total_sales")));
            dwsAsset.setAvgTotalSales(convertToBigDecimal(GetAssetIndex.evaluate(result,"avg_total_sales")));
            dwsAsset.setAvgTotalEquity(convertToBigDecimal(GetAssetIndex.evaluate(result,"avg_total_equity")));
            dwsAsset.setRoe(convertToBigDecimal(GetAssetIndex.evaluate(result,"roe")));
            dwsAsset.setLoar(convertToBigDecimal(GetAssetIndex.evaluate(result,"loar")));
            dwsAsset.setNetProfitMargin(convertToBigDecimal(GetAssetIndex.evaluate(result,"net_profit_margin")));
            dwsAsset.setTotalAssetTurnover(convertToBigDecimal(GetAssetIndex.evaluate(result,"total_asset_turnover")));
            dwsAsset.setRoa(convertToBigDecimal(GetAssetIndex.evaluate(result,"roa")));

            dwsAsset.setCreateTime("2023-01-01 00:00:00");
            dwsAsset.setUpdateTime(DateUtil.localDateTimeFormatyMdHms(DateUtil.toLocalDateTime(DateUtil.toDate(LocalDateTime.now()))));
            dwsAsset.setUseFlag("0");
            dwsAssets.add(dwsAsset);
        }

        dwsAssets.sort(Comparator.comparing(DwsAsset::getReportYear).reversed());
        //开始计算分数
        DwsAsset latest_dwsAsset = dwsAssets.get(0);
        String score = getScore.evaluate(
                convertBigDecimalToString(latest_dwsAsset.getIncTotalSales()),
                convertBigDecimalToString(latest_dwsAsset.getAvgTotalSales()),
                convertBigDecimalToString(latest_dwsAsset.getAvgTotalEquity()),
                convertBigDecimalToString(latest_dwsAsset.getRoe()),
                convertBigDecimalToString(latest_dwsAsset.getLoar()),
                convertBigDecimalToString(latest_dwsAsset.getNetProfitMargin()),
                convertBigDecimalToString(latest_dwsAsset.getTotalAssetTurnover()));

        System.out.println("分数："+score);

        // 计算分数描述 要拿最近一年的近三年的数据
        DwsAsset pre_1_dwsAsset = null;
        DwsAsset pre_2_dwsAsset = null;
        if(dwsAssets.size()>1) {
             pre_1_dwsAsset = dwsAssets.get(1);
        }else{
            pre_1_dwsAsset = new DwsAsset();
        }
        if(dwsAssets.size()>2) {
             pre_2_dwsAsset = dwsAssets.get(2);
        }else{
            pre_2_dwsAsset = new DwsAsset();
        }
        String core_manage_msg = getCorpManageMsg.evaluate(
                convertBigDecimalToString(latest_dwsAsset.getTotalSales()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalSales()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalSales()),
                "销售总额",
                convertBigDecimalToString(latest_dwsAsset.getPrimeBusProfit()),
                convertBigDecimalToString(pre_1_dwsAsset.getPrimeBusProfit()),
                convertBigDecimalToString(pre_2_dwsAsset.getPrimeBusProfit()),
                "主营业务收入",
                convertBigDecimalToString(latest_dwsAsset.getTotalProfit()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalProfit()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalProfit()),
                "利润总额",
                convertBigDecimalToString(latest_dwsAsset.getRetainedProfit()),
                convertBigDecimalToString(pre_1_dwsAsset.getRetainedProfit()),
                convertBigDecimalToString(pre_2_dwsAsset.getRetainedProfit()),
                "净利润",
                convertBigDecimalToString(latest_dwsAsset.getTotalTax()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalTax()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalTax()),
                "纳税总额"
        );
        String asset_msg = getAssetMsg.evaluate(
                convertBigDecimalToString(latest_dwsAsset.getTotalAssets()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalAssets()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalAssets()),
                "资产总额",
                convertBigDecimalToString(latest_dwsAsset.getTotalEquity()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalEquity()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalEquity()),
                "所有者权益合计",
                convertBigDecimalToString(latest_dwsAsset.getTotalLiability()),
                convertBigDecimalToString(pre_1_dwsAsset.getTotalLiability()),
                convertBigDecimalToString(pre_2_dwsAsset.getTotalLiability()),
                "负债总额"
        );
        String corp_score_msg = "";
        if(!StrUtil.isBlank(score)){
            Double aDouble = Double.valueOf(score);
            if(aDouble >= 60){
                corp_score_msg = "综合分析下，该企业评分优秀";
            }else if(aDouble >=30 && aDouble <60){
                corp_score_msg = "综合分析下，该企业评分良好";
            }else {
                corp_score_msg = "综合分析下，该企业评分较差";
            }
        }else {
            corp_score_msg = "综合分析下，该企业评分较差";
        }
        //生成新的指标列表
        String score_msg = asset_msg+core_manage_msg+corp_score_msg;

        for(int i = 0; i < dwsAssets.size();i++) {
            DwsAsset currentYear = dwsAssets.get(i);
            DwsAsset previousYear = null;
            if(i+1 < dwsAssets.size()){
                previousYear = dwsAssets.get(i+1);
            }else{
                previousYear = new DwsAsset();
            }

            DwsAsset previous_2_Year = null;
            if(i+2 < dwsAssets.size()){
                previous_2_Year = dwsAssets.get(i+2);
            }else{
                previous_2_Year = new DwsAsset();
            }
            // 开始计算变异系数 拿当前年份的近三年数据
            String new_context = AssetIndexCovContext.evaluate(currentYear.getExtendInfo(), previousYear.getExtendInfo(), previous_2_Year.getExtendInfo());
            currentYear.setExtendInfo(new_context);
            // 赋值分数
            currentYear.setScore(score != null ? Integer.valueOf(score) : null);
            currentYear.setScoreMsg(score_msg);
            String s_insertDwsSql = "INSERT INTO assets.dws_oi_company_finance_asset_year VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SrUtil.executeDynamicInsert(s_insertDwsSql, currentYear);
        }





    }

    public static String convertBigDecimalToString(BigDecimal value) {
        return value != null ? value.toString() : null;
    }
    public static BigDecimal convertToBigDecimal(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }



}
