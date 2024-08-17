package org.openoa.engine.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

/**
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=NON_NULL)
public class BpmProcessCategoryVo implements Serializable {

   private Long id;
    /**
    * process type name
    */
    private String processTypeName;
    private Integer isDel;
    /**
    * sort
    */
    private Integer sort;
    /**
     * is for app 0:no 1:yes
    */
    private Integer isApp;
    private Integer state;
    /**
     * entrance
     */
    private String entrance;
    private Integer type;
    private String name;
}
