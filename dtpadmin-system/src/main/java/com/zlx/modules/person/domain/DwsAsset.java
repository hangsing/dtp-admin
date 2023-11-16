package com.zlx.modules.person.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DwsAsset{
    private String uscd;
    private Integer reportYear;
    private String companyId;
    private String companyName;
    private BigDecimal incTotalSales;
    private BigDecimal avgTotalSales;
    private BigDecimal avgTotalEquity;
    private BigDecimal roe;
    private BigDecimal loar;
    private BigDecimal netProfitMargin;
    private BigDecimal totalAssetTurnover;
    private BigDecimal roa;
    private BigDecimal totalAssets;
    private BigDecimal totalEquity;
    private BigDecimal totalSales;
    private BigDecimal totalProfit;
    private BigDecimal primeBusProfit;
    private BigDecimal retainedProfit;
    private BigDecimal totalTax;
    private BigDecimal totalLiability;
    private Integer employeeNo;
    private BigDecimal pSales;
    private BigDecimal pProfit;
    private String createTime;
    private String updateTime;
    private String useFlag;
    private Integer score;
    private String scoreMsg;
    private String extendInfo;
}
