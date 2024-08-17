package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutSideBpmCallbackUrlConfMapper extends BaseMapper<OutSideBpmCallbackUrlConf> {

    List<OutSideBpmCallbackUrlConfVo> selectPageList(Page page, OutSideBpmCallbackUrlConfVo vo);

}
