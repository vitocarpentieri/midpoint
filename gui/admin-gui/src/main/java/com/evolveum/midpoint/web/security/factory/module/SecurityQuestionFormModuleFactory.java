/*
 * Copyright (c) 2010-2019 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.web.security.factory.module;

import com.evolveum.midpoint.model.api.authentication.AuthenticationChannel;
import com.evolveum.midpoint.model.api.authentication.ModuleAuthentication;
import com.evolveum.midpoint.model.api.authentication.ModuleWebSecurityConfiguration;
import com.evolveum.midpoint.web.security.module.ModuleWebSecurityConfig;
import com.evolveum.midpoint.web.security.module.SecurityQuestionsFormModuleWebSecurityConfig;
import com.evolveum.midpoint.web.security.module.authentication.SecurityQuestionFormModuleAuthentication;
import com.evolveum.midpoint.web.security.module.configuration.LoginFormModuleWebSecurityConfiguration;
import com.evolveum.midpoint.web.security.module.configuration.ModuleWebSecurityConfigurationImpl;
import com.evolveum.midpoint.web.security.provider.SecurityQuestionProvider;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Component;

/**
 * @author skublik
 */
@Component
public class SecurityQuestionFormModuleFactory extends AbstractCredentialModuleFactory {

    @Override
    public boolean match(AbstractAuthenticationModuleType moduleType) {
        if (moduleType instanceof SecurityQuestionsFormAuthenticationModuleType) {
            return true;
        }
        return false;
    }

    @Override
    protected ModuleWebSecurityConfiguration createConfiguration(AbstractAuthenticationModuleType moduleType, String prefixOfSequence, AuthenticationChannel authenticationChannel) {
        ModuleWebSecurityConfigurationImpl configuration = LoginFormModuleWebSecurityConfiguration.build(moduleType,prefixOfSequence);
        configuration.setPrefixOfSequence(prefixOfSequence);
        return configuration;
    }

    @Override
    protected ModuleWebSecurityConfig createModule(ModuleWebSecurityConfiguration configuration) {
        return  getObjectObjectPostProcessor().postProcess(new SecurityQuestionsFormModuleWebSecurityConfig((LoginFormModuleWebSecurityConfiguration) configuration));
    }

    //TODO
    @Override
    protected AuthenticationProvider createProvider(CredentialPolicyType usedPolicy) {
        return new SecurityQuestionProvider();
    }

    @Override
    protected Class<? extends CredentialPolicyType> supportedClass() {
        return SecurityQuestionsCredentialsPolicyType.class;
    }

    @Override
    protected ModuleAuthentication createEmptyModuleAuthentication(AbstractAuthenticationModuleType moduleType,
                                                                   ModuleWebSecurityConfiguration configuration) {
        SecurityQuestionFormModuleAuthentication moduleAuthentication = new SecurityQuestionFormModuleAuthentication();
        moduleAuthentication.setPrefix(configuration.getPrefix());
        moduleAuthentication.setCredentialName(((AbstractCredentialAuthenticationModuleType)moduleType).getCredentialName());
        moduleAuthentication.setCredentialType(supportedClass());
        moduleAuthentication.setNameOfModule(configuration.getNameOfModule());
        return moduleAuthentication;
    }

}
