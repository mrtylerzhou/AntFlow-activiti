package org.openoa.engine.bpmnconf.controller;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.dto.DictDataPageReq;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.DictDataSaveVo;
import org.openoa.base.vo.DictDataVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.biz.DictDataBizServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典管理
 * 管理 t_dict_data 表(分页/新增/编辑/删除)
 * 规则: lowcodeflow 系统数据禁止编辑/删除; dict_type+dict_label+dict_value 唯一性校验
 */
@Slf4j
@RestController
@RequestMapping(value = "/dictData")
public class DictDataController {

    @Autowired
    private DictDataBizServiceImpl dictDataBizService;

    /**
     * 分页列表
     */
    @PostMapping("/listPage")
    public ResultAndPage<DictDataVo> listPage(@RequestBody DictDataPageReq req) {
        return dictDataBizService.listPage(req);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    public Result save(@RequestBody DictDataSaveVo vo) {
        try {
            Long id = dictDataBizService.save(vo);
            return Result.newSuccessResult(id);
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 编辑
     */
    @PostMapping("/update")
    public Result update(@RequestBody DictDataSaveVo vo) {
        try {
            dictDataBizService.update(vo);
            return Result.newSuccessResult("ok");
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }

    /**
     * 删除(逻辑删除 is_del=1, lowcodeflow 系统数据拒绝)
     */
    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        try {
            dictDataBizService.delete(id);
            return Result.newSuccessResult("ok");
        } catch (AFBizException e) {
            return Result.newFailureResult(e.getCode(), e.getMessage());
        }
    }
}
