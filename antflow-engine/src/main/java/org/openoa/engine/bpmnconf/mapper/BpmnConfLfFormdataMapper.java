package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.vo.LfFormManageVo;

import java.util.List;


@Mapper
public interface BpmnConfLfFormdataMapper  extends BaseMapper<BpmnConfLfFormdata> {
    BpmnConfLfFormdata getByFormCode(@Param("formCode") String formCode);

    /**
     * 分页查询独立表单的当前生效版本（家族分组，每族一行）
     */
    List<LfFormManageVo> listEffectiveFormPage(Page<LfFormManageVo> page, @Param("vo") LfFormManageVo vo);

    /**
     * 查询某家族的所有版本（历史版本查看）
     */
    List<LfFormManageVo> listVersionsByFormCode(@Param("formCode") String formCode);

    /**
     * 查询所有生效独立表单（流程设计多选下拉框用，不分页）
     */
    List<LfFormManageVo> listAllEffectiveForms();

    /**
     * 按 id 查询（含已软删，供运行中流程实例读取）
     */
    BpmnConfLfFormdata getByIdIgnoreDeleted(@Param("id") Long id);

    /**
     * 按 id 列表批量查询（含已软删，供运行中流程实例读取）
     */
    List<BpmnConfLfFormdata> listByIdsIgnoreDeleted(@Param("ids") List<Long> ids);

    /**
     * 生成新的家族 formCode（取当前最大序号+1）
     */
    String getMaxFormCode(@Param("prefix") String prefix);
}
