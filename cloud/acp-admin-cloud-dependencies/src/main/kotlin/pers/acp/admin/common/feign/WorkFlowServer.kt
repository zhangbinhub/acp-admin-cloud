package pers.acp.admin.common.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import pers.acp.admin.api.CommonPath
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
import javax.validation.Valid

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
@FeignClient(value = "workflow-server", fallbackFactory = WorkFlowServerHystrix::class)
interface WorkFlowServer {
    /**
     * 启动流程
     */
    @PutMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun startInner(@RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 启动流程
     */
    @PutMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.start + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun startByUser(@PathVariable userId: String, @RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 流程处理
     */
    @PostMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.process + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun processByUser(@PathVariable userId: String, @RequestBody @Valid processHandlingPo: ProcessHandlingPo): InfoVo

    /**
     * 获取指定用户的待处理任务
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.pending + "/{processInstanceId}/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun pendingByUser(@PathVariable processInstanceId: String, @PathVariable userId: String): List<ProcessTaskVo>

    /**
     * 启动流程
     */
    @PutMapping(value = [WorkFlowApi.basePath + WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun start(@RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 领取任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.claim + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun claim(@PathVariable taskId: String): InfoVo

    /**
     * 转办任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.transfer + "/{taskId}/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun transfer(@PathVariable taskId: String, @PathVariable userId: String): InfoVo

    /**
     * 委托任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.delegate + "/{taskId}/{acceptUserId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delegate(@PathVariable taskId: String, @PathVariable acceptUserId: String): InfoVo

    /**
     * 流程处理
     */
    @PostMapping(value = [WorkFlowApi.basePath + WorkFlowApi.process], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun process(@RequestBody processHandlingPo: ProcessHandlingPo): InfoVo

    /**
     * 强制终止流程实例
     */
    @PostMapping(value = [WorkFlowApi.basePath + WorkFlowApi.termination], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun termination(@RequestBody processTerminationPo: ProcessTerminationPo): InfoVo

    /**
     * 获取流程实例信息
     */
    @GetMapping(value = [WorkFlowApi.basePath + WorkFlowApi.instance + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getInstance(@PathVariable processInstanceId: String): ProcessInstanceVo?

    /**
     * 获取流程处理记录
     */
    @GetMapping(value = [WorkFlowApi.basePath + WorkFlowApi.history + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getHistoryActivity(@PathVariable processInstanceId: String): List<ProcessHistoryActivityVo>

    /**
     * 获取任务信息
     */
    @GetMapping(value = [WorkFlowApi.basePath + WorkFlowApi.task + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getTaskInfo(@PathVariable taskId: String): ProcessTaskVo?
}