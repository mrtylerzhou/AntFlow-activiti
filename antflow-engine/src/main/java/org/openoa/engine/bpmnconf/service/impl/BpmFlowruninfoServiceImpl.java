package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmFlowruninfo;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.mapper.BpmFlowruninfoMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmFlowruninfoService;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class BpmFlowruninfoServiceImpl extends ServiceImpl<BpmFlowruninfoMapper, BpmFlowruninfo> implements BpmFlowruninfoService {



    /**
     * create run info
     */
    @Override
    public void createFlowRunInfo(String entryId, String processInstance) throws Exception {
        BpmFlowruninfo flowruninfo = new BpmFlowruninfo();
        flowruninfo.setEntitykey(entryId);
        flowruninfo.setCreateUserId( SecurityUtils.getLogInEmpIdSafe());
        flowruninfo.setRuninfoid(Long.parseLong(processInstance));
        flowruninfo.setEntityclass(SecurityUtils.getLogInEmpNameSafe());
        flowruninfo.setCreateactor(SecurityUtils.getLogInEmpNameSafe());
        flowruninfo.setCreatedate(new Date());
        getBaseMapper().insert(flowruninfo);
    }

    /**
     * create run info
     *
     * @param flowruninfo
     */
    @Override
    public void createFlowRunInfo(BpmFlowruninfo flowruninfo) {
        getBaseMapper().insert(flowruninfo);
    }

    /**
     * get run info by id
     *
     * @param runInfoId
     * @return
     */
    @Override
    public BpmFlowruninfo getFlowruninfo(Long runInfoId) {
        return getBaseMapper().getFlowruninfo(runInfoId);
    }

    /**
     * delete  run info
     */
    @Override
    public void deleteFlowruninfo(Long id) {
        getBaseMapper().deleteById(id);
    }


}
