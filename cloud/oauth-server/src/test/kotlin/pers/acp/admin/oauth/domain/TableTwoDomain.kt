package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.jpa.MemberTwo
import pers.acp.admin.oauth.jpa.TableTwo
import pers.acp.admin.oauth.jpa.TableTwoRepository
import pers.acp.spring.cloud.lock.DistributedLock

@Service
@Transactional(readOnly = true)
class TableTwoDomain @Autowired
constructor(private val tableTwoRepository: TableTwoRepository) {
    @Transactional
    fun doTestSync(tableTwo: TableTwo) {
        tableTwoRepository.findByName(tableTwo.name).let {
            if (it.isPresent) {
                print(Thread.currentThread())
                println("  找到数据 name=${tableTwo.name}")
                it.get()
            } else {
                print(Thread.currentThread())
                println("  找不到数据 ---> name=${tableTwo.name}")
                TableTwo(name = tableTwo.name, value = tableTwo.value * 10
                )
            }
        }.apply {
            this.memberSet.add(MemberTwo(loginNo = tableTwo.memberSet.first().loginNo, two = this))
            tableTwoRepository.save(this)
        }
    }
}