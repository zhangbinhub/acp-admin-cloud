package pers.acp.admin.route.definition

import javax.validation.ValidationException
import java.util.LinkedHashMap

import org.springframework.util.StringUtils.tokenizeToStringArray

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
class PredicateDefinition {

    var name: String? = null

    private var args: MutableMap<String, String> = LinkedHashMap()

    fun getArgs(): Map<String, String> {
        return args
    }

    fun setArgs(args: MutableMap<String, String>) {
        this.args = args
    }

    constructor()

    constructor(text: String) {
        val eqIdx = text.indexOf('=')
        if (eqIdx <= 0) {
            throw ValidationException("Unable to parse PredicateDefinition text '" + text + "'" +
                    ", must be of the form name=value")
        }
        name = text.substring(0, eqIdx)
        val args = tokenizeToStringArray(text.substring(eqIdx + 1), ",")
        for (i in args.indices) {
            this.args[NameUtils.generateName(i)] = args[i]
        }
    }

}
