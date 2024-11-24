package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;
import org.openoa.engine.vo.BpmProcessCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: jwz
 * @Date: 2024/9/9 18:02
 * @Description:
 * @Version: 1.0.0
 */
@RequestMapping("/processMgmt")
@RestController
public class BpmProcessDeptController {

    @Autowired
    private BpmProcessDeptServiceImpl processDeptService;
    @Autowired
    private BpmProcessCategoryServiceImpl processCategoryService;
    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;
    @Autowired
    private BpmProcessApplicationTypeServiceImpl bpmProcessApplicationTypeService;
    @Autowired
    private BpmProcessAppDataServiceImpl bpmProcessAppDataService;


    /**
     * 流程图标下面弄一个配置选项,保存流程权限(目前尚未实现),流程通知类型
     * @param vo
     * @return
     */
    @PostMapping("/taskMgmt")
    public Result saveProcessNotices(@RequestBody BpmProcessDeptVo vo){
        processDeptService.editProcessConf(vo);
        return Result.success();
    }
    /*
   /**
    * 分页查询流程配置表
    * @return
    */
    @PostMapping("/process/listConfigure")
    public Result list(@RequestBody BpmProcessDeptVo vo) throws JiMuBizException {
        return Result.newSuccessResult(processDeptService.listPage(vo));
    }
    /**
     * 流程配置表保存/修改
     */
    @PostMapping("/process/editProcess")
    public Result saveOrEdit(@RequestBody BpmProcessDeptVo vo) throws JiMuBizException {
        return Result.success(processDeptService.editProcess(vo));
    }
    /**
     * 查询流程配置数据
     *
     * @param id
     * @return
     * @throws JiMuBizException
     */
    @GetMapping("process/selectConfigure/{id}")
    public Result selectConfigure(@PathVariable Integer id) throws JiMuBizException {
        return Result.success(processDeptService.findBpmProcessDeptById(id));
    }
    /**
     * 申请流程入口
     */
    @GetMapping("/process/processData")
    public Result viewBpmProcessDept() {
        return Result.success(processDeptService.viewBpmProcessDeptVo());
    }
    /**
     * 添加类别
     */
    @PostMapping("/process/editCategory")
    public Result editProcessCategory(@RequestBody BpmProcessCategoryVo vo) throws JiMuBizException {
        return Result.success(processCategoryService.editProcessCategory(vo));
    }
    /**
     * 类别操作
     */
    @PostMapping("/process/categoryOperation")
    public Result categoryOperation(@RequestBody BpmProcessCategoryVo vo) throws JiMuBizException {
        return Result.success(processCategoryService.categoryOperation(vo.getType(), vo.getId()));
    }
    /**
     * 类别列表
     */
    @GetMapping("/process/categoryList")
    public Result processCategoryList() throws JiMuBizException {
        return Result.success(processCategoryService.processCategoryVos(BpmProcessCategoryVo.builder().build()));
    }
    /**
     * 应用添加/修改
     */
    @PostMapping("/process/editAppIcon")
    public Result editProcessApplicationType(@RequestBody BpmProcessAppApplicationVo vo) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.addBpmProcessAppApplication(vo));
    }
    /**
     * 应用删除
     */
    @GetMapping("/process/deleteAppIcon/{id}")
    public Result processCategoryList(@PathVariable Long id) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.deleteAppIcon(id));
    }
    /**
     * 应用操作
     */
    @PostMapping("/process/editIcon")
    public Result editIcon(@RequestBody BpmProcessApplicationTypeVo vo) throws JiMuBizException {
        return Result.success(bpmProcessApplicationTypeService.iconOperation(vo));
    }
    /**
     * 应用列表
     */
    @GetMapping("/process/iconListPage")
    public Result applicationsList(PageDto page, BpmProcessAppApplicationVo vo) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.applicationsList(page, vo));
    }
    /**
     * 应用列表
     */
    @GetMapping("/process/appIcon")
    public Result appIcon(@RequestHeader("appversion") String version) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.list(version));
    }
    /**
     * 流程应用列表
     */
    @GetMapping("/process/processApplicationList")
    public Result processTypeList() throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.processApplicationList());
    }
    /**
     * app/pc配置列表
     */
    @GetMapping("/process/allIcon")
    public Result iconConfig(BpmProcessAppApplicationVo vo) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.iconConfig(Integer.parseInt(vo.getIsApp()), vo.getParentId(), vo.getProcessCategoryId()));
    }
    /**
     * PC首页常用功能
     */
    @GetMapping("/process/homePageIcon")
    public Result homePageIcon(BpmProcessAppApplicationVo vo) throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.homePageIcon(vo));
    }
    /**
     * 应用列表
     * 包括用应用创建的时间来区分应用的不同版本
     */
    @GetMapping("/process/listProcessAppApplication")
    public Result listProcessAppApplication() throws JiMuBizException {
        return Result.success(bpmProcessAppApplicationService.listProcessApplication());
    }

    /**
     * 流程配置表保存/修改
     */
    @PostMapping("/process/editBpmnConf")
    public Result editBpmnConf(@RequestBody BpmProcessDeptVo vo) throws JiMuBizException {
        processDeptService.editProcessConf(vo);
        return Result.success();
    }
    /**
     * 查询流程配置数据
     *
     * @param bpmnConfId
     * @return
     * @throws JiMuBizException
     */
    @GetMapping("process/findConf/{bpmnConfId}")
    public Result getBpmnConf(@PathVariable Integer bpmnConfId) throws JiMuBizException {
        return Result.success(processDeptService.getBpmnConf(bpmnConfId));
    }
    /**
     * 应用搜索
     */
    @GetMapping("/process/searchIcon")
    public Result allSupplier(@RequestParam(name = "search", required = false) String search,
                            @RequestParam(name = "limitSize", required = false) Integer limitSize) {
        return Result.success(bpmProcessAppApplicationService.listProcessAppApplication(search, limitSize));
    }

    /**
     * 获取app最大版本应用
     */
    @GetMapping("/process/findMaxAppData")
    public Result findMaxAppData() {
        return Result.success(bpmProcessAppDataService.findMaxAppData());
    }
}
