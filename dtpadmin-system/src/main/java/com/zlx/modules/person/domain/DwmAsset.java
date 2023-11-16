package com.zlx.modules.person.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DwmAsset {
    private String uscd;
    private Integer reportYear;
    private String companyId;
    private String companyName;
    private BigDecimal totalAssets;
    private BigDecimal totalEquity;
    private BigDecimal totalSales;
    private BigDecimal totalProfit;
    private BigDecimal primeBusProfit;
    private BigDecimal retainedProfit;
    private BigDecimal totalTax;
    private BigDecimal totalLiability;
    private String province;
    private String city;
    private String industryL1Name;
    private String industryL4Code;
    private String isPub;
    private String qaFlag;
    private Integer companyMajorType;
    private String stockStatus;
    private String extendInfo;
}
