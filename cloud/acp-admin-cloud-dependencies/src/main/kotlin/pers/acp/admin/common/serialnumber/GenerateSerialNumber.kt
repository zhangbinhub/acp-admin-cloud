package pers.acp.admin.common.serialnumber

import pers.acp.core.CommonTools

/**
 * @author zhang by 05/03/2020
 * @since JDK 11
 */
interface GenerateSerialNumber {
    /**
     * 生成序列号
     * @param keyString 序列号键名称
     * @param expirationTime 过期时间（重新计数超时时间），单位毫秒，默认24小时
     */
    fun getSerialNumber(keyString: String = CommonTools.getNowString(), expirationTime: Long = 86400000): Long
}