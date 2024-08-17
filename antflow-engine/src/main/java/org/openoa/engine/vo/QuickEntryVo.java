package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
/**
 * 快捷入口表
 *  query entry entity vo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class QuickEntryVo  implements Serializable {

    private Integer id;
    /**
    * application title
    */
    private String title;

    /**
     * name
     */
    private String name;
    /**
     *  effective image url
    */
    private String effectiveSource;
    /**
    * is shown(not deleted)
    */
    private Integer isDel;

    /**
     * status name
     */
    private String  stateName;
    /**
     * request route
     */
    private String route;
    /**
     * sort order
     */
    private Integer sort;
    private Date createTime;
    /**
     * types
     */
    private List<Integer> types;
    /**
     * type ids
     */
    public String typeIds;
    /**
     * type name
     */
    private String typeName;

    /**
     * 0 for not in use and 1 for in use
     */
    private Integer status;

    /**
     * variable url flag
     */
    private Integer variableUrlFlag;

}
