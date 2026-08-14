package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessComment;
import org.openoa.engine.bpmnconf.mapper.BpmProcessCommentMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.ProcessCommentService;
import org.openoa.engine.vo.ProcessCommentVo;

import java.util.List;

/**
 * 流程沟通业务接口.
 * 按流程实例(processNumber)一条会话, 支持二级回复 + @提及.
 */
public interface ProcessCommentBizService extends BizService<BpmProcessCommentMapper, ProcessCommentService, BpmProcessComment> {

    /**
     * 按 processNumber 查询未删除的沟通消息, 按 createTime + id 升序.
     */
    List<BpmProcessComment> listComments(String processNumber);

    /**
     * 发送根消息或回复. 回复时自动挂到根(rootId)并记录回给谁(replyToUser).
     * 返回落库后的完整实体(含自增 id).
     */
    BpmProcessComment addComment(ProcessCommentVo vo);

    /**
     * 撤回自己发送的消息(软删除 is_deleted=1).
     */
    void withdrawComment(Long id);
}
