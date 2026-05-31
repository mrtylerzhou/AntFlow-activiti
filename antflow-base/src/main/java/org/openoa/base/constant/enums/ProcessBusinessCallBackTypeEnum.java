package org.openoa.base.constant.enums;


import lombok.Getter;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.service.BusinessCallBackFace;
import org.openoa.base.service.ProcessEventIndexAdaptor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Author tylerzhou
 */
public enum ProcessBusinessCallBackTypeEnum implements BusinessCallBackFace,AfEnumBase {
    Send_MQ_Message(1, ProcessEventIndexAdaptor.class,"流程事件数据索引到ES")
    ,;
    @Getter
    private  Integer code;

    private  Class<? extends BusinessCallBackAdaptor> clsz;
    @Getter
    private  String desc;

    ProcessBusinessCallBackTypeEnum(Integer code, Class<? extends BusinessCallBackAdaptor>clsz, String desc){
        this.code = code;
        this.clsz = clsz;
        this.desc = desc;
    }

    @Override
    public Class<? extends BusinessCallBackAdaptor> getClsz() {
        return this.clsz;
    }

    public static ProcessBusinessCallBackTypeEnum getEnumByCode(Integer code){
        for (ProcessBusinessCallBackTypeEnum callBackTypeEnum : ProcessBusinessCallBackTypeEnum.values()) {
            if(Objects.equals(callBackTypeEnum.getCode(), code)){
                return callBackTypeEnum;
            }
        }
        return null;
    }
    @Override
    public BusinessCallBackAdaptor getAdaptorByCode(Integer code) {
        return null;
    }
}
