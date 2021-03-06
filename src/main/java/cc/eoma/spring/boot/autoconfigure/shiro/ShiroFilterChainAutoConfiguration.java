package cc.eoma.spring.boot.autoconfigure.shiro;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import javax.annotation.Resource;

@Configuration
@ConditionalOnClass({ApplicationContextAware.class, ApplicationContext.class})
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroFilterChainAutoConfiguration implements ApplicationContextAware {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Resource
  private ShiroProperties shiroProperties;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    if (this.shiroProperties != null && this.shiroProperties.getRealm() != null){
      AuthorizingRealm realm = applicationContext.getBean(this.shiroProperties.getRealm());
      DefaultWebSecurityManager defaultWebSecurityManager = applicationContext.getBean(DefaultWebSecurityManager.class);
      defaultWebSecurityManager.setRealm(realm);
    }

    if (this.shiroProperties != null && this.shiroProperties.getFilterChainDefinitioner() != null) {
      this.logger.info(
          "application config filter chain definitioner is: {}",
          this.shiroProperties.getFilterChainDefinitioner());

      FilterChainDefinitioner filterChainDefinitioner =
          applicationContext.getBean(this.shiroProperties.getFilterChainDefinitioner());
      Map<String, String> filterChainDefinitionMap =
          filterChainDefinitioner.getFilterChainDefinitionMap();
      this.logger.info("get filter chain definition map: {}", filterChainDefinitionMap);

      ShiroFilterFactoryBean shiroFilterFactoryBean =
          applicationContext.getBean(ShiroFilterFactoryBean.class);
      shiroFilterFactoryBean.getFilterChainDefinitionMap().putAll(filterChainDefinitionMap);
    }


  }
}
