package pers.acp.admin.route.definition;

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
public class RouteDefinition {

    private String id = UUID.randomUUID().toString();

    private List<PredicateDefinition> predicates = new ArrayList<>();

    private List<FilterDefinition> filters = new ArrayList<>();

    private URI uri;

    private int order = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PredicateDefinition> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<PredicateDefinition> predicates) {
        this.predicates = predicates;
    }

    public List<FilterDefinition> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDefinition> filters) {
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

    public RouteDefinition() {}

    public RouteDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse RouteDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }

        setId(text.substring(0, eqIdx));

        String[] args = tokenizeToStringArray(text.substring(eqIdx+1), ",");

        setUri(URI.create(args[0]));

        for (int i=1; i < args.length; i++) {
            this.predicates.add(new PredicateDefinition(args[i]));
        }
    }

}
