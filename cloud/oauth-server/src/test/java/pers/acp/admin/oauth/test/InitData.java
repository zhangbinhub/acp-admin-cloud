package pers.acp.admin.oauth.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.BaseTest;
import pers.acp.admin.oauth.entity.*;
import pers.acp.admin.oauth.repo.*;
import pers.acp.core.CommonTools;
import pers.acp.core.security.SHA256Utils;

/**
 * @author zhang by 18/12/2018
 * @since JDK 11
 */
class InitData extends BaseTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    /**
     * 初始化数据，仅可执行一次
     */
    @Test
    @Transactional
    @Rollback(false)
    void doInit() {
        Application application = new Application();
        application.setAppname("Acp-Admin");
        application.setSecret(CommonTools.getUuid());
        application.setCovert(false);
        application.setSort(0);
        application = applicationRepository.save(application);

        Menu menu1 = new Menu();
        menu1.setAppid(application.getId());
        menu1.setName("系统配置");
        menu1.setIconClass("fa-cogs");
        menu1.setIconColor("#1b992f");
        menu1.setPath("");
        menu1.setParentid(application.getId());
        menu1.setSort(0);
        menu1.setEnabled(true);
        menu1.setCovert(false);
        menu1.setOpentype(0);
        menu1.setDialogH(0);
        menu1.setDialogW(0);
        menu1 = menuRepository.save(menu1);

        Menu menu2 = new Menu();
        menu2.setAppid(application.getId());
        menu2.setName("用户配置");
        menu2.setIconClass("fa-users");
        menu2.setIconColor("#354ab8");
        menu2.setPath("/view/page/user/user");
        menu2.setParentid(menu1.getId());
        menu2.setSort(1);
        menu2.setEnabled(true);
        menu2.setCovert(false);
        menu2.setOpentype(0);
        menu2.setDialogH(0);
        menu2.setDialogW(0);
        menu2 = menuRepository.save(menu2);

        Menu menu3 = new Menu();
        menu3.setAppid(application.getId());
        menu3.setName("机构配置");
        menu3.setIconClass("fa-deviantart");
        menu3.setIconColor("#354ab8");
        menu3.setPath("/view/page/department/department");
        menu3.setParentid(menu1.getId());
        menu3.setSort(2);
        menu3.setEnabled(true);
        menu3.setCovert(false);
        menu3.setOpentype(0);
        menu3.setDialogH(0);
        menu3.setDialogW(0);
        menu3 = menuRepository.save(menu3);

        Menu menu4 = new Menu();
        menu4.setAppid(application.getId());
        menu4.setName("demo");
        menu4.setIconClass("fa-cogs");
        menu4.setIconColor("#1b992f");
        menu4.setPath("");
        menu4.setParentid(application.getId());
        menu4.setSort(0);
        menu4.setEnabled(true);
        menu4.setCovert(false);
        menu4.setOpentype(0);
        menu4.setDialogH(0);
        menu4.setDialogW(0);
        menu4 = menuRepository.save(menu4);

        Menu menu5 = new Menu();
        menu5.setAppid(application.getId());
        menu5.setName("上传");
        menu5.setIconClass("fa-users");
        menu5.setIconColor("#354ab8");
        menu5.setPath("/view/page/demo/upload?_type=0");
        menu5.setParentid(menu4.getId());
        menu5.setSort(1);
        menu5.setEnabled(true);
        menu5.setCovert(true);
        menu5.setOpentype(0);
        menu5.setDialogH(0);
        menu5.setDialogW(0);
        menu5 = menuRepository.save(menu5);

        Role role = new Role();
        role.setAppid(application.getId());
        role.setName("超级管理员");
        role.setCode("ADMIN");
        role.setLevels(0);
        role.setSort(0);
        role.getMenuSet().add(menu1);
        role.getMenuSet().add(menu2);
        role.getMenuSet().add(menu3);
        role.getMenuSet().add(menu4);
        role.getMenuSet().add(menu5);
        role = roleRepository.save(role);

        User user = new User();
        user.setName("超级管理员");
        user.setLoginno("admin");
        user.setPassword(SHA256Utils.encrypt(SHA256Utils.encrypt("888888") + "admin"));
        user.setLevels(0);
        user.setEnabled(true);
        user.setSort(0);
        user.getRoleSet().add(role);
        userRepository.save(user);
    }

}
