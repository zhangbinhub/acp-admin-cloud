package pers.acp.admin.common.code;

/**
 * 功能编码
 * 新建功能时，需要向该接口增加对应的编码
 *
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
public interface FuncCode {

    String paramAdd = "param_add";

    String paramDelete = "param_delete";

    String paramUpdate = "param_update";

    String paramQuery = "param_query";

    String appAdd = "app_add";

    String appDelete = "app_delete";

    String appUpdate = "app_update";

    String appQuery = "app_query";

    String appUpdateSecret = "app_update_secret";

}
