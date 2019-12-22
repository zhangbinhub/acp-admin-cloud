package pers.acp.admin.common.hystrix

import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.WorkFlowServer
import pers.acp.admin.common.po.ProcessTerminationPo
import pers.acp.admin.common.po.ProcessHandlingPo
import pers.acp.admin.common.po.ProcessStartPo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.common.vo.ProcessHistoryActivityVo
import pers.acp.admin.common.vo.ProcessInstanceVo
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
class WorkFlowServerHystrix
constructor(logAdapter: LogAdapter) : BaseFeignHystrix<WorkFlowServer>(logAdapter) {
    override fun create(cause: Throwable?): WorkFlowServer {
        logAdapter.error("调用 workflow-server 异常: " + cause?.message, cause)
        return object : WorkFlowServer {
            @Throws(ServerException::class)
            override fun start(processStartPo: ProcessStartPo): InfoVo {
                val errMsg = "流程启动失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun claim(taskId: String): InfoVo {
                val errMsg = "任务领取失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun transfer(taskId: String, userId: String): InfoVo {
                val errMsg = "任务转办失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun delegate(taskId: String, acceptUserId: String): InfoVo {
                val errMsg = "任务委托办理失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun process(processHandlingPo: ProcessHandlingPo): InfoVo {
                val errMsg = "任务处理失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            override fun termination(processTerminationPo: ProcessTerminationPo): InfoVo {
                val errMsg = "强制终止流程实例失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun getInstance(processInstanceId: String): ProcessInstanceVo? {
                val errMsg = "获取流程实例失败"
                logAdapter.info(errMsg)
                return null
            }

            @Throws(ServerException::class)
            override fun getHistoryActivity(processInstanceId: String): MutableList<ProcessHistoryActivityVo> {
                val errMsg = "获取流程处理记录失败"
                logAdapter.info(errMsg)
                return mutableListOf()
            }

            @Throws(ServerException::class)
            override fun getTaskInfo(taskId: String): ProcessTaskVo? {
                val errMsg = "获取任务信息失败"
                logAdapter.info(errMsg)
                return null
            }

        }
    }
}