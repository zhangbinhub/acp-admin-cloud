package pers.acp.admin.workflow.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.workflow.constant.WorkFlowApi;
import pers.acp.admin.workflow.domain.WorkFlowDomain;

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(WorkFlowApi.basePath)
@Api("工作流控制")
public class WorkFlowController extends BaseController {

    private final WorkFlowDomain workFlowDomain;

    @Autowired
    public WorkFlowController(WorkFlowDomain workFlowDomain) {
        this.workFlowDomain = workFlowDomain;
    }

}
