package org.openoa.base.service;

import org.apache.commons.lang3.EnumUtils;
import org.openoa.base.interf.BusinessCallBackAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Author tylerzhou
 */
public interface BusinessCallBackFace {
   default Set<Class<BusinessCallBackAdaptor>> getAllAdaptors(){
       Enum anEnum = (Enum) this;
       List enumList = EnumUtils.getEnumList(anEnum.getClass());
       Set<Class<BusinessCallBackAdaptor>>resultSet=new HashSet<>();
       for (Object o : enumList) {
           BusinessCallBackFace callBackFace = (BusinessCallBackFace)o;
           Class<BusinessCallBackAdaptor> clsz = (Class<BusinessCallBackAdaptor>)callBackFace.getClsz();
           resultSet.add(clsz);
       }
       return resultSet;
   }
    Integer getCode();
    Class<? extends BusinessCallBackAdaptor> getClsz();
    default BusinessCallBackAdaptor getAdaptorByCode(Integer code){
        Enum anEnum = (Enum) this;
        List enumList = EnumUtils.getEnumList(anEnum.getClass());
        for (Object en :enumList){
            BusinessCallBackFace callBackFace = (BusinessCallBackFace) en;
            Integer adaptorCode = callBackFace.getCode();
            if(Objects.equals(adaptorCode,code)){
                Class<? extends BusinessCallBackAdaptor> clsz = callBackFace.getClsz();
                BusinessCallBackAdaptor callBackAdaptor=null;
                try {
                    callBackAdaptor=clsz.newInstance();
                    return callBackAdaptor;
                }catch (Exception e){

                }
            }
        }
        return null;
    }
    default BusinessCallBackAdaptor getClszAdaptorInstance(){
       BusinessCallBackAdaptor adaptor=null;
       try {
           try {

               Component annotation = AnnotationUtils.findAnnotation(this.getClsz(),Component.class);
               if(annotation!=null){
                   adaptor= SpringBeanUtils.getBean(this.getClsz());
               }
           }catch (Exception e){


           }
           if(adaptor==null){
               adaptor= this.getClsz().newInstance();
           }
       }catch (Exception e){

       }
       return adaptor;
    }
}
