package org.openoa.engine.bpmnconf.service.biz.callback;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.BusinessCallbackEnum;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.service.BusinessCallBackFace;
import org.openoa.base.util.Retryer;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author tylerzhou
 */
@Slf4j
public class BusinessCallBackFactory {
    private static Map<BusinessCallbackEnum, List<BusinessCallBackAdaptor>> allAdaptorsOfType = null;

    static {
        allAdaptorsOfType = BusinessCallbackEnum.getAllAdaptorsByType();
    }

    private BusinessCallBackFactory() {

    }

    private static final BusinessCallBackFactory instance = new BusinessCallBackFactory();

    public static BusinessCallBackFactory build() {
        return instance;
    }

    /**
     * 对单个adaptor的回调
     *
     * @param params
     * @param callbackEnum
     * @param code  {@link BusinessCallBackFace#getCode()}
     */
    public <T> void doCallBack(T params, BusinessCallbackEnum callbackEnum, Integer code) {
        List<BusinessCallBackAdaptor> callBackAdaptorsOfType = getCallBackAdaptorsOfType(callbackEnum);
        if (CollectionUtils.isEmpty(callBackAdaptorsOfType)) {
            return;
        }
        Class<? extends BusinessCallBackFace> clsz = callbackEnum.getClsz();

        BusinessCallBackFace[] enumFaces = clsz.getEnumConstants();
        for (BusinessCallBackFace callBackFace : enumFaces) {
            if(Objects.equals(callBackFace.getCode(),code)){
                BusinessCallBackAdaptor businessCallBackAdaptor = callBackFace.getClszAdaptorInstance();
                Retryer retryer=new Retryer.Default();
                while (true){
                    try {
                        businessCallBackAdaptor.doCallBack(params);
                        break;
                    }catch (RuntimeException e){
                        retryer.continueOrPropagate(e);
                    }
                }

            }
        }

    }

    /**
     * 对某一业务类型下的所有adaptor回调
     *
     * @param params
     * @param callbackEnum
     */
    public <T> void doCallBacks(T params, BusinessCallbackEnum callbackEnum) {
        List<BusinessCallBackAdaptor> callBackAdaptors = this.getCallBackAdaptorsOfType(callbackEnum);
        for (BusinessCallBackAdaptor callBackAdaptor : callBackAdaptors) {
            try {
                callBackAdaptor.doCallBack(params);
            } catch (Exception e) {
                log.error("error while executing callback method,className:{}", callBackAdaptor.getClass().getName(), e);
            }
        }
    }

    private List<BusinessCallBackAdaptor> getCallBackAdaptorsOfType(BusinessCallbackEnum businessCallbackEnum) {
        List<BusinessCallBackAdaptor> businessCallBackAdaptors = allAdaptorsOfType.get(businessCallbackEnum);
        return businessCallBackAdaptors;
    }
}
