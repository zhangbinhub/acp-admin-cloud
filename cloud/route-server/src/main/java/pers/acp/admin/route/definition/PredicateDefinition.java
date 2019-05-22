package pers.acp.admin.route.definition;

import javax.validation.ValidationException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
public class PredicateDefinition {

    private String name;

    private Map<String, String> args = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public PredicateDefinition() {
    }

    public PredicateDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse PredicateDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }
        setName(text.substring(0, eqIdx));
        String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");
        for (int i = 0; i < args.length; i++) {
            this.args.put(NameUtils.generateName(i), args[i]);
        }
    }

}
