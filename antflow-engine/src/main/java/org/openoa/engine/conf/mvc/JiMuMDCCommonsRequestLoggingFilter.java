package org.openoa.engine.conf.mvc;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.MDCLogUtil;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-18 11:24
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class JiMuMDCCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Autowired
    private AfUserService userService;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        MDCLogUtil.resetLogId();
        if (!request.getMethod().equals("OPTIONS")) {
            String userId = request.getHeader("userId");
            String userName = request.getHeader("userName");
            if(!StringUtils.isEmpty(userName)){
                try {
                    userName = URLDecoder.decode(userName, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            if (StringUtils.isEmpty(userId)) {
                userId = request.getHeader("Userid");
            }
            if (!StringUtils.isEmpty(userId)) {
                if(!StringUtils.isEmpty(userName)){
                    BaseIdTranStruVo userInfo = BaseIdTranStruVo.builder().id(userId).name(userName).build();
                    ThreadLocalContainer.set("currentuser", userInfo);
                }else{
                    BaseIdTranStruVo userById = userService.getById(userId);
                    if (userById != null && StringUtils.isEmpty(userName)) {
                        userName = userById.getName();
                        BaseIdTranStruVo userInfo = BaseIdTranStruVo.builder().id(userId).name(userName).build();
                        ThreadLocalContainer.set("currentuser", userInfo);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.info("开始输出详细日志");
                        super.beforeRequest(request, message);
                    }
                }

            }
            if (!StringUtils.isEmpty(userName)) {
                ThreadLocalContainer.set("userName", userName);
            }

            if (!StringUtils.isEmpty(userId)) {
                ThreadLocalContainer.set("userId", userId);
            }
        }
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        ThreadLocalContainer.clean();
        if (logger.isDebugEnabled()) {
            super.afterRequest(request, message);
        }
    }

    @Override
    protected boolean isIncludeHeaders() {
        return logger.isDebugEnabled() || super.isIncludeHeaders();
    }

    @Override
    protected boolean isIncludeQueryString() {
        return logger.isDebugEnabled() || super.isIncludeQueryString();
    }

    @Override
    protected boolean isIncludePayload() {
        return logger.isDebugEnabled() || super.isIncludePayload();
    }

    @Override
    protected boolean isIncludeClientInfo() {
        return logger.isDebugEnabled() || super.isIncludeClientInfo();
    }
}
