package pers.acp.admin.route.definition

import javax.validation.ValidationException
import java.net.URI
import java.util.UUID

import org.springframework.util.StringUtils.tokenizeToStringArray

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
class RouteDefinition {

    var id = UUID.randomUUID().toString()

    var predicates: MutableList<PredicateDefinition> = mutableListOf()

    var filters: MutableList<FilterDefinition> = mutableListOf()

    var uri: URI? = null

    var order = 0

    constructor() {}

    constructor(text: String) {
        val eqIdx = text.indexOf('=')
        if (eqIdx <= 0) {
            throw ValidationException("Unable to parse RouteDefinition text '" + text + "'" +
                    ", must be of the form name=value")
        }

        id = text.substring(0, eqIdx)

        val args = tokenizeToStringArray(text.substring(eqIdx + 1), ",")

        uri = URI.create(args[0])

        for (i in 1 until args.size) {
            this.predicates.add(PredicateDefinition(args[i]))
        }
    }

}
