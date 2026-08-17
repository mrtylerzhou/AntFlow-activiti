package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.vo.NodeDiagnosisRequestVo;
import org.openoa.base.vo.NodeDiagnosisVo;
import org.openoa.base.vo.ProcessDiagnosisInitVo;

/**
 * 流程诊断 (流程管理-流程监控-更多-流程诊断)
 *
 * @see org.openoa.engine.bpmnconf.service.biz.ProcessDiagnosisBizServiceImpl
 */
public interface ProcessDiagnosisBizService {

    /**
     * 诊断初始化: processNumber → confId/bpmnCode/发起人/当前表单值
     */
    ProcessDiagnosisInitVo diagnosisInit(String processNumber);

    /**
     * 节点归因诊断, 短路矩阵见 .scratch/process-diagnosis-design.md §5
     */
    NodeDiagnosisVo diagnoseNode(NodeDiagnosisRequestVo request);
}
