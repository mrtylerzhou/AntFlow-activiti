package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.SysVersion;
import org.openoa.engine.vo.SysVersionVo;

import java.util.List;

/**
 * sysversion mapper
 */
@Mapper
public interface SysVersionMapper extends BaseMapper<SysVersion> {

    /**
     * get max index
     * @return
     */
    Integer maxIndex();

    List<SysVersionVo> selectPageList(@Param("param") SysVersionVo vo, @Param("page") PageDto page);

    Integer selectPageListCount(@Param("param") SysVersionVo vo);
}
