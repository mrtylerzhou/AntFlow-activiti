package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.OutSideBpmApproveTemplate;
import org.openoa.engine.vo.OutSideBpmApproveTemplateVo;

import java.util.List;

@Mapper
public interface OutSideBpmApproveTemplateMapper extends BaseMapper<OutSideBpmApproveTemplate> {

    List<OutSideBpmApproveTemplateVo> selectPageList(Page page, OutSideBpmApproveTemplateVo vo);

    String selectRoleApiUrlByConfId(Long confId);
}
