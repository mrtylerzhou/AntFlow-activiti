package org.openoa.base.interf;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this interface is used to adapt to different business objects
 * @Author TylerZhou
 * @Date 2024/7/7 7:26
 * @Version 0.5
 */
public interface AdaptorService {
    //used to store supported business objects
    ConcurrentHashMap<String, List<Enum<?>>> SUPPORTED_BUSINESS =new ConcurrentHashMap<>();
    void setSupportBusinessObjects();
    default void  addSupportBusinessObjects(Enum<?>... businessObjects) {
        addSupportBusinessObjects("", businessObjects);
    }
    default void  addSupportBusinessObjects(String marker,Enum<?>... businessObjects) {
        String clsNameWithMaker = this.getClass().getName()+marker;
        if (businessObjects != null) {
            for (Enum<?> c : businessObjects) {
                if (SUPPORTED_BUSINESS.containsKey(clsNameWithMaker)) {
                    SUPPORTED_BUSINESS.get(this.getClass().getName()+marker).add(c);
                }else{//first time add
                    SUPPORTED_BUSINESS.put(clsNameWithMaker, Lists.newArrayList(c));
                }
            }
        }
    }
    /**
     * check whether given business object is supported
     */
    default boolean isSupportBusinessObject(Enum<?> businessObject) {
       return isSupportBusinessObject("",businessObject);
    }
    default boolean isSupportBusinessObject(String marker,Enum<?> businessObject) {
        String clsNameWithMaker = this.getClass().getName()+marker;
        if(SUPPORTED_BUSINESS.containsKey(clsNameWithMaker)){
            List<Enum<?>> enums = SUPPORTED_BUSINESS.get(clsNameWithMaker);
            for (Enum<?> anEnum : enums) {
                if(anEnum==businessObject){
                    return true;
                }
            }
        }
        setSupportBusinessObjects();
        if(SUPPORTED_BUSINESS.containsKey(clsNameWithMaker)){
            List<Enum<?>> enums = SUPPORTED_BUSINESS.get(clsNameWithMaker);
            for (Enum<?> anEnum : enums) {
                if(anEnum==businessObject){
                    return true;
                }
            }
        }
        return false;
    }
}
