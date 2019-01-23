package pers.acp.admin.oauth.base;

import org.springframework.data.repository.NoRepositoryBean;
import pers.acp.admin.common.base.BaseRepository;

import java.io.Serializable;

/**
 * @author zhangbin by 2018-1-15 16:59
 * @since JDK 11
 */
@NoRepositoryBean
public interface OauthBaseRepository<T, ID extends Serializable> extends BaseRepository<T, ID> {
}
