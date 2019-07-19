package pers.acp.admin.oauth.base

import org.springframework.data.repository.NoRepositoryBean
import pers.acp.admin.common.base.BaseRepository

/**
 * @author zhangbin by 2018-1-15 16:59
 * @since JDK 11
 */
@NoRepositoryBean
interface OauthBaseRepository<T, ID> : BaseRepository<T, ID>