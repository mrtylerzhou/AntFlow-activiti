package org.openoa.engine.conf.mvc;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class MVCRequestWrapped extends HttpServletRequestWrapper {

    private byte[] bytes;
    private WrappedServletInputStream wrappedServletInputStream;

    public MVCRequestWrapped(HttpServletRequest request) throws IOException {
        super(request);
        // 读取输入流里的请求参数，并保存到bytes里
        bytes = IOUtils.toByteArray(request.getInputStream());
        String params = new String(bytes, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        this.wrappedServletInputStream = new WrappedServletInputStream(byteArrayInputStream);

        // 很重要，把post参数重新写入请求流
        reWriteInputStream();

    }

    /**
     * 把参数重新写进请求里
     */
    public void reWriteInputStream() {
        wrappedServletInputStream.setStream(new ByteArrayInputStream(bytes != null ? bytes : new byte[0]));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return wrappedServletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(wrappedServletInputStream));
    }

    /**
     * 获取post参数，可以自己再转为相应格式
     */
    public String getRequestParams() throws IOException {
        return new String(bytes, this.getCharacterEncoding());
    }

    private class WrappedServletInputStream extends ServletInputStream {

        public WrappedServletInputStream(InputStream stream) {
            this.sourceStream = stream;
        }
        private InputStream sourceStream;
        private boolean finished=false;
        public final InputStream getSourceStream(){
            return this.sourceStream;
        }

        public void setStream(InputStream stream) {
            this.sourceStream = stream;
        }

        @Override
        public int available() throws IOException {
            return this.sourceStream.available();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        @Override
        public int read() throws IOException {
            int data=this.sourceStream.read();
            if(data==-1){
                this.finished=true;
            }
            return data;
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}

