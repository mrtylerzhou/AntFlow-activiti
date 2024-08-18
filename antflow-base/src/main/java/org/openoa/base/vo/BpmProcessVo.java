package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author AntFlow
 * @since  0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessVo implements Serializable {

    private String processName;

    private String processKey;

}