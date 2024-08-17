package org.openoa.base.util;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.exception.JiMuBizException;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-11 15:14
 * @Param
 * @return
 * @Version 1.0
 */

public class SecurityUtils {
    public static BaseIdTranStruVo getLogInEmpInfo(){
        return (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
    }
    public static Long getLogInEmpId(){
        BaseIdTranStruVo currentuser = (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
        if(currentuser==null){
            throw new JiMuBizException("当前用户未登陆!");
        }
        return currentuser.getId();
    }
    public static String getLogInEmpIdStr(){
        BaseIdTranStruVo currentuser = (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
        if(currentuser==null){
            throw new JiMuBizException("当前用户未登陆!");
        }
        return currentuser.getId().toString();
    }

    public static String getLogInEmpName(){
        BaseIdTranStruVo currentuser = (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
        if(currentuser==null){
            throw new JiMuBizException("当前用户未登陆!");
        }
        return currentuser.getName();
    }
    public static String getLogInEmpNameSafe(){
        BaseIdTranStruVo currentuser = (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
        if(currentuser==null){
            return StringUtils.EMPTY;
        }
        return currentuser.getName();
    }
    public static Long getLogInEmpIdSafe(){
        BaseIdTranStruVo currentuser = (BaseIdTranStruVo) ThreadLocalContainer.get("currentuser");
        if(currentuser==null){
            return -999L;
        }
        return currentuser.getId();
    }
}
