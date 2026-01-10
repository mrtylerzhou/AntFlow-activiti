package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class ExtraSignInfoVo {

    /**
     * 1 add sign,2 exclude sign
     */
    private Integer propertyType;
    /**
     * @see org.openoa.base.constant.enums.NodePropertyEnum
     */
    private Integer nodeProperty;

    private List<BaseIdTranStruVo> signInfos;
    /**
     * signInfos 用于前值给前端用于反显设计时数据
     * 如果用户添加/排除的是角色(后续还可能会有其它),这里存储的是角色对应的人员的信息
     */
    private List<BaseIdTranStruVo> otherSignInfos;
}
