package org.openoa.base.vo;

import lombok.Data;
/**
 * @author AntFlow
 * @since 0.5
 * this class is used by a demo process
 */

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-07 9:10
 * @Param
 * @return
 * @Version 1.0
 */
@Data
public class ThirdPartyAccountApplyVo extends BusinessDataVo {
    private Integer accountType;
    private String accountOwnerName;
    private String remark;
}
