package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmProcessVo;
import org.openoa.engine.vo.StartFlowListRowVo;

import java.util.List;

/**
 * @Classname BpmnConfMapper
 * @since 0.0.1
 * @Created by AntOffice
 */
@Mapper
public interface BpmnConfMapper extends BaseMapper<BpmnConf> {

    List<Integer> getIds();

    String getMaxBpmnCode(@Param("bpmnCodeParts") String bpmnCodeParts);
    List<BpmnConfVo> selectPageList(Page page,@Param("bpmnConfVo") BpmnConfVo vo);

    List<BpmnConfVo> selectThirdBpmnConfList(@Param("bpmnConfVo") BpmnConfVo vo);

    List<BpmnConfVo> selectOutSideFormCodeList(Page page,@Param("bpmnConfVo") BpmnConfVo vo);

    BpmnConf getConfByProcessNumber(@Param("processNumber") String processNumber);

    List<BpmProcessVo> allProcess();

    BpmProcessVo getBpmProcessVoByFormCode(@Param("formCode") String formCode);

    List<String> formCodeListByBpmnName(@Param("bpmnName") String bpmnName);

    List<String> formCodeListByConfId(@Param("confId") Long confId);

    /**
     * 统计有多少生效流程(effective_status=1)在 lf_formdata_ids 中引用了指定表单版本id
     * 用于独立表单删除保护
     */
    int countEffectiveConfReferencingFormdata(@Param("formdataId") Long formdataId);

    /**
     * 查询所有在 lf_formdata_ids 中引用了指定表单版本的流程配置（查看引用用）
     */
    List<BpmnConfVo> listConfsReferencingFormdata(@Param("formdataId") Long formdataId);

    /**
     * 发起流程聚合:全部有效流程(含 applicationId 关联,outside 用)
     */
    List<StartFlowListRowVo> selectStartFlowList();
}
