
package com.berry.oss.common.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Title CsvUtils
 *
 * @author berry_cooper
 * @version 1.0
 * @date 2019/4/28 15:31
 */
public class CsvUtils {

    private static final Map<String, String> SCHEDULED_MAP = new HashMap<>(16);


    static {
        readCsv();
    }

    public static String getCornByName(String name) {
        return SCHEDULED_MAP.get(name);
    }

    private static void readCsv() {
        ClassPathResource resource = new ClassPathResource("task_scheduled.csv");
        try {
            String line;
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            // 读取到的内容给line变量
            while ((line = br.readLine()) != null) {
                int firstGubIndex = line.indexOf(",");
                SCHEDULED_MAP.put(line.substring(0, firstGubIndex).trim(), line.substring(firstGubIndex).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
