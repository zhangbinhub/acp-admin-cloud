package pers.acp.springcloud.oauth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 11/04/2018 15:19
 * @since JDK 11
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    /**
     * 根据 username 获取用户信息
     *
     * @param username 用户名
     * @return 用户对象
     * @throws UsernameNotFoundException 找不到用户信息异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//角色编码
        grantedAuthorities.add(new SimpleGrantedAuthority("ALL"));//权限编码
        return new User(username, username, true, true, true, true, grantedAuthorities);
    }

}
