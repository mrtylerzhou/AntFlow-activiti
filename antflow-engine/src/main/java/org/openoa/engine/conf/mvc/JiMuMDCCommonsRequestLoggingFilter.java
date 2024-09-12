package org.openoa.engine.conf.mvc;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.util.MDCLogUtil;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-18 11:24
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class JiMuMDCCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Autowired
    private UserServiceImpl userService;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        MDCLogUtil.resetLogId();
        if (!request.getMethod().equals("OPTIONS")) {
            String userId = request.getHeader("userId");
            if (StringUtils.isEmpty(userId)) {
                userId = request.getHeader("Userid");
            }
            if (!StringUtils.isEmpty(userId)) {
                BaseIdTranStruVo userById = userService.getById(userId);
                String userName = StringUtils.EMPTY;
                if (userById != null) {
                    userName = userById.getName();
                    BaseIdTranStruVo userInfo = BaseIdTranStruVo.builder().id(userId).name(userName).build();
                    ThreadLocalContainer.set("currentuser", userInfo);
                }
                if (logger.isDebugEnabled()) {
                    logger.info("开始输出详细日志");
                    super.beforeRequest(request, message);
                }
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
