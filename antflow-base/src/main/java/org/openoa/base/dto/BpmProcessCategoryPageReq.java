package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程分类管理列表查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessCategoryPageReq implements Serializable {

    private PageDto pageDto;
    /**
     * 分类名称(模糊)
     */
    private String processTypeName;
}
