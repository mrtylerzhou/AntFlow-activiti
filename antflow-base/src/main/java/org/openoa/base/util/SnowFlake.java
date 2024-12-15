package org.openoa.base.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 改进版 SnowFlake，适用于 Kubernetes 环境。
 * 在k8s环境中需要设置变量POD_NAME或者其它变量能惟一标识pod的,改下代码即可
 */
public class SnowFlake {

    private static final long twepoch = 12888349746579L;
    private static final long workerIdBits = 5L; // 机器标识位数
    private static final long datacenterIdBits = 5L; // 数据中心标识位数
    private static final long sequenceBits = 12L; // 毫秒内序列位数

    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits); // 最大机器ID
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 最大数据中心ID

    private static final long workerIdShift = sequenceBits; // 机器ID左移位数
    private static final long datacenterIdShift = sequenceBits + workerIdBits; // 数据中心ID左移位数
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; // 时间戳左移位数
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits); // 序列号掩码

    private static long workerId; // 机器ID
    private static long datacenterId; // 数据中心ID
    private static long sequence = 0L; // 当前毫秒内序列
    private static long lastTimestamp = -1L; // 上次生成ID的时间戳

    // 静态初始化 workerId 和 datacenterId
    static {
        workerId = getWorkerId();
        datacenterId = getDatacenterId();
    }

    /**
     * 生成下一个唯一 ID（静态方法）。
     */
    public static synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 序列号用尽，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 阻塞等待直到下一毫秒。
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳（毫秒）。
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取机器 ID。
     */
    private static long getWorkerId() {
        try {
            String podName = System.getenv("POD_NAME");
            if (podName != null) {
                return Math.abs(podName.hashCode()) & maxWorkerId;
            }

            InetAddress ip = InetAddress.getLocalHost();
            return Math.abs(ip.hashCode()) & maxWorkerId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取数据中心 ID。
     */
    private static long getDatacenterId() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            return Math.abs(sb.toString().hashCode()) & maxDatacenterId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

}
