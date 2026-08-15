package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 业务数据列表分页请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataListPageReq implements Serializable {

    private PageDto pageDto;

    /**
     * 低代码流程 formCode
     */
    private String formCode;

    /**
     * 流程编号关键字(模糊)
     */
    private String processNumber;
}
