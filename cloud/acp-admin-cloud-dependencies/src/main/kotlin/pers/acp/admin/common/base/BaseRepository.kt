package pers.acp.admin.common.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
@NoRepositoryBean
interface BaseRepository<T, ID> : JpaSpecificationExecutor<T>, JpaRepository<T, ID>
