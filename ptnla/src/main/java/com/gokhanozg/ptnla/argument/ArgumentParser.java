package com.gokhanozg.ptnla.argument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mephala on 5/9/17.
 */
public class ArgumentParser {

    public static Map<String, String> getArguments(String[] args) throws Exception {
        if (args == null || args.length == 0)
            return Collections.emptyMap();
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-") && !arg.startsWith("--")) {
                String key = arg.substring(1);
                String value = "";
                if (i + 1 == args.length) {
                    throw new Exception("Parameter required after:" + arg);
                }
                for (int j = i + 1; j < args.length; j++) {
                    String val = args[j];
                    if (val.startsWith("-") && value.isEmpty()) {
                        throw new Exception("Parameter required after:" + arg);
                    } else if (val.startsWith("-")) {
                        break;
                    } else {
                        if (value.length() == 0) {
                            value = val;
                        } else {
                            value += " " + val;
                        }
                    }
                }
                argMap.put(key, value);
            } else if (arg.startsWith("--")) {
                argMap.put(arg.substring(2), "true");
            }
        }
        return argMap;
    }


}
