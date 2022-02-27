package pers.acp.admin.oauth.nobuild

import io.github.zhangbinhub.acp.core.CommonTools
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pers.acp.admin.common.serialnumber.GenerateSerialNumber
import pers.acp.admin.oauth.BaseTest

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
internal class TestSerialNumber : BaseTest() {
    @Autowired
    private val generateSerialNumber: GenerateSerialNumber? = null

    @Test
    fun testGenerateSerialNumber() {
        val key = "1TEST"
        for (i in 1..1000) {
            generateSerialNumber!!.getSerialNumber(key).let {
                println("serial number: $it")
                println(CommonTools.strFillIn(it.toString(), 5, 0, "0"))
            }
        }
    }
}