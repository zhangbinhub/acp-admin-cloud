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
    fun startInner(@RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 强制终止流程实例
     */
    @DeleteMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.termination], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun terminationInner(@RequestBody processTerminationPo: ProcessTerminationPo): InfoVo

    /**
     * 启动流程
     */
    @PutMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.start + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startByUser(@PathVariable userId: String, @RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 流程处理
     */
    @PostMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.process + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun processByUser(@PathVariable userId: String, @RequestBody processHandlingPo: ProcessHandlingPo): InfoVo

    /**
     * 获取指定用户的待处理任务
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.pending + "/{processInstanceId}/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun pendingByUser(@PathVariable processInstanceId: String, @PathVariable userId: String): List<ProcessTaskVo>

    /**
     * 获取流程实例信息
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.instance + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getInstance(@PathVariable processInstanceId: String): ProcessInstanceVo?

    /**
     * 获取任务信息
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + WorkFlowApi.task + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTaskInfoOpen(@PathVariable taskId: String): ProcessTaskVo?

    /**
     * 启动流程
     */
    @PutMapping(value = [WorkFlowApi.basePath + WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun start(@RequestBody processStartPo: ProcessStartPo): InfoVo

    /**
     * 领取任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.claim + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun claim(@PathVariable taskId: String): InfoVo

    /**
     * 转办任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.transfer + "/{taskId}/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transfer(@PathVariable taskId: String, @PathVariable userId: String): InfoVo

    /**
     * 委托任务
     */
    @PatchMapping(value = [WorkFlowApi.basePath + WorkFlowApi.delegate + "/{taskId}/{acceptUserId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun delegate(@PathVariable taskId: String, @PathVariable acceptUserId: String): InfoVo

    /**
     * 流程处理
     */
    @PostMapping(value = [WorkFlowApi.basePath + WorkFlowApi.process], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun process(@RequestBody processHandlingPo: ProcessHandlingPo): InfoVo

    /**
     * 强制终止流程实例
     */
    @DeleteMapping(value = [WorkFlowApi.basePath + WorkFlowApi.termination], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun termination(@RequestBody processTerminationPo: ProcessTerminationPo): InfoVo

    /**
     * 获取流程处理记录
     */
    @GetMapping(value = [WorkFlowApi.basePath + WorkFlowApi.history + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getHistoryActivity(@PathVariable processInstanceId: String): List<ProcessHistoryActivityVo>

    /**
     * 获取任务信息
     */
    @GetMapping(value = [WorkFlowApi.basePath + WorkFlowApi.task + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTaskInfo(@PathVariable taskId: String): ProcessTaskVo?
}