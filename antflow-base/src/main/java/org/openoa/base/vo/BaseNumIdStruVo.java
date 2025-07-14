package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseNumIdStruVo {
    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;
    private Boolean active;
}
