package com.zlx.modules.person.rest;


import com.zlx.modules.person.domain.DwdFixAsset;
import com.zlx.modules.person.domain.DwmAsset;
import com.zlx.modules.person.service.AssetService;
import com.zlx.modules.person.util.SrUtil;
import com.zlx.utils.DateUtil;
import com.zlx.utils.PageResult;
import com.zlx.utils.PageUtil;
import com.zlx.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "个性服务：财务管理")
@RequestMapping("/api/asset")
public class AssetController {


    @Autowired
    private AssetService assetService;

    @ApiOperation(value = "查询财务")
    @GetMapping
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<PageResult<DwmAsset>> queryAssets(String companyName, Pageable pageable){
        if("".equals(companyName) || companyName == null) {
            companyName = "中国工商银行股份有限公司";
        }

//        String sql = "select * from default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_name like ?  limit 200";
//        List<DwmAsset> dwmAssets = SrUtil.executeDynamicQuery(sql,
//                DwmAsset.class, "fix",companyName);

        String sql = "SELECT * FROM default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_name LIKE ? ORDER BY " +
                "CASE " +
                "WHEN company_name = ? THEN 1 " +        // 完全匹配
                "WHEN company_name LIKE ? THEN 2 " +     // 开头匹配
                "ELSE 3 " +                              // 其他情况
                "END LIMIT 200";
        String likePattern = "%" + companyName + "%";
        String likePattern1 = companyName + "%";
        List<DwmAsset> dwmAssets = SrUtil.executeDynamicQuery(sql, DwmAsset.class, "nofix", likePattern, companyName, likePattern1);





        PageResult<DwmAsset> dwmAssetPageResult = PageUtil.toPage(
                PageUtil.paging(pageable.getPageNumber(), pageable.getPageSize(), dwmAssets),
                dwmAssets.size()
        );

        return new ResponseEntity<>(dwmAssetPageResult,HttpStatus.OK);
    }

    @ApiOperation(value = "删除财务数据")
    @DeleteMapping
    @PreAuthorize("@el.check('asset:del')")
    public ResponseEntity<Object> deleteAssets(@RequestBody String companyId, String reportYear){
        System.out.println(companyId);
        System.out.println(reportYear);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation(value = "查询财务修改记录")
    @GetMapping(value = "/detail")
    @PreAuthorize("@el.check('asset:detail')")
    public ResponseEntity<PageResult<DwdFixAsset>> queryAssetsDetailByAsset(String companyId,String reportYear){

        //查询财务数据修改记录
        String sql = "select * from default_catalog.assets.dwd_oi_company_fix_app_finance_asset_year WHERE company_id = ? and report_year = ?";
        List<DwdFixAsset> dwdFixAsset = SrUtil.executeDynamicQuery(sql,DwdFixAsset.class,"nofix",
                companyId,reportYear);

        PageResult<DwdFixAsset> dwdFixAssetResult = PageUtil.toPage(
                PageUtil.pagingAll( dwdFixAsset),
                dwdFixAsset.size()
        );

        return new ResponseEntity<>(dwdFixAssetResult,HttpStatus.OK);
    }


    @ApiOperation(value = "新增财务数据，原来不存在")
    @PostMapping ()
    @PreAuthorize("@el.check('asset:addDetail')")
    public ResponseEntity<Object> addAssetsByWithout(@Validated @RequestBody DwmAsset dwmAsset){
        System.out.println("新增财务数据");
        DwdFixAsset dwdFixAsset = new DwdFixAsset();
        //BeanUtils.copyProperties(dwmAsset, dwdFixAsset);
        //获取当前登录用户
        String currentUsername = SecurityUtils.getCurrentUsername();
        dwdFixAsset.setFixOwner(currentUsername);
        dwdFixAsset.setFixTime(DateUtil.localDateTimeFormatyMdHms(DateUtil.toLocalDateTime(DateUtil.toDate(LocalDateTime.now()))));
        dwdFixAsset.setFixVersion("0");
        dwdFixAsset.setFixApp("9");
        dwdFixAsset.setCompanyId(dwmAsset.getCompanyId());
        dwdFixAsset.setCompanyName(dwmAsset.getCompanyName());
        dwdFixAsset.setReportYear(dwmAsset.getReportYear());
        dwdFixAsset.setUscd(dwmAsset.getUscd());
        String insertSql = "INSERT INTO dwd_oi_company_fix_app_finance_asset_year " +
                "(uscd, fix_version, report_year, company_id, company_name, total_assets, total_equity, total_sales, " +
                "total_profit, prime_bus_profit, retained_profit, total_tax, total_liability, province, " +
                "city, industry_l1_name, industry_l4_code, is_pub, qa_flag, company_major_type, stock_status, " +
                "fix_owner, fix_time, fix_app, extend_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int rowsInserted = SrUtil.executeDynamicInsert(insertSql, dwdFixAsset);

        String sql = "select * from default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_id = ? and report_year = ?";
        System.out.println(dwmAsset);
        List<DwmAsset> s_dwmAssets = SrUtil.executeDynamicQuery(sql,
                DwmAsset.class, "nofix",dwdFixAsset.getCompanyId(),dwdFixAsset.getReportYear());

        DwdFixAsset s_dwdFixAsset = new DwdFixAsset();
        if(s_dwmAssets.size() == 0){
            BeanUtils.copyProperties(dwmAsset, s_dwdFixAsset);
        }else{
            DwmAsset s_dwmAsset = s_dwmAssets.get(0);
            BeanUtils.copyProperties(s_dwmAsset, s_dwdFixAsset);
        }


        s_dwdFixAsset.setFixApp("1");
        s_dwdFixAsset.setFixVersion("1");
        s_dwdFixAsset.setFixTime(DateUtil.localDateTimeFormatyMdHms(DateUtil.toLocalDateTime(DateUtil.toDate(LocalDateTime.now()))));
        s_dwdFixAsset.setFixOwner(currentUsername);

        String s_insertSql = "INSERT INTO dwd_oi_company_fix_app_finance_asset_year " +
                "(uscd, fix_version, report_year, company_id, company_name, total_assets, total_equity, total_sales, " +
                "total_profit, prime_bus_profit, retained_profit, total_tax, total_liability, province, " +
                "city, industry_l1_name, industry_l4_code, is_pub, qa_flag, company_major_type, stock_status, " +
                "fix_owner, fix_time, fix_app, extend_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int s_rowsInserted = SrUtil.executeDynamicInsert(s_insertSql, s_dwdFixAsset);


        // 将数据写入到dwm
        String dwm_insertSql = "INSERT INTO `assets`.`dwm_oi_company_finance_asset_year`\n" +
                "    (`uscd`, `report_year`, `company_id`, `company_name`, `total_assets`, `total_equity`,\n" +
                "    `total_sales`, `total_profit`, `prime_bus_profit`, `retained_profit`, `total_tax`, \n" +
                "    `total_liability`, `province`, `city`, `industry_l1_name`, `industry_l4_code`,\n" +
                "    `is_pub`, `qa_flag`, `company_major_type`, `stock_status`, `extend_info`)\n" +
                "VALUES\n" +
                "    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n";

        int dwm_rowsInserted = SrUtil.executeDynamicInsert(dwm_insertSql, dwmAsset);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "修改存在的财务数")
    @PutMapping ()
    @PreAuthorize("@el.check('asset:addDetail')")
    public ResponseEntity<Object> addAssetsDetail(@Validated @RequestBody DwmAsset dwmAsset){
        System.out.println("修改存在的财务数据");
        DwdFixAsset dwdFixAsset = new DwdFixAsset();
        BeanUtils.copyProperties(dwmAsset, dwdFixAsset);
        //获取当前登录用户
        String currentUsername = SecurityUtils.getCurrentUsername();
        dwdFixAsset.setFixOwner(currentUsername);
        dwdFixAsset.setFixTime(DateUtil.localDateTimeFormatyMdHms(DateUtil.toLocalDateTime(DateUtil.toDate(LocalDateTime.now()))));
        dwdFixAsset.setFixVersion("1");
        dwdFixAsset.setFixApp("0");
        String insertSql = "INSERT INTO dwd_oi_company_fix_app_finance_asset_year " +
                "(uscd, fix_version, report_year, company_id, company_name, total_assets, total_equity, total_sales, " +
                "total_profit, prime_bus_profit, retained_profit, total_tax, total_liability, province, " +
                "city, industry_l1_name, industry_l4_code, is_pub, qa_flag, company_major_type, stock_status, " +
                "fix_owner, fix_time, fix_app, extend_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int rowsInserted = SrUtil.executeDynamicInsert(insertSql, dwdFixAsset);

        String sql = "select * from default_catalog.assets.dwm_oi_company_finance_asset_year WHERE company_id = ? and report_year = ?";
        System.out.println(dwmAsset);
        List<DwmAsset> s_dwmAssets = SrUtil.executeDynamicQuery(sql,
                DwmAsset.class, "nofix",dwdFixAsset.getCompanyId(),dwdFixAsset.getReportYear());
        DwmAsset s_dwmAsset = s_dwmAssets.get(0);

        DwdFixAsset s_dwdFixAsset = new DwdFixAsset();
        BeanUtils.copyProperties(s_dwmAsset, s_dwdFixAsset);

        s_dwdFixAsset.setFixApp("9");
        s_dwdFixAsset.setFixVersion("0");
        s_dwdFixAsset.setFixTime("");
        s_dwdFixAsset.setFixOwner("system");

        String s_insertSql = "INSERT INTO dwd_oi_company_fix_app_finance_asset_year " +
                "(uscd, fix_version, report_year, company_id, company_name, total_assets, total_equity, total_sales, " +
                "total_profit, prime_bus_profit, retained_profit, total_tax, total_liability, province, " +
                "city, industry_l1_name, industry_l4_code, is_pub, qa_flag, company_major_type, stock_status, " +
                "fix_owner, fix_time, fix_app, extend_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int s_rowsInserted = SrUtil.executeDynamicInsert(s_insertSql, s_dwdFixAsset);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @ApiOperation(value = "应用财务数据")
    @PutMapping (value = "/apply")
    @PreAuthorize("@el.check('asset:apply')")
    public ResponseEntity<Object> assetApply(@Validated @RequestBody DwdFixAsset dwdFixAsset){
        assetService.inserAsset2Dwm(dwdFixAsset);
        assetService.insertAsset2Dws(dwdFixAsset.getCompanyId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
