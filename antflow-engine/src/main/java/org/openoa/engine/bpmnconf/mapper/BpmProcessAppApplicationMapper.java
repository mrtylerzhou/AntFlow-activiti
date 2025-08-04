package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;

import java.util.LinkedList;
import java.util.List;

/**
 * process application mapper
 * @since 0.5
 */
@Mapper
public interface BpmProcessAppApplicationMapper extends BaseMapper<BpmProcessAppApplication> {
    /**
     * query application list
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> listPage(Page page, BpmProcessAppApplicationVo vo);
    /**
     * query application list
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> newListPage(Page page, BpmProcessAppApplicationVo vo);
    /**
     * query by category
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> listIcon(BpmProcessAppApplicationVo vo);
    /**
     * get all application that one has no create permission
     *
     * @return
     */
    List<BpmProcessAppApplication> findProcessAppApplication();

    /**
     * search applications
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> searchIcon(BpmProcessAppApplicationVo vo);

    /**
     * list application by category
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> listProcessIcon(BpmProcessAppApplicationVo vo);

    LinkedList<BpmProcessAppApplicationVo> selectAllByBusinessPart(String businessPartyMark);

    LinkedList<BpmProcessAppApplicationVo> selectAllByPartMarkId(Integer partyMarkId);
}
