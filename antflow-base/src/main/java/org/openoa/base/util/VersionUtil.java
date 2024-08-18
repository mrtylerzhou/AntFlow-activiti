package org.openoa.base.util;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.JiMuBizException;

public class VersionUtil {

    /**

     * @param nowAppVersion  App now version
     * @param lastAppVersion the mim support version
     * @throws JiMuBizException
     */
    public static void checkAppVersion(String nowAppVersion, String lastAppVersion) throws JiMuBizException {
        if (StringUtils.isNotEmpty(nowAppVersion) && StringUtils.isNotEmpty(lastAppVersion)) {
            String msg = "current version is not supported,please download the latest version!";
            Long longNowVersion = 0L;
            Long longLastVersion = 0L;
            try {
                longNowVersion = Long.valueOf(nowAppVersion.replaceAll("\\.", ""));
                longLastVersion = Long.valueOf(lastAppVersion.replaceAll("\\.", ""));
            } catch (Exception e) {
            }
            if (longNowVersion < longLastVersion) {
                throw new JiMuBizException(msg);
            }
        }
    }
}
