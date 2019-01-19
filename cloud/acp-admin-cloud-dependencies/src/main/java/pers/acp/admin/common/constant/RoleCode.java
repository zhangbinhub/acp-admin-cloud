package pers.acp.admin.common.constant;

/**
 * 角色编码
 * 新建角色时，需要向该接口中增加对应的编码
 * 系统中配置的角色编码不应包含前缀prefix
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface RoleCode {

    String prefix = "ROLE_";

    /**
     * 新建角色时默认值
     */
    String OTHER = "OTHER";

    /**
     * 超级管理员
     */
    String ADMIN = "ADMIN";

    /**
     * 测试人员
     */
    String TEST = "TEST";

}
