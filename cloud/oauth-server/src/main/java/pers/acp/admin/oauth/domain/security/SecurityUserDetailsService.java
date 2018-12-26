package pers.acp.admin.oauth.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.core.security.SHA256Utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 11/04/2018 15:19
 * @since JDK 11
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final UserRepository userRepository;

    @Autowired
    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据 username 获取用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 找不到用户信息异常
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        pers.acp.admin.oauth.entity.User user = userRepository.findByLoginno(username).orElse(null);
        if (user == null) {
            log.error("无此用户：" + username);
            throw new UsernameNotFoundException("无此用户：" + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        user.getRoleSet().forEach(role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode())); //角色编码
            role.getModuleSet().forEach(module -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(module.getCode())); //模块编码
            });
            role.getModuleFuncSet().forEach(moduleFunc -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(moduleFunc.getCode())); //功能编码
            });
        });
        return new User(user.getLoginno(),
                SHA256Utils.encrypt(user.getPassword() + CommonTools.getDateTimeString(new Date(), "yyyyMMddHH")),
                user.isEnabled(), true, true, true,
                grantedAuthorities);
    }

}
