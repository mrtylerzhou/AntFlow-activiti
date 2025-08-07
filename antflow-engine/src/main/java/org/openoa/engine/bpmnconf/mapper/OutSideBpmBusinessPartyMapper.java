package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.OutSideBpmBusinessParty;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;

import java.util.List;

/**
 * third party process service,business party mapper
 * @since 0.5
 */
@Mapper
public interface OutSideBpmBusinessPartyMapper extends BaseMapper<OutSideBpmBusinessParty> {

    /**
     * list business parties by page
     *
     * @param page
     * @param vo
     * @return
     */
    List<OutSideBpmBusinessPartyVo> selectPageList(Page page, OutSideBpmBusinessPartyVo vo);

    /**
     * query business party mark by id
     *
     * @param id
     * @return
     */
    String getBusinessPartyMarkById(@Param("id") Long id);

    /**
     * check whether the data exists
     *
     * @param vo
     * @return
     */
    Integer checkData(OutSideBpmBusinessPartyVo vo);


}
