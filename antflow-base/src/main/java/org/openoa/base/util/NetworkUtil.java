package org.openoa.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class NetworkUtil {

    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
            log.debug("isHostConnectable err", e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.debug("socket.close err", e);
            }
        }
        return true;
    }

    public static List<String> getNetworkIPList() {
        List<String> list = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        list.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error("getNetworkIPList error", e);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(JimuJsonUtil.toJsonString(getNetworkIPList()));
    }

}