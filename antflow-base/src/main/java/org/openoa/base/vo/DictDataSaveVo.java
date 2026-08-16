package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字典数据 新增/编辑 请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataSaveVo implements Serializable {

    /**
     * 主键(编辑时必传)
     */
    private Long id;
    /**
     * 字典标签(必填)
     */
    private String dictLabel;
    /**
     * 字典键值(必填)
     */
    private String dictValue;
    /**
     * 字典类型(必填, 仅 udr/processlabel 可选, lowcodeflow 禁止)
     */
    private String dictType;
    /**
     * 字典排序(选填, 默认 0)
     */
    private Integer sort;
    /**
     * 备注(选填)
     */
    private String remark;
}
