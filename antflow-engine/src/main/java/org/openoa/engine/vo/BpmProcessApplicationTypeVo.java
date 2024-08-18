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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=NON_NULL)
public class BpmProcessApplicationTypeVo implements Serializable {

   private Long id;
    /**
    * application id
    */
    private Long applicationId;
    /**
    * category id
    */
    private Long categoryId;
    private Integer isDel;
    /**
     * process type
     */
    private List<Long> processTypes;
    private Integer sort;
    private Integer type;
    /**
     * is frequently used 0 for no and 1 for yes
     */
    private Integer state;
    /**
     * 常用功能id
     * frequently used id
     */
    private Long  commonUseId;
    /***
     * history id
     */
    private Long historyId;
    /**
     * visible state 0 for hidden and 1 for visible
     */
    private Integer visbleState;
    private Date createTime;
    /**
     * frequently use state
     */
    private Integer commonUseState;
}
