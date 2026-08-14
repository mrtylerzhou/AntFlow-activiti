package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmProcessComment;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessCommentBizService;
import org.openoa.engine.utils.UserMsgUtils;
import org.openoa.engine.vo.ProcessCommentVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 流程沟通实现.
 * <p>二级回复模型: 根消息 parentId/rootId 均 null; 回复消息 parentId 指向被回复消息,
 * rootId 归到根消息(父是根→父自身; 父是回复→父的 root), 避免递归回溯.</p>
 */
@Slf4j
@Service
public class ProcessCommentBizServiceImpl implements ProcessCommentBizService {

    @Override
    public List<BpmProcessComment> listComments(String processNumber) {
        if (!StringUtils.hasText(processNumber)) {
            return java.util.Collections.emptyList();
        }
        return this.getMapper().selectList(
                new QueryWrapper<BpmProcessComment>()
                        .eq("process_number", processNumber)
                        .eq("is_deleted", 0)
                        .orderByAsc("create_time", "id")
        );
    }

    @Override
    public BpmProcessComment addComment(ProcessCommentVo vo) {
        if (vo == null || !StringUtils.hasText(vo.getProcessNumber())) {
            throw new IllegalArgumentException("processNumber 不能为空");
        }
        BpmProcessComment comment = new BpmProcessComment();
        comment.setProcessNumber(vo.getProcessNumber());
        comment.setContent(vo.getContent());
        comment.setAttachment(vo.getAttachment());
        comment.setCreateUser(SecurityUtils.getLogInEmpIdStr());
        comment.setCreateUserName(SecurityUtils.getLogInEmpNameSafe());
        comment.setIsDeleted(0);

        // 回复消息: 确定 rootId + replyToUser
        Long parentId = vo.getParentId();
        if (parentId != null) {
            BpmProcessComment parent = this.getService().getById(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("被回复的消息不存在或已删除");
            }
            comment.setParentId(parentId);
            // 父是根 → 父自身; 父是回复 → 父的 root
            comment.setRootId(parent.getParentId() == null ? parent.getId() : parent.getRootId());
            comment.setReplyToUser(parent.getCreateUser());
            comment.setReplyToUserName(parent.getCreateUserName());
        }

        // @提及序列化
        if (!CollectionUtils.isEmpty(vo.getMentions())) {
            comment.setMentions(JSON.toJSONString(vo.getMentions()));
        }

        this.getService().save(comment);

        // @提及发站内信
        if (!CollectionUtils.isEmpty(vo.getMentions())) {
            notifyMentions(comment, vo.getMentions());
        }
        return comment;
    }

    @Override
    public void withdrawComment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        BpmProcessComment comment = this.getService().getById(id);
        if (comment == null) {
            throw new IllegalArgumentException("消息不存在");
        }
        String loginId = SecurityUtils.getLogInEmpIdStr();
        if (!Objects.equals(comment.getCreateUser(), loginId)) {
            throw new IllegalArgumentException("只能撤回自己发送的消息");
        }
        comment.setIsDeleted(1);
        this.getService().updateById(comment);
    }

    /**
     * 给被 @ 的参与者发站内信.
     */
    private void notifyMentions(BpmProcessComment comment, List<ProcessCommentVo.Mention> mentions) {
        String fromName = StringUtils.hasText(comment.getCreateUserName())
                ? comment.getCreateUserName() : comment.getCreateUser();
        String snippet = comment.getContent();
        if (snippet != null && snippet.length() > 50) {
            snippet = snippet.substring(0, 50) + "...";
        }
        for (ProcessCommentVo.Mention m : mentions) {
            if (m == null || !StringUtils.hasText(m.getUserId())) {
                continue;
            }
            try {
                UserMsgVo msg = UserMsgVo.builder()
                        .userId(m.getUserId())
                        .title("流程沟通提醒")
                        .content(fromName + " 在流程 " + comment.getProcessNumber() + " 的沟通中@了你：" + snippet)
                        .build();
                UserMsgUtils.insertUserMessage(msg);
            } catch (Exception e) {
                log.warn("notify mention failed, userId:{}", m.getUserId(), e);
            }
        }
    }
}
