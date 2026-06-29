package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmProcessVo;

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
}
