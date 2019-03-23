package pers.acp.admin.oauth.route;

import javax.validation.ValidationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
public class GateWayRouteDefinition {

    private String id = UUID.randomUUID().toString();

    private List<GateWayPredicateDefinition> predicates = new ArrayList<>();

    private List<GateWayFilterDefinition> filters = new ArrayList<>();

    private URI uri;

    private int order = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GateWayPredicateDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<GateWayPredicateDefinition> predicates) {
        this.predicates = predicates;
    }

    public List<GateWayFilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<GateWayFilterDefinition> filters) {
        this.filters = filters;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public GateWayRouteDefinition() {}

    public GateWayRouteDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse RouteDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }

        setId(text.substring(0, eqIdx));

        String[] args = tokenizeToStringArray(text.substring(eqIdx+1), ",");

        setUri(URI.create(args[0]));

        for (int i=1; i < args.length; i++) {
            this.predicates.add(new GateWayPredicateDefinition(args[i]));
        }
    }

}
