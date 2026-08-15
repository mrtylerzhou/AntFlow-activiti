package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程权限管理批量保存结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPermissionsSaveResult implements Serializable {

    /**
     * 新增条数
     */
    private Integer insertCount;
    /**
     * 跳过条数(已存在)
     */
    private Integer skipCount;
}
