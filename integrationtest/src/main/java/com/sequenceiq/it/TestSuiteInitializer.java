package com.sequenceiq.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.jayway.restassured.response.Response;
import com.sequenceiq.it.config.IntegrationTestConfiguration;
import com.sequenceiq.it.util.RestUtil;

@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public class TestSuiteInitializer extends AbstractTestNGSpringContextTests {
    private final static String UAA_SERVER = "http://qa.uaa.sequenceiq.com";
    private final static String UAA_USER = "inttest";
    private final static String UAA_PASSWORD = "inttest";

    @Autowired
    private IntegrationTestContext itContext;

    @BeforeSuite
    @Parameters({ "uaaServer", "uaaUser", "uaaPassword" })
    public void initTestSuite(@Optional(UAA_SERVER) String uaaServer, @Optional(UAA_USER) String uaaUser, @Optional(UAA_PASSWORD) String uaaPassword) throws Exception {
        // Workaround of https://jira.spring.io/browse/SPR-4072
        springTestContextBeforeTestClass();
        springTestContextPrepareTestInstance();

        Response response = RestUtil.createAuthorizationRequest(uaaServer, uaaUser, uaaPassword).post("/oauth/authorize");
        response.then().statusCode(HttpStatus.FOUND.value());
        String accessToken = RestUtil.getAccessToken(response);
        Assert.assertNotNull(accessToken, "Access token cannot be null.");
        itContext.putContextParam(IntegrationTestContext.AUTH_TOKEN, accessToken);
    }
}
