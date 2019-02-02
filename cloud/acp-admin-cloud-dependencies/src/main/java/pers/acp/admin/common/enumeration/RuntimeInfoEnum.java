package pers.acp.admin.common.enumeration;

import pers.acp.admin.common.constant.RuntimeName;
import pers.acp.core.exceptions.EnumValueUndefinedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
public enum RuntimeInfoEnum {

    logServerBackUpMaxHistory(RuntimeName.logServerBackUpMaxHistory, "180", "日志服务备份文件最大保留天数", true);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConfigDes() {
        return configDes;
    }

    public void setConfigDes(String configDes) {
        this.configDes = configDes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private String name;

    private String value;

    private String configDes;

    private boolean enabled;

    private static Map<String, RuntimeInfoEnum> map;

    static {
        map = new HashMap<>();
        for (RuntimeInfoEnum type : values()) {
            map.put(type.getName(), type);
        }
    }

    RuntimeInfoEnum(String name, String value, String configDes, boolean enabled) {
        this.name = name;
        this.value = value;
        this.configDes = configDes;
        this.enabled = enabled;
    }

    public static RuntimeInfoEnum getEnum(String name) throws EnumValueUndefinedException {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        throw new EnumValueUndefinedException(RuntimeInfoEnum.class, name);
    }

}
