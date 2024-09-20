package org.openoa.base.vo;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendParam implements Serializable {
    /**
     * receiver id
     */
    private String userId;

    /**
     * title
     */
    private String title;

    /**
     * content
     */
    private String content;


    private String url;

    /**
     * node id(can be empty)
     */
    private String node;

    /**
     * params
     */
    private String params;

    /**
     * url prams
     */
    private UrlParams urlParams;

    /**
     * app url
     */
    private String appUrl;

}
