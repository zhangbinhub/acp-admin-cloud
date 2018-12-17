package pers.acp.springcloud.common.enums;

import pers.acp.core.exceptions.EnumValueUndefinedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangbin by 11/07/2018 14:54
 * @since JDK 11
 */
public enum LogLevel {

    INFO("INFO", 1),

    DEBUG("DEBUG", 2),

    WARN("WARN", 3),

    ERROR("ERROR", 4),

    TRACE("TRACE", 5),

    OTHER("OTHER", 6);

    LogLevel(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    private static Map<Integer, LogLevel> map;

    static {
        map = new HashMap<>();
        for (LogLevel level : values()) {
            map.put(level.getValue(), level);
        }
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static LogLevel getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        } else {
            throw new EnumValueUndefinedException(LogLevel.class, value);
        }
    }

    private String name;

    private Integer value;

}
