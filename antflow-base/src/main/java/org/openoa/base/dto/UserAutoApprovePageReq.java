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
     * 归属人姓名(模糊, 未传 ownerUserId 时生效)
     */
    private String ownerUserName;
    /**
     * 归属人 id(精确过滤, 优先于 ownerUserName)
     */
    private String ownerUserId;
    /**
     * formCode(模糊)
     */
    private String formCode;
}
