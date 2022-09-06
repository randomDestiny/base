package com.iscloud.common.cfg;

import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.utils.JsonUtils;
import com.iscloud.common.utils.SpringContextUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Hybrid
 */
@Configuration
@Import(AutoRegisterConfig.ImportConfig.class)
public class AutoRegisterConfig {

    public static class ImportConfig implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        private AutoRegisterBean bean;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry reg) {
            if (this.bean != null) {
                JSONObject m = this.bean.getMapping();
                if (m != null && !m.isEmpty()) {
                    m.forEach((k, v) -> SpringContextUtils.beanRegistry(k, JsonUtils.toJSONObject(v), reg));
                }
            }
        }

        @Override
        public void setEnvironment(Environment env) {
            this.bean = SpringContextUtils.boundGet(AutoRegisterBean.class, env);
        }

    }
}
