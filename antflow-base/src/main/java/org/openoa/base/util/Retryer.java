package org.openoa.base.util;

import static java.util.concurrent.TimeUnit.SECONDS;

public interface Retryer extends Cloneable {

    void continueOrPropagate(RuntimeException e);

    class Default implements Retryer {
        private long period;
        private long maxPeriod;
        private int attempt;
        private int maxAttempts;

        public Default() {
            this(SECONDS.toMillis(1), 3);
        }

        public Default(long period, int maxAttempts){
            this.period = period;
            this.maxPeriod = SECONDS.toMillis(10);
            this.maxAttempts = maxAttempts;
            this.attempt = 1;
        }

        public Default(long period, long maxPeriod, int maxAttempts){
            this.period = period;
            this.maxPeriod = maxPeriod;
            this.maxAttempts = maxAttempts;
            this.attempt = 1;
        }

        @Override
        public void continueOrPropagate(RuntimeException e) {
            if (attempt++ >= maxAttempts) {
                throw e;
            }
            try {
                Thread.sleep(nextMaxInterval());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        long nextMaxInterval() {
            long interval = (long) (period * Math.pow(1.5, attempt - 1.0));
            return interval > maxPeriod ? maxPeriod : interval;
        }

    }
}
