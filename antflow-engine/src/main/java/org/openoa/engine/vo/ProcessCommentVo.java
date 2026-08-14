package org.openoa.engine.vo;

import lombok.Data;

import java.util.List;

/**
 * 流程沟通 发送消息入参 VO.
 */
@Data
public class ProcessCommentVo {

    /** 流程实例编号(会话锚点) */
    private String processNumber;

    /** 回复哪条消息(根消息为 null) */
    private Long parentId;

    /** 消息正文 */
    private String content;

    /** 图片/附件 url JSON 数组(仅预留, v1 不处理) */
    private String attachment;

    /** @提及列表 */
    private List<Mention> mentions;

    @Data
    public static class Mention {
        private String userId;
        private String userName;
    }
}
