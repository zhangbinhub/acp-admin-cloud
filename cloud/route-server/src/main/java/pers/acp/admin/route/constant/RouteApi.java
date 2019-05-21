package pers.acp.admin.route.constant;

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
public interface RouteApi {

    String basePath = "/route";

    String gateWayRouteConfig = "/gatewayroute";

    String gateWayRouteRefresh = gateWayRouteConfig + "/refresh";

}
