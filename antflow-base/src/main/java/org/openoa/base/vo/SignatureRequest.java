package org.openoa.base.vo;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignatureRequest implements Serializable {

    /**
     * os type，iOS，Android，1=PC
     */
    private String systemType;
    /**
     * app version
     */
    private String appVersion;
    /**
     * device hardward info
     */
    private String hardware;
    /**
     * app system version
     */
    private String systemVersion;

}
