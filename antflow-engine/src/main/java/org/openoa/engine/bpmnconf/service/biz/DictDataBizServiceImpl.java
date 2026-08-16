package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.AFSpecialDictCategoryEnum;
import org.openoa.base.dto.DictDataPageReq;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.DictData;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.DictDataSaveVo;
import org.openoa.base.vo.DictDataVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.interf.DicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典管理 业务服务
 * 管理 t_dict_data 表(分页/新增/编辑/删除)
 * 规则:
 * - 列表过滤 is_del=0, dict_type 映射汉字含义(后端映射)
 * - lowcodeflow 为系统自动写入数据, 禁止编辑/删除
 * - dict_type+dict_label+dict_value 三者全同(is_del=0 范围)禁止新增/编辑
 */
@Service
@Slf4j
public class DictDataBizServiceImpl {

    @Autowired
    private DicDataService dicDataService;

    // ==================== 列表 ====================

    public ResultAndPage<DictDataVo> listPage(DictDataPageReq req) {
        PageDto pageDto = req.getPageDto() == null ? PageDto.first() : req.getPageDto();
        Page<DictData> page = PageUtils.getPageByPageDto(pageDto);

        //tenantQuery 自带 is_del=0 + 租户过滤
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictData> qw = AFWrappers.lambdaTenantQuery();
        qw.eq(StringUtils.hasText(req.getDictType()), DictData::getDictType, req.getDictType());
        if (StringUtils.hasText(req.getKeyword())) {
            qw.and(w -> w.like(DictData::getLabel, req.getKeyword())
                    .or().like(DictData::getValue, req.getKeyword()));
        }
        qw.orderByAsc(DictData::getSort).orderByDesc(DictData::getId);

        Page<DictData> result = dicDataService.getBaseMapper().selectPage(page, qw);
        List<DictData> records = result.getRecords();
        List<DictDataVo> vos = records.isEmpty()
                ? Collections.emptyList()
                : records.stream().map(this::toVo).collect(Collectors.toList());
        return PageUtils.getResultAndPage(vos, PageUtils.getPageDto(result));
    }

    private DictDataVo toVo(DictData e) {
        return DictDataVo.builder()
                .id(e.getId())
                .dictLabel(e.getLabel())
                .dictValue(e.getValue())
                .dictType(e.getDictType())
                //后端映射汉字含义, 未知类型原样展示
                .dictTypeLabel(AFSpecialDictCategoryEnum.getLabelByDesc(e.getDictType()))
                .sort(e.getSort())
                .remark(e.getRemark())
                .createUser(e.getCreateUser())
                .createTime(e.getCreateTime())
                .updateTime(e.getUpdateTime())
                .build();
    }

    // ==================== 新增 ====================

    @Transactional
    public Long save(DictDataSaveVo vo) {
        validateSaveVo(vo);
        //唯一性校验: dict_type+dict_label+dict_value 三者全同(is_del=0 范围)禁止添加
        if (existsSame(vo.getDictType(), vo.getDictLabel(), vo.getDictValue(), null)) {
            throw new AFBizException("400001", "相同字典数据已存在");
        }
        DictData entity = new DictData();
        entity.setDictType(vo.getDictType());
        entity.setLabel(vo.getDictLabel());
        entity.setValue(vo.getDictValue());
        entity.setSort(vo.getSort() == null ? 0 : vo.getSort());
        entity.setRemark(vo.getRemark());
        entity.setIsDefault("N");
        entity.setIsDel(0);
        entity.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        entity.setCreateTime(new Date());
        entity.setTenantId(MultiTenantUtil.getCurrentTenantId());
        dicDataService.getBaseMapper().insert(entity);
        return entity.getId();
    }

    // ==================== 编辑 ====================

    @Transactional
    public void update(DictDataSaveVo vo) {
        if (vo.getId() == null) {
            throw new AFBizException("400002", "参数错误:缺少主键");
        }
        validateSaveVo(vo);
        DictData exist = dicDataService.getBaseMapper().selectById(vo.getId());
        if (exist == null || Integer.valueOf(1).equals(exist.getIsDel())) {
            throw new AFBizException("400003", "字典数据不存在");
        }
        //lowcodeflow 系统数据禁止编辑
        if (AFSpecialDictCategoryEnum.isLowCodeFlow(exist.getDictType())) {
            throw new AFBizException("400004", "低代码流程数据禁止编辑");
        }
        //唯一性校验(排除自身)
        if (existsSame(vo.getDictType(), vo.getDictLabel(), vo.getDictValue(), vo.getId())) {
            throw new AFBizException("400001", "相同字典数据已存在");
        }
        exist.setDictType(vo.getDictType());
        exist.setLabel(vo.getDictLabel());
        exist.setValue(vo.getDictValue());
        exist.setSort(vo.getSort() == null ? 0 : vo.getSort());
        exist.setRemark(vo.getRemark());
        exist.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        exist.setUpdateTime(new Date());
        dicDataService.getBaseMapper().updateById(exist);
    }

    // ==================== 删除(逻辑删除 is_del=1) ====================

    @Transactional
    public void delete(Long id) {
        DictData exist = dicDataService.getBaseMapper().selectById(id);
        if (exist == null || Integer.valueOf(1).equals(exist.getIsDel())) {
            throw new AFBizException("400003", "字典数据不存在");
        }
        //lowcodeflow 系统数据禁止删除
        if (AFSpecialDictCategoryEnum.isLowCodeFlow(exist.getDictType())) {
            throw new AFBizException("400004", "低代码流程数据禁止删除");
        }
        LambdaUpdateWrapper<DictData> uw = new LambdaUpdateWrapper<>();
        uw.eq(DictData::getId, id)
                .set(DictData::getIsDel, 1)
                .set(DictData::getUpdateUser, SecurityUtils.getLogInEmpNameSafe())
                .set(DictData::getUpdateTime, new Date());
        dicDataService.getBaseMapper().update(null, uw);
    }

    // ==================== 私有方法 ====================

    private void validateSaveVo(DictDataSaveVo vo) {
        if (!StringUtils.hasText(vo.getDictLabel())) {
            throw new AFBizException("400005", "字典标签不能为空");
        }
        if (!StringUtils.hasText(vo.getDictValue())) {
            throw new AFBizException("400006", "字典键值不能为空");
        }
        if (!StringUtils.hasText(vo.getDictType())) {
            throw new AFBizException("400007", "字典类型不能为空");
        }
        //lowcodeflow 系统自动写入, 不允许手动新增
        if (AFSpecialDictCategoryEnum.isLowCodeFlow(vo.getDictType())) {
            throw new AFBizException("400008", "低代码流程类型不允许手动新增");
        }
        //仅允许 udr/processlabel 两个手动类型
        if (AFSpecialDictCategoryEnum.getLabelByDesc(vo.getDictType()) == null) {
            throw new AFBizException("400009", "字典类型不合法");
        }
    }

    /**
     * 唯一性校验: dict_type+dict_label+dict_value 三者全同(is_del=0 范围)视为已存在
     *
     * @param excludeId 编辑时排除自身
     */
    private boolean existsSame(String dictType, String dictLabel, String dictValue, Long excludeId) {
        //tenantQuery 自带 is_del=0 + 租户过滤
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DictData> qw = AFWrappers.lambdaTenantQuery();
        qw.eq(DictData::getDictType, dictType)
                .eq(DictData::getLabel, dictLabel)
                .eq(DictData::getValue, dictValue)
                .ne(excludeId != null, DictData::getId, excludeId);
        return dicDataService.getBaseMapper().selectCount(qw) > 0;
    }
}
