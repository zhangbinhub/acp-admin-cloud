package pers.acp.admin.common.enumeration

import pers.acp.admin.common.constant.RuntimeName
import pers.acp.core.exceptions.EnumValueUndefinedException

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
enum class RuntimeInfoEnum(var value: String?, var configDes: String?, var enabled: Boolean) {
    ;

    companion object {

        private var map: MutableMap<String, RuntimeInfoEnum> = mutableMapOf()

        init {
            for (type in values()) {
                map[type.name] = type
            }
        }

        @Throws(EnumValueUndefinedException::class)
        fun getEnum(name: String): RuntimeInfoEnum {
            if (map.containsKey(name)) {
                return map.getValue(name)
            }
            throw EnumValueUndefinedException(RuntimeInfoEnum::class.java, name)
        }
    }

}
