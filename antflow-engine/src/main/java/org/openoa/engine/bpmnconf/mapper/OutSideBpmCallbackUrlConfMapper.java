package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;

import java.util.List;

@Mapper
public interface OutSideBpmCallbackUrlConfMapper extends BaseMapper<OutSideBpmCallbackUrlConf> {

    List<OutSideBpmCallbackUrlConfVo> selectPageList(Page page, OutSideBpmCallbackUrlConfVo vo);

}
