package org.openoa.base.util;

import com.google.common.base.Strings;
import org.openoa.base.exception.AFBizException;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @since  0.5.0
 * @Param
 * @return
 * @Version 1.0
 */
public class AssertUtil {
    public static void throwsIfEmpty(Object object, String errorMsg, List params){

        if(Strings.isNullOrEmpty(errorMsg)){
            errorMsg="对象不存在";
        }


        StringBuilder sb=new StringBuilder(errorMsg);
        for (int i = 0; i < params.size(); i++) {
            sb.append(" %s;");
        }

        errorMsg=Strings.lenientFormat(sb.toString(),params);

        if(ObjectUtils.isEmpty(object)){
            throw new AFBizException(errorMsg);
        }

    }
}
