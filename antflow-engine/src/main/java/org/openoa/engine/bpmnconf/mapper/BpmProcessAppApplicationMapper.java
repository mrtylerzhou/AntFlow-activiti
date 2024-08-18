package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.engine.bpmnconf.confentity.BpmProcessAppApplication;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * process application mapper
 * @since 0.5
 */
@Repository
public interface BpmProcessAppApplicationMapper extends BaseMapper<BpmProcessAppApplication> {
    /**
     * query application list
     *
     * @return
     */
    public List<BpmProcessAppApplicationVo> listPage(Page page, BpmProcessAppApplicationVo vo);
    /**
     * query application list
     *
     * @return
     */
    public List<BpmProcessAppApplicationVo> newListPage(Page page, BpmProcessAppApplicationVo vo);
    /**
     * query by category
     *
     * @return
     */
    public List<BpmProcessAppApplicationVo> listIcon(BpmProcessAppApplicationVo vo);
    /**
     * get all application that one has no create permission
     *
     * @return
     */
    public List<BpmProcessAppApplication> findProcessAppApplication();

    /**
     * search applications
     *
     * @return
     */
    public List<BpmProcessAppApplicationVo> searchIcon(BpmProcessAppApplicationVo vo);

    /**
     * list application by category
     *
     * @return
     */
    List<BpmProcessAppApplicationVo> listProcessIcon(BpmProcessAppApplicationVo vo);
}
