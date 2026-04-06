package org.openoa.base.util;


import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

import java.beans.Introspector;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @Classname StrUtils
 * @Description string utils
 * @Date 2021-10-31 15:14
 * @Created by AntOffice
 */
public class StrUtils {
    public static final Integer BPMN_CODE_LEN = 5;
    //.*-([0-9]{5})
    public static final String BPMNCONF_PATTERN = ".*-([0-9]{" + BPMN_CODE_LEN + "})";
    @TableField(exist = false)
    public static final String BPMNCONF_FORMATMARK = "%0" + BPMN_CODE_LEN + "d";
    public static String getFirstLetters(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return null;
        }
        return PinyinUtils.getFirstLettersUpperCase(s);
    }

    public static String getFirstLettersSmall(String s) {
        return PinyinUtils.getFirstLettersLowerCase(s);
    }

    /**
     * @param bpmncodePart bpmnCode'_'前面内容
     * @param bpmnCode
     */
    public static String joinBpmnCode(String bpmncodePart, String bpmnCode) {
        int defaultNum = 1;
        String maxNumStr = StringUtils.EMPTY;
        if (Pattern.matches(BPMNCONF_PATTERN, bpmnCode)) {
            String[] strings = bpmnCode.split(StringConstants.BPMN_CODE_SPLITMARK);
            maxNumStr = strings[strings.length - 1];
        }
        if (!Strings.isNullOrEmpty(maxNumStr)) {
            defaultNum = Integer.parseInt(maxNumStr) + 1;
        }
        String stringWithRawFormat=bpmncodePart+StringConstants.BPMN_CODE_SPLITMARK+BPMNCONF_FORMATMARK;
        String reJoinedBpmnCode =String.format(stringWithRawFormat, defaultNum);
        return reJoinedBpmnCode;
    }
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set emptyNames = new HashSet();
        for(java.beans.PropertyDescriptor pd : pds) {
            //check if value of this property is null then add it to the collection
            Object srcValue = src.getPropertyValue(pd.getName());
            if (ObjectUtils.isEmpty(srcValue)) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return (String[]) emptyNames.toArray(result);
    }

    public static String getBeanNameStandard(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        return Introspector.decapitalize(cls.getSimpleName());
    }
    @Deprecated
    public static String getBeanName(Class<?>cls){
        String simpleName = cls.getSimpleName();
        String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        return beanName;
    }

    public static String nullOrBlankToWhiteSpace(String value){
        if(value==null|| value.isEmpty()){
            return " ";
        }
        return value;
    }

}
