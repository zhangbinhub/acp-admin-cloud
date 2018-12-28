package pers.acp.admin.common.permission;

/**
 * 角色编码
 * 新建角色时，需要向该接口中增加对应的编码
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface RoleCode {

    /**
     * 超级管理员
     */
    String ADMIN = "ADMIN";

    /**
     * 测试人员
     */
    String TEST = "TEST";

}
