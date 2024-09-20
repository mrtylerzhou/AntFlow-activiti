package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMsgVo {

    /**
     * receiver id
     */
    private String userId;

    /**
     * receiver email
     */
    private String email;

    /**
     * carbon copy array
     */
    private String[] cc;

    /**
     * receiver mobile phone no
     */
    private String mobile;

    /**
     * message title
     */
    private String title;

    /**
     * message cotent
     */
    private String content;

    /**
     * email url
     */
    private String emailUrl;

    /**
     * system message box url
     */
    private String url;

    /**
     * App push url
     */
    private String appPushUrl;

    /**
     * task id
     */
    private String taskId;

    /**
     * sso domain
     */
    private String ssoSessionDomain;

    /**
     * message source system
     */
    private Integer source;
}
