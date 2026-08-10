package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebUtilsTest extends BaseTest {

    @Nested
    @DisplayName("toHttp(ServletResponse)")
    class ToHttpResponseTest {
        @Test
        @DisplayName("should cast ServletResponse to HttpServletResponse")
        void shouldCastResponse() {
            HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            HttpServletResponse result = WebUtils.toHttp(mockResponse);
            assertSame(mockResponse, result);
        }
    }

    @Nested
    @DisplayName("toHttp(ServletRequest)")
    class ToHttpRequestTest {
        @Test
        @DisplayName("should cast ServletRequest to HttpServletRequest")
        void shouldCastRequest() {
            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            HttpServletRequest result = WebUtils.toHttp(mockRequest);
            assertSame(mockRequest, result);
        }
    }
}
