package org.openoa.base.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.SignatureRequest;

import static org.junit.jupiter.api.Assertions.*;

class EnvUtilTest extends BaseTest {

    @AfterEach
    void cleanup() {
        ThreadLocalContainer.clean();
    }

    @Nested
    @DisplayName("getSignatureReq")
    class GetSignatureReqTest {
        @Test
        @DisplayName("should return new SignatureRequest when nothing is set")
        void emptyThreadLocal() {
            SignatureRequest req = EnvUtil.getSignatureReq();
            assertNotNull(req);
            assertNull(req.getSystemType());
            assertNull(req.getAppVersion());
        }

        @Test
        @DisplayName("should return stored SignatureRequest")
        void storedRequest() {
            SignatureRequest stored = SignatureRequest.builder()
                    .systemType("iOS")
                    .appVersion("1.0.0")
                    .hardware("iPhone")
                    .systemVersion("16.0")
                    .build();
            ThreadLocalContainer.set("SIGNATURE_REQUEST", stored);
            SignatureRequest result = EnvUtil.getSignatureReq();
            assertSame(stored, result);
            assertEquals("iOS", result.getSystemType());
            assertEquals("1.0.0", result.getAppVersion());
        }

        @Test
        @DisplayName("should return new SignatureRequest after clean")
        void afterClean() {
            SignatureRequest stored = SignatureRequest.builder()
                    .systemType("Android")
                    .build();
            ThreadLocalContainer.set("SIGNATURE_REQUEST", stored);
            ThreadLocalContainer.clean();
            SignatureRequest result = EnvUtil.getSignatureReq();
            assertNotNull(result);
            assertNull(result.getSystemType());
        }
    }
}
