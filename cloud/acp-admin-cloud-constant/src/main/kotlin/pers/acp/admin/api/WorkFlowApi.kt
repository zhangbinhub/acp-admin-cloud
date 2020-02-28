package pers.acp.admin.api

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
object WorkFlowApi {
    const val basePath = "/workflow"
    const val definition = "/definition"
    const val definitionFile = "$definition/file"
    const val definitionDeploy = "$definition/deploy"
    const val start = "/start"
    const val pending = "/pending"
    const val claim = "/claim"
    const val transfer = "/transfer"
    const val delegate = "/delegate"
    const val process = "/process"
    const val history = "/history"
    const val diagram = "/diagram"
    const val definitionDiagram = definition + diagram
    const val instance = "/instance"
    const val termination = "$instance/termination"
    const val myProcess = "$instance/my-process"
    const val task = "/task"
}