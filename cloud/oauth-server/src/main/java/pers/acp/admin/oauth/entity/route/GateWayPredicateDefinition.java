package pers.acp.admin.oauth.entity.route;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
public class GateWayPredicateDefinition {

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

}
