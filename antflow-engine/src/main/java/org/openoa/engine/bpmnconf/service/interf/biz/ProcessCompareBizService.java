package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.vo.ProcessCompareCandidateVo;
import org.openoa.base.vo.ProcessCompareEntrustVo;

import java.util.List;

/**
 * 流程对比(实例级): 流程监控-更多-流程对比
 *
 * <p>设计: .scratch/process-instance-compare-design.md</p>
 * <p>仅提供两个薄查询, 节点对齐与审批人 diff 全部由前端完成。</p>
 */
public interface ProcessCompareBizService {

    /**
     * 候选实例搜索: 同 formCode(PROCESSINESS_KEY) 的实例, 全状态, 排除已删除。
     *
     * @param formCode 流程类型 formCode(必填)
     * @param keyword  可选, 模糊匹配流程编号(BUSINESS_NUMBER)或发起人姓名(user_name)
     */
    List<ProcessCompareCandidateVo> compareCandidates(String formCode, String keyword);

    /**
     * 某实例全部加签/减签/转办记录(bpm_flowrun_entrust, 按 node_id 归组由前端完成)。
     */
    List<ProcessCompareEntrustVo> compareEntrusts(String processNumber);
}
