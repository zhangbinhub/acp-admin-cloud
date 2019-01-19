package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.ModuleFunc;
import pers.acp.admin.oauth.repo.ModuleFuncRepository;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class ModuleFuncDomain extends OauthBaseDomain {

    private final ModuleFuncRepository moduleFuncRepository;

    @Autowired
    public ModuleFuncDomain(UserRepository userRepository, ModuleFuncRepository moduleFuncRepository) {
        super(userRepository);
        this.moduleFuncRepository = moduleFuncRepository;
    }

    public List<ModuleFunc> getModuleFuncList(String appId) {
        List<ModuleFunc> result = new ArrayList<>();
        Map<String, ModuleFunc> moduleFuncMap = moduleFuncRepository.findByAppid(appId).stream().collect(Collectors.toMap(ModuleFunc::getId, menu -> menu));
        moduleFuncMap.forEach((id, moduleFunc) -> {
            if (moduleFuncMap.containsKey(moduleFunc.getParentid())) {
                moduleFuncMap.get(moduleFunc.getParentid()).getChildren().add(moduleFunc);
            } else {
                result.add(moduleFunc);
            }
        });
        return result;
    }

}
