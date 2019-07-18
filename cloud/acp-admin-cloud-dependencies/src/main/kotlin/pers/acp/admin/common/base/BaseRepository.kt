package pers.acp.admin.common.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

import java.io.Serializable

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
interface BaseRepository<T, ID : Serializable> : JpaSpecificationExecutor<T>, JpaRepository<T, ID>
