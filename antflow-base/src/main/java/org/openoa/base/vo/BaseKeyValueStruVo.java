package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author TylerZhou
 * @since 0.0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseKeyValueStruVo {
    private String key;
    private String value;
    private String type;
}
