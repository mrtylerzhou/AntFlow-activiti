package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;


/**
 * @author AntFlow
 * @since 0.5
 */
@Data
public class IdsVo implements Serializable {
    private Integer id;
    private String powerId;

}
