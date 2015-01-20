package com.sequenceiq.it.cloudbreak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.config.IntegrationTestConfiguration;

@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public class CloudbreakTestSuiteInitializer extends AbstractTestNGSpringContextTests {
    private static final String CLOUDBREAK_SERVER = "http://qa.cloudbreak-api.sequenceiq.com";

    @Autowired
    private IntegrationTestContext itContext;

    @BeforeSuite
    @Parameters("cloudbreakServer")
    // blueprintname, stackname
    public void initCloudbreakSuite(@Optional(CLOUDBREAK_SERVER) String cloudbreakServer) throws Exception {
        // Workaround of https://jira.spring.io/browse/SPR-4072
        springTestContextBeforeTestClass();
        springTestContextPrepareTestInstance();

        itContext.putContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER, cloudbreakServer);
    }
}
