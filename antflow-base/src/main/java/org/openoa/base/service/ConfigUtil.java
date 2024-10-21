package org.openoa.base.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigUtil {

    private static Boolean outsideCallbackOn;

    @Value("${outside.callback.switch:false}")
    public void setOutsideCallbackOn(boolean outsideCallbackOn) {
        ConfigUtil.outsideCallbackOn = outsideCallbackOn;
    }

    public static boolean getOutsideCallbackOn() {
        return Boolean.TRUE.equals(outsideCallbackOn);
    }
}