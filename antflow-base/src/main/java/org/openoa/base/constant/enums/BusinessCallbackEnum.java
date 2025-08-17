package org.openoa.base.constant.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.service.BusinessCallBackFace;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author tylerzhou
 */
@Slf4j
public enum BusinessCallbackEnum implements AfEnumBase{
    PROCESS_EVENT_CALLBACK(1, ProcessBusinessCallBackTypeEnum.class, "流程类回调枚举"),
    ;
    @Getter
    private final Integer code;
    @Getter
    private final Class<? extends BusinessCallBackFace> clsz;
    @Getter
    private final String desc;

    BusinessCallbackEnum(Integer code, Class<? extends BusinessCallBackFace> clsz, String desc) {
        this.code = code;
        this.clsz = clsz;
        this.desc = desc;
    }
    public static Map<BusinessCallbackEnum, List<BusinessCallBackAdaptor>>getAllAdaptorsByType(){

        Map<BusinessCallbackEnum,List<BusinessCallBackAdaptor>> allAdaptorsOfType=new HashMap<>();
        for (BusinessCallbackEnum businessCallbackEnum : BusinessCallbackEnum.values()) {
            Class<? extends BusinessCallBackFace> clsz = businessCallbackEnum.getClsz();

            BusinessCallBackFace businessCallBackFace = null;
            try {
              businessCallBackFace=  clsz.getEnumConstants()[0];
                Set<Class<BusinessCallBackAdaptor>> allAdaptors = businessCallBackFace.getAllAdaptors();
                List<BusinessCallBackAdaptor>businessCallBackAdaptors=new ArrayList<>();
                for (Class<BusinessCallBackAdaptor> adaptor : allAdaptors) {
                    BusinessCallBackAdaptor businessCallBackAdaptor = adaptor.newInstance();
                    businessCallBackAdaptors.add(businessCallBackAdaptor);
                }
                allAdaptorsOfType.put(businessCallbackEnum,businessCallBackAdaptors);
            }catch (Exception ex){
                log.error("error occur while creating instance by reflection");
            }
        }
        return allAdaptorsOfType;
    }
}
