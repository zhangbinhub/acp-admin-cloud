package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.ModuleFunc;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.po.ModuleFuncPO;
import pers.acp.admin.oauth.repo.ModuleFuncRepository;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.vo.ModuleFuncVO;
import pers.acp.springboot.core.exceptions.ServerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class ModuleFuncDomain extends OauthBaseDomain {

    private final RoleRepository roleRepository;

    private final ModuleFuncRepository moduleFuncRepository;

    @Autowired
    public ModuleFuncDomain(UserRepository userRepository, RoleRepository roleRepository, ModuleFuncRepository moduleFuncRepository) {
        super(userRepository);
        this.roleRepository = roleRepository;
        this.moduleFuncRepository = moduleFuncRepository;
    }

    private List<ModuleFunc> sortModuleFuncList(List<ModuleFunc> moduleFuncList) {
        moduleFuncList.forEach(organization -> {
            if (!organization.getChildren().isEmpty()) {
                sortModuleFuncList(organization.getChildren());
            }
        });
        moduleFuncList.sort(Comparator.comparing(ModuleFunc::getCode));
        return moduleFuncList;
    }

    public List<ModuleFunc> getModuleFuncListByAppId(String appId) {
        return sortModuleFuncList(formatToTreeList(moduleFuncRepository.findByAppid(appId).stream().collect(Collectors.toMap(ModuleFunc::getId, moduleFunc -> moduleFunc))));
    }

    public List<ModuleFunc> getAllModuleFuncList() {
        return sortModuleFuncList(formatToTreeList(moduleFuncRepository.findAll().stream().collect(Collectors.toMap(ModuleFunc::getId, moduleFunc -> moduleFunc))));
    }

    private ModuleFunc doSave(ModuleFunc moduleFunc, ModuleFuncPO moduleFuncPO) {
        moduleFunc.setName(moduleFuncPO.getName());
        moduleFunc.setCode(moduleFuncPO.getCode());
        moduleFunc.setParentid(moduleFuncPO.getParentid());
        moduleFunc.setRoleSet(new HashSet<>(roleRepository.findAllById(moduleFuncPO.getRoleIds())));
        return moduleFuncRepository.save(moduleFunc);
    }

    @Transactional
    public ModuleFunc doCreate(ModuleFuncPO moduleFuncPO) {
        ModuleFunc moduleFunc = new ModuleFunc();
        moduleFunc.setAppid(moduleFuncPO.getAppid());
        moduleFunc.setCovert(true);
        return doSave(moduleFunc, moduleFuncPO);
    }

    @Transactional
    public void doDelete(List<String> idList) throws ServerException {
        List<ModuleFunc> menuList = moduleFuncRepository.findByParentidIn(idList);
        if (!menuList.isEmpty()) {
            throw new ServerException("存在下级模块功能，不允许删除");
        }
        moduleFuncRepository.deleteByIdInAndCovert(idList, true);
    }

    @Transactional
    public ModuleFunc doUpdate(ModuleFuncPO moduleFuncPO) throws ServerException {
        Optional<ModuleFunc> moduleFuncOptional = moduleFuncRepository.findById(moduleFuncPO.getId());
        if (moduleFuncOptional.isEmpty()) {
            throw new ServerException("找不到模块功能信息");
        }
        ModuleFunc moduleFunc = moduleFuncOptional.get();
        return doSave(moduleFunc, moduleFuncPO);
    }

    public ModuleFuncVO getModuleFuncInfo(String moduleFuncId) throws ServerException {
        Optional<ModuleFunc> moduleFuncOptional = moduleFuncRepository.findById(moduleFuncId);
        if (moduleFuncOptional.isEmpty()) {
            throw new ServerException("找不到模块功能信息");
        }
        ModuleFunc moduleFunc = moduleFuncOptional.get();
        ModuleFuncVO moduleFuncVO = new ModuleFuncVO();
        moduleFuncVO.setId(moduleFunc.getId());
        moduleFuncVO.setAppid(moduleFunc.getAppid());
        moduleFuncVO.setCode(moduleFunc.getCode());
        moduleFuncVO.setName(moduleFunc.getName());
        moduleFuncVO.setParentid(moduleFunc.getParentid());
        moduleFuncVO.setRoleIds(moduleFunc.getRoleSet().stream().map(Role::getId).collect(Collectors.toList()));
        return moduleFuncVO;
    }

}
