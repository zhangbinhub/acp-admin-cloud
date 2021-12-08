package pers.acp.admin.workflow.base

import org.flowable.common.engine.api.delegate.event.FlowableEventListener

abstract class BaseEventListener : FlowableEventListener {
    override fun isFailOnException(): Boolean = false
    override fun isFireOnTransactionLifecycleEvent(): Boolean = false
    override fun getOnTransaction(): String? = null
}