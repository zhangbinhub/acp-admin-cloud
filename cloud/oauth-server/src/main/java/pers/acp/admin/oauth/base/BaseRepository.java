package pers.acp.admin.oauth.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author zhangbin by 2018-1-15 16:59
 * @since JDK 11
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaSpecificationExecutor<T>, JpaRepository<T, ID> {
}
