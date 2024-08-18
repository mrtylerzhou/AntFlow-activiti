package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageInfo implements Serializable {
    /**
     * sender
     */
    private String sender;

    /**
     * receiver mobile
     */
    private String receiver;

    /**
     * content
     */
    private String content;

    /**
     * sign mark
     */
    private String sign;

}