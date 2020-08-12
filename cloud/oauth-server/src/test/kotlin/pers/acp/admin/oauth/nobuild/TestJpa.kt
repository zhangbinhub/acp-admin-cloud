package pers.acp.admin.oauth.nobuild

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import pers.acp.admin.oauth.BaseTest
import pers.acp.admin.oauth.domain.TableTwoDomain
import pers.acp.admin.oauth.jpa.MemberTwo
import pers.acp.admin.oauth.jpa.TableTwo
import pers.acp.spring.cloud.lock.DistributedLock

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
internal class TestJpa : BaseTest() {
    @Autowired
    private val distributedLock: DistributedLock? = null

    @Autowired
    private val tableTwoDomain: TableTwoDomain? = null

    @Test
    @Rollback(false)
    fun testSelectAndUpdate() = runBlocking(Dispatchers.Default) {
        for (index in 0..5000) {
            val name = "name${index % 9}"
            launch(Dispatchers.IO) {
                if (distributedLock!!.getLock(name, name, 3600000, false)) {
                    print(Thread.currentThread())
                    println("  获取锁 $name")
                    try {
                        withContext(Dispatchers.IO) {
                            tableTwoDomain!!.doTestSync(TableTwo(
                                    name = name,
                                    value = index.toDouble()
                            ).apply {
                                this.memberSet.add(MemberTwo(loginNo = "loginNo${index}"))
                            })
                        }
                    } finally {
                        distributedLock.releaseLock(name, name)
                        print(Thread.currentThread())
                        println("  释放锁 $name")
                    }
                }
            }
        }
    }
}