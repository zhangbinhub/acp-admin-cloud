package pers.acp.admin.oauth.entity.route;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

}
