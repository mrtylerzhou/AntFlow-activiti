package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
/**
 * 快捷入口类型关联表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class QuickEntryTypeVo implements Serializable {

            private Long id;
    /**
    * quick entry id
    */
    private Long quickEntryId;
    /**
     * type 1 for pc and 2 for app
    */
    private Integer type;
    private Integer isDel;
    private Date createTime;


}
