package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字典管理列表查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataPageReq implements Serializable {

    private PageDto pageDto;
    /**
     * 字典类型(精确筛选, lowcodeflow/udr/processlabel)
     */
    private String dictType;
    /**
     * 关键字(字典标签/字典键值 模糊匹配)
     */
    private String keyword;
}
