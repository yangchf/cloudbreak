package com.sequenceiq.it.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.testng.TestNG;

import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.IntegrationTestContextProperties;

import freemarker.template.TemplateException;

@Configuration
@ComponentScan("com.sequenceiq.it")
@ConfigurationProperties
@EnableConfigurationProperties(IntegrationTestContextProperties.class)
public class IntegrationTestConfiguration {
    @Autowired
    IntegrationTestContextProperties itContextProperties;

    @Bean
    public static PropertyResourceConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public IntegrationTestContext itContext() {
        return new IntegrationTestContext(itContextProperties.getPropertiesMap());
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() throws IOException, TemplateException {
        FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
        factory.setPreferFileSystemAccess(false);
        factory.setTemplateLoaderPath("classpath:/");
        return factory.createConfiguration();
    }

    @Bean
    public TestNG testNG() {
        return new TestNG();
    }
}
