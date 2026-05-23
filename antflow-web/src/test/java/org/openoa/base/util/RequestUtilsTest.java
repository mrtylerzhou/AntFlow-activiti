package org.openoa.base.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestUtilsTest extends MockBaseTest {

    @Nested
    @DisplayName("getRequestPostBytes")
    class GetRequestPostBytesTest {
        @Test
        @DisplayName("should return null when contentLength is negative")
        void negativeContentLength() throws IOException {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getContentLength()).thenReturn(-1);
            assertNull(RequestUtils.getRequestPostBytes(request));
        }

        @Test
        @DisplayName("should read bytes from request input stream")
        void readBytes() throws IOException {
            byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getContentLength()).thenReturn(data.length);
            when(request.getInputStream()).thenReturn(
                    new javax.servlet.ServletInputStream() {
                        private final ByteArrayInputStream bais = new ByteArrayInputStream(data);
                        @Override
                        public int read() { return bais.read(); }
                        @Override
                        public int read(byte[] b, int off, int len) { return bais.read(b, off, len); }
                        @Override
                        public boolean isFinished() { return bais.available() == 0; }
                        @Override
                        public boolean isReady() { return true; }
                        @Override
                        public void setReadListener(javax.servlet.ReadListener readListener) {}
                    });
            byte[] result = RequestUtils.getRequestPostBytes(request);
            assertArrayEquals(data, result);
        }

        @Test
        @DisplayName("should return empty byte array when contentLength is 0")
        void zeroContentLength() throws IOException {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getContentLength()).thenReturn(0);
            byte[] result = RequestUtils.getRequestPostBytes(request);
            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    @Nested
    @DisplayName("getRequestPostStr")
    class GetRequestPostStrTest {
        @Test
        @DisplayName("should use UTF-8 when charEncoding is null")
        void defaultUtf8Encoding() throws IOException {
            byte[] data = "你好世界".getBytes(StandardCharsets.UTF_8);
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getContentLength()).thenReturn(data.length);
            when(request.getCharacterEncoding()).thenReturn(null);
            when(request.getInputStream()).thenReturn(
                    new javax.servlet.ServletInputStream() {
                        private final ByteArrayInputStream bais = new ByteArrayInputStream(data);
                        @Override
                        public int read() { return bais.read(); }
                        @Override
                        public int read(byte[] b, int off, int len) { return bais.read(b, off, len); }
                        @Override
                        public boolean isFinished() { return bais.available() == 0; }
                        @Override
                        public boolean isReady() { return true; }
                        @Override
                        public void setReadListener(javax.servlet.ReadListener readListener) {}
                    });
            String result = RequestUtils.getRequestPostStr(request);
            assertEquals("你好世界", result);
        }

        @Test
        @DisplayName("should use specified encoding")
        void specifiedEncoding() throws IOException {
            byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getContentLength()).thenReturn(data.length);
            when(request.getCharacterEncoding()).thenReturn("UTF-8");
            when(request.getInputStream()).thenReturn(
                    new javax.servlet.ServletInputStream() {
                        private final ByteArrayInputStream bais = new ByteArrayInputStream(data);
                        @Override
                        public int read() { return bais.read(); }
                        @Override
                        public int read(byte[] b, int off, int len) { return bais.read(b, off, len); }
                        @Override
                        public boolean isFinished() { return bais.available() == 0; }
                        @Override
                        public boolean isReady() { return true; }
                        @Override
                        public void setReadListener(javax.servlet.ReadListener readListener) {}
                    });
            String result = RequestUtils.getRequestPostStr(request);
            assertEquals("hello", result);
        }
    }
}
