package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典管理列表行
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataVo implements Serializable {

    private Long id;
    /**
     * 字典标签
     */
    private String dictLabel;
    /**
     * 字典键值
     */
    private String dictValue;
    /**
     * 字典类型(lowcodeflow/udr/processlabel)
     */
    private String dictType;
    /**
     * 字典类型汉字含义(后端映射, 未知类型原样展示)
     */
    private String dictTypeLabel;
    /**
     * 字典排序
     */
    private Integer sort;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
