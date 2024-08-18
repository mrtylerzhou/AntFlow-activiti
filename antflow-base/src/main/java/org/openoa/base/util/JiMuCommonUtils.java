package org.openoa.base.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JiMuCommonUtils {
    public JiMuCommonUtils() {
    }

    public static String listToString(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            Collections.sort(list);
            StringBuilder sb = new StringBuilder();
            list.forEach((str) -> {
                sb.append(str).append(",");
            });
            return sb.toString();
        }
    }

    public static List<String> strToList(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            List<String> list = new ArrayList();
            String[] array = str.split(",");
            String[] var3 = array;
            int var4 = array.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String tmp = var3[var5];
                if (StringUtils.isNotBlank(tmp)) {
                    list.add(tmp);
                }
            }

            return list;
        }
    }

    public static String exceptionToString(Throwable e) {
        if (e == null) {
            return "";
        } else {
            Writer writer = null;
            PrintWriter printWriter = null;

            String var3;
            try {
                writer = new StringWriter();
                printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                var3 = writer.toString();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }

                    if (printWriter != null) {
                        printWriter.close();
                    }
                } catch (IOException var10) {
                }

            }

            return var3;
        }
    }
}
