package org.openoa.base.util;

import lombok.extern.slf4j.Slf4j;

import org.openoa.base.vo.SignatureRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvUtil {


    private static String SIGNATURE_REQUEST_KEY = "SIGNATURE_REQUEST";


    private static EnvUtil INSTANCE;

    public EnvUtil() {
        INSTANCE = this;
    }


    public static SignatureRequest getSignatureReq() {
        Object obj = ThreadLocalContainer.get(SIGNATURE_REQUEST_KEY);
        if (obj != null) {
            return (SignatureRequest) obj;
        } else {
            return new SignatureRequest();
        }
    }
}
