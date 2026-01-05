package org.openoa.base.constant.enums;

import lombok.Getter;
import org.openoa.base.vo.BaseIdTranStruVo;

@Getter
public enum AFSpecialAssigneeEnum implements AfEnumBase{
    TO_BE_REMOVED(0, "0", "最终会被去除的人员"),
    CC_NODE(-1,"-1","抄送人"),
    SKIP(-2,"-2","自动节点自动跳过")
    ;
    private final Integer code;
    private final String id;
    private final String desc;

    AFSpecialAssigneeEnum(int code, String id, String desc){

        this.code = code;
        this.id = id;
        this.desc = desc;
    }
    public static BaseIdTranStruVo buildToBeRemoved(){
        return new BaseIdTranStruVo(TO_BE_REMOVED.getId(), TO_BE_REMOVED.getDesc());
    }
}
