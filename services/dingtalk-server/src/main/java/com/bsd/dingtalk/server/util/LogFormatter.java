package com.bsd.dingtalk.server.util;

public class LogFormatter {
    private static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String getKVLogData(LogEvent logEvent, KeyValue... args) {
        return getKVLogData(logEvent, null, null, null, args);
    }

    public static String getKVLogData(LogEvent logEvent, String message, KeyValue... args) {
        return getKVLogData(logEvent, message, null, null, args);
    }

    public static String getKVLogData(LogEvent logEvent, String message, String errCode, String errMsg, KeyValue... args) {
        StringBuilder logData = new StringBuilder();
        logData.append("logEvent:");
        logData.append(logEvent == null ? "" : logEvent.getValue());

        if (!isEmpty(message)) {
            logData.append("\t");
            logData.append("msg:");
            logData.append(message);
        }
        if (!isEmpty(errCode)) {
            logData.append("\t");
            logData.append("errCode:");
            logData.append(errCode);
        }
        if (!isEmpty(errMsg)) {
            logData.append("\t");
            logData.append("errMsg:");
            logData.append(errMsg);
        }
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                KeyValue keyValue = args[i];
                if (keyValue != null) {
                    logData.append("\t");
                    logData.append(keyValue.getKey());
                    logData.append(":");
                    logData.append(keyValue.getValue());
                }
            }
        }

        return logData.toString();
    }

    public static enum LogEvent {
        START("开始"), END("结束");

        private String value;

        private LogEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class KeyValue {
        private String key;
        private Object value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return String.valueOf(value);
        }

        private KeyValue(String key, Object value) {
            super();
            this.key = key;
            this.value = value;
        }

        public static KeyValue getNew(String key, Object value) {
            return new KeyValue(key, value);
        }
    }
}
