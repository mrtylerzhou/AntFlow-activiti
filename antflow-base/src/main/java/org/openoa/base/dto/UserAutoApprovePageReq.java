package org.openoa.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自动审批设置列表查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAutoApprovePageReq implements Serializable {

    private PageDto pageDto;
    /**
     * 归属人姓名(模糊)
     */
    private String ownerUserName;
    /**
     * formCode(模糊)
     */
    private String formCode;
}
