package pers.acp.springcloud.oauth.component;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码编码器
 *
 * @author zhangbin by 11/04/2018 17:14
 * @since JDK 11
 */
@Component
public class UserPasswordEncoder implements PasswordEncoder {

    /**
     * 编码
     *
     * @param rawPassword 原始密码
     * @return 编码结果
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    /**
     * 匹配
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 编码后的密码
     * @return 匹配结果
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equalsIgnoreCase(encodedPassword);
    }

}
