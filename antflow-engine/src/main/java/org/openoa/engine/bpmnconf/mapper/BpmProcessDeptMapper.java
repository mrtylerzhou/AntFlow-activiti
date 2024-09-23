package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.BpmProcessDept;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BpmProcessDeptMapper extends BaseMapper<BpmProcessDept> {
    /**
     * list a page of process deployment configuration
     *
     * @param page
     * @param vo
     * @return
     */
    public List<BpmProcessDeptVo> listConfigure(Page page, BpmProcessDeptVo vo);

    /**
     * get all process deployment configuration
     *
     * @param vo
     * @return
     */
    public List<BpmProcessDeptVo> allConfigure(BpmProcessDeptVo vo);

    /**
     * get max process code
     *
     * @return
     */
    public String maxProcessCode();

    /***
     * @param processKey
     * @return
     */
    public String selectMaxProcessId(@Param("processKey") String processKey);
}