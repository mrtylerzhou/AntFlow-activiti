package org.openoa.engine.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_NULL)
public class BaseApplicationVo implements Serializable {

    private String id;
    private String name;

    /**
     * 查看url
     */
    @TableField("look_url")
    private String lookUrl;
    /**
     * 提交url
     */
    @TableField("submit_url")
    private String submitUrl;
    /**
     * 条件url
     */
    @TableField("condition_url")
    private String conditionUrl;

    private Integer pkId;

}
