/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bsoa.rpc.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bsoa.rpc.common.json.JSON;
import io.bsoa.rpc.common.utils.ClassLoaderUtils;
import io.bsoa.rpc.common.utils.FileUtils;
import io.bsoa.rpc.exception.BsoaRuntimeException;

/**
 * <p></p>
 * <p>
 * Created by zhangg on 2016/12/10 22:22. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public class BsoaConfigs {

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BsoaConfigs.class);
    /**
     * 全部配置
     */
    private final static ConcurrentHashMap<String, Object> CFG = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<String, List<ConfigListener>> CFG_LISTENER = new ConcurrentHashMap<>();

    static {
        init();
    }
    private static void init() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Load config from file start!");
            }
            // loadDefault
            String json = FileUtils.file2String(BsoaConfigs.class, "bsoa_default.json", "UTF-8");
            Map map = JSON.parseObject(json, Map.class);
            CFG.putAll(map);

            // loadCustom();
            loadCustom("bsoa.json");
            loadCustom("META-INF/bsoa.json");

            CFG.putAll(new HashMap(System.getProperties())); //读取system.properties
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Load config from file end!");
                for (Map.Entry<String, Object> entry : CFG.entrySet()) {
                    LOGGER.debug("{}: {}", entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            throw new BsoaRuntimeException(22222, "", e);
        }
    }

    private static void loadCustom(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(BsoaConfigs.class);
        Enumeration<URL> urls = classLoader != null ? classLoader.getResources(fileName)
                : ClassLoader.getSystemResources(fileName);
        // 可能存在多个文件。
        if (urls != null) {
            List<CfgFile> allFile = new ArrayList<>();
            while (urls.hasMoreElements()) {
                // 读取一个文件
                URL url = urls.nextElement();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Loading custom config from file: {}", url);
                }
                try (InputStreamReader input = new InputStreamReader(url.openStream(), "utf-8");
                     BufferedReader reader = new BufferedReader(input)) {
                    StringBuilder context = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        context.append(line).append("\n");
                    }
                    Map map = JSON.parseObject(context.toString(), Map.class);
                    Integer order = (Integer) map.get(BsoaOptions.BSOA_CFG_ORDER);
                    allFile.add(new CfgFile(url, order == null ? 0 : order, map));
                }
            }
            Collections.sort(allFile, (o1, o2) -> o1.getOrder() - o2.getOrder());  // 排下序
            for (CfgFile file : allFile) {
                CFG.putAll(file.getMap());
            }
        }
    }

    private static class CfgFile {
        private URL url;
        private int order;
        private Map map;
        public CfgFile(URL url, int order, Map map) {
            this.url = url;
            this.order = order;
            this.map = map;
        }
        public URL getUrl() {
            return url;
        }
        public int getOrder() {
            return order;
        }
        public Map getMap() {
            return map;
        }
    }

    public static void putValue(String key, Object newValue) {
        Object oldValue = CFG.get(key);
        if (oldValue != null && oldValue.equals(newValue)) {
            // No onChange
        } else {
            CFG.put(key, newValue);
            List<ConfigListener> configListeners = CFG_LISTENER.get(key);
            for (ConfigListener configListener : configListeners) {
                configListener.onChange(oldValue, newValue);
            }
        }
    }

    public static boolean getBooleanValue(String primaryKey) {
        Boolean val = (Boolean) CFG.get(primaryKey);
        if (val == null) {
            throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey);
        } else {
            return val;
        }
    }

    public static boolean getBooleanValue(String primaryKey, String secondaryKey) {
        Boolean val = (Boolean) CFG.get(primaryKey);
        if (val == null) {
            val = (Boolean) CFG.get(secondaryKey);
            if (val == null) {
                throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey + "/" + secondaryKey);
            } else {
                return val;
            }
        } else {
            return val;
        }
    }

    public static int getIntValue(String primaryKey) {
        Integer val = (Integer) CFG.get(primaryKey);
        if (val == null) {
            throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey);
        } else {
            return val;
        }
    }

    public static <T> T getOrDefaultValue(String primaryKey, T defaultValue) {
        Object val = CFG.get(primaryKey);
        return val == null ? defaultValue : (T) val;
    }

    public static int getIntValue(String primaryKey, String secondaryKey) {
        Integer val = (Integer) CFG.get(primaryKey);
        if (val == null) {
            val = (Integer) CFG.get(secondaryKey);
            if (val == null) {
                throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey + "/" + secondaryKey);
            } else {
                return val;
            }
        } else {
            return val;
        }
    }

    public static <T extends Enum<T>> T getEnumValue(String primaryKey, Class<T> enumClazz) {
        String val = (String) CFG.get(primaryKey);
        if (val == null) {
            throw new BsoaRuntimeException(22222, "Not Found Key: " + primaryKey);
        } else {
            return Enum.valueOf(enumClazz, val);
        }
    }

    public static String getStringValue(String primaryKey) {
        String val = (String) CFG.get(primaryKey);
        if (val == null) {
            throw new BsoaRuntimeException(22222, "Not Found Key: " + primaryKey);
        } else {
            return val;
        }
    }

    public static String getStringValue(String primaryKey, String secondaryKey) {
        String val = (String) CFG.get(primaryKey);
        if (val == null) {
            val = (String) CFG.get(secondaryKey);
            if (val == null) {
                throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey + "/" + secondaryKey);
            } else {
                return val;
            }
        } else {
            return val;
        }
    }

    public static List getListValue(String primaryKey) {
        List val = (List) CFG.get(primaryKey);
        if (val == null) {
            throw new BsoaRuntimeException(22222, "Not found key: " + primaryKey);
        } else {
            return val;
        }
    }


    public static synchronized void subscribe(String key, ConfigListener configListener) {
        List<ConfigListener> listeners = CFG_LISTENER.get(key);
        if (listeners == null) {
            listeners = new ArrayList<>();
            CFG_LISTENER.put(key, listeners);
        }
        listeners.add(configListener);
    }

    public static synchronized void unSubscribe(String key, ConfigListener configListener) {
        List<ConfigListener> listeners = CFG_LISTENER.get(key);
        if (listeners != null) {
            listeners.remove(configListener);
            if (listeners.size() == 0) {
                CFG_LISTENER.remove(key);
            }
        }
    }

    /**
     *
     */
    public interface ConfigListener<T> {
        public void onChange(T oldValue, T newValue);
    }

}