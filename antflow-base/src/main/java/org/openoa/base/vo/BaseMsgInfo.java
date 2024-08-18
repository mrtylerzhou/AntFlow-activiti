package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.UserMessageStatus;

/**
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseMsgInfo {

    private String msgTitle;

    private String url;

    private String username;

    private String content;

    private UserMessageStatus userMessageStatus;
}
