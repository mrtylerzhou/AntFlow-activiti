package org.openoa.engine.conf.confval;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AsyncProperties.PREFIX)
public class AsyncProperties {

    public static final String PREFIX = "async";

    /**
     * 执行线程池核心线程数
     */
    private int corePoolSize=Runtime.getRuntime().availableProcessors();

    /**
     * 执行线程池最大线程数
     */
    private int maxPoolSize= 100;

    /**
     * 线程池BlockingQueue容量
     */
    private int queueCapacity= 200;

    /**
     * 处理线程名前缀
     */
    private String threadNamePrefix = "jimu-Async-Executor-";

    /**
     * keepAlive时间,单位秒
     */
    private Integer keepAliveSeconds=60;

    /**
     * 等待终止时间,单位秒
     */
    private Integer awaitTerminationSeconds=10;

    /**
     * 是否启用
     */
    boolean enabled = true;

    public Integer getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(Integer awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
