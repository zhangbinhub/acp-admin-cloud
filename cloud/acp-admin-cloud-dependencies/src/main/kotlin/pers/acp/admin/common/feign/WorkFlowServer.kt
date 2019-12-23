package pers.acp.admin.common.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import pers.acp.admin.api.WorkFlowApi
import pers.acp.admin.common.hystrix.WorkFlowServerHystrix
import pers.acp.admin.common.po.ProcessTerminationPo
import pers.acp.admin.common.po.ProcessHandlingPo
import pers.acp.admin.common.po.ProcessStartPo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.common.vo.ProcessHistoryActivityVo
import pers.acp.admin.common.vo.ProcessInstanceVo
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
@FeignClient(value = "workflow-server", fallbackFactory = WorkFlowServerHystrix::class)
interface WorkFlowServer {
    /**
     * 启动流程
     */
    @RequestMapping(value = [WorkFlowApi.start], method = [RequestMethod.PUT],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun start(@RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 领取任务
     */
    @RequestMapping(value = [WorkFlowApi.claim + "/{taskId}"], method = [RequestMethod.PATCH],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun claim(@PathVariable taskId: String): InfoVo

    /**
     * 转办任务
     */
    @RequestMapping(value = [WorkFlowApi.transfer + "/{taskId}/{userId}"], method = [RequestMethod.PATCH],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun transfer(@PathVariable taskId: String, @PathVariable userId: String): InfoVo

    /**
     * 委托任务
     */
    @RequestMapping(value = [WorkFlowApi.delegate + "/{taskId}/{acceptUserId}"], method = [RequestMethod.PATCH],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun delegate(@PathVariable taskId: String, @PathVariable acceptUserId: String): InfoVo

    /**
     * 流程处理
     */
    @RequestMapping(value = [WorkFlowApi.process], method = [RequestMethod.POST],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun process(@RequestBody processHandlingPo: ProcessHandlingPo): InfoVo

    /**
     * 强制终止流程实例
     */
    @RequestMapping(value = [WorkFlowApi.termination], method = [RequestMethod.POST],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun termination(@RequestBody processTerminationPo: ProcessTerminationPo): InfoVo

    /**
     * 获取流程实例信息
     */
    @RequestMapping(value = [WorkFlowApi.instance + "/{processInstanceId}"], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun getInstance(@PathVariable processInstanceId: String): ProcessInstanceVo?

    /**
     * 获取流程处理记录
     */
    @RequestMapping(value = [WorkFlowApi.history + "/{processInstanceId}"], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun getHistoryActivity(@PathVariable processInstanceId: String): MutableList<ProcessHistoryActivityVo>

    /**
     * 获取任务信息
     */
    @RequestMapping(value = [WorkFlowApi.task + "/{taskId}"], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun getTaskInfo(@PathVariable taskId: String): ProcessTaskVo?
}