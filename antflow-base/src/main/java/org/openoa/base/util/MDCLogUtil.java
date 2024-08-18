package org.openoa.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.List;

@Slf4j
public abstract class MDCLogUtil {

    public static String getLogIdBydefault() {
        return MDC.get("ruid");
    }

    public static String getLogId() {
        String logId =StringUtils.EMPTY;
        try {
            logId=MDC.get("ruid");
        }catch (Exception e){
            log.error("error occur while acquiring logid ",e);
        }
        if (StringUtils.isEmpty(logId)) {
            return setLogId();
        } else {
            return logId;
        }
    }

    public static void resetLogId() {
        cleanLogId();
        setLogId();
    }

    private static String setLogId() {
        String ip = "_0";
        String logId=StringUtils.EMPTY;
        try {
            List<String> networkIPList = NetworkUtil.getNetworkIPList();
            for (String str : networkIPList) {
                if (!"127.0.0.1".equals(str)) {
                    ip = "_" + str.substring(str.lastIndexOf(".") + 1);
                }
            }
            logId =ShortUUID.uuid() + ip;
            MDC.put("ruid", logId);
        } catch (Exception e) {
            log.error("setLogId error", e);
        }
        return logId;
    }

    public static void cleanLogId() {
        try {
            MDC.remove("ruid");
        }catch (Exception e){
            log.error("error occur when cleaning logid ",e);
        }
    }
}
