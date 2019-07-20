package pers.acp.admin.route.definition

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
internal object NameUtils {

    private const val GENERATED_NAME_PREFIX = "_genkey_"

    fun generateName(i: Int): String {
        return GENERATED_NAME_PREFIX + i
    }

}
