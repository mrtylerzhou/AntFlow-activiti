package org.openoa.base.constant.enums;


import org.openoa.base.exception.AFBizException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum BpmnConfFlagsEnum implements BinaryAfEnumBase<BpmnConfFlagsEnum>{
    NOTHING(0,"不存在"),
    HAS_NODE_LABELS(0b1,"包含节点标签(任意节点包含标签)"),
    HAS_STARTUSER_CHOOSE_MODULES(0b10,"是否包含发起人自选模块"),
    HAS_DYNAMIC_CONDITIONS(0b100,"是否包含动态条件"),
    HAS_COPY(0b1000,"是否包含抄送"),
    HAS_LAST_NODE_COPY(0b10000,"最后一个节点是否包含抄送"),
    HAS_FORM_RELATED_ASSIGNEES(0b100000,"包含表单中选取人员")
    ;
    private final Integer code;
    private final String desc;

    BpmnConfFlagsEnum(Integer code, String desc){

        this.code = code;
        this.desc = desc;
    }

    /**
     * flag叠加
     * @param alreadyFlags
     * @param newFlag
     * @return
     */
    public static Integer binaryOr(Integer alreadyFlags,Integer newFlag){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }
        return alreadyFlags|newFlag;
    }

    public String getDesc() {
        return desc;
    }
    public Integer getCode(){
        return code;
    }
}
