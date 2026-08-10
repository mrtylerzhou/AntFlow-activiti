package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAuditMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.ProcessAuditService;

import java.util.List;

public interface ProcessAuditBizService extends BizService<BpmProcessAuditMapper, ProcessAuditService, BpmProcessAudit> {

    /**
     * 保存表单字段变更审计. 在 consentData 写入新值之前调用.
     *
     * @param vo          业务数据 vo (含前端提交的新值)
     * @param entityClass vo 对应的实体类型. 低代码流程可传 BusinessDataVo; DIY 流程传子类.
     */
    void saveChanges(BusinessDataVo vo, Class<?> entityClass);

    /**
     * 按 processNumber 查询所有审计记录, 按 taskDefKey + createTime 升序.
     */
    List<BpmProcessAudit> getProcessAudits(String processNumber);
}
