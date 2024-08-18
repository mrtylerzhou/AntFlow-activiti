package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.base.vo.BpmnConfVo;

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
}
