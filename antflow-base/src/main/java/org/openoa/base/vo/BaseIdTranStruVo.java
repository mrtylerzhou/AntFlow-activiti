package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname BaseIdTranStruVo
 * @Description common id and name mapping
 * @since 0.0.1
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseIdTranStruVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * name
     */
    private String name;
}
