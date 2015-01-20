package com.sequenceiq.it.cloudbreak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.util.RestUtil;

public class AbstractCloudbreakIntegrationTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCloudbreakIntegrationTest.class);
    private final static int MAX_RETRY = 180;
    private final static int POLLING_INTERVAL = 10000;

    @Autowired
    protected IntegrationTestContext itContext;

    @BeforeMethod
    public void checkContextParameters() {
        Assert.assertNotNull(itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN), "Access token cannot be null.");
        Assert.assertNotNull(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER), "Cloudbreak server endpoint must be given!");
    }

    protected String getResourceIdByName(String resourcePath, String name) {
        Response resourceResponse = RestUtil.entityPathRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER),
                itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN)).get(resourcePath, name);
        return resourceResponse.jsonPath().getString("id");
    }

    protected void checkResponse(Response entityCreationResponse, HttpStatus httpStatus, ContentType contentType) {
        entityCreationResponse.then().statusCode(httpStatus.value()).contentType(contentType);
    }

    protected void waitForStackStatus(String stackId, String desiredStatus) {
        String stackStatus = null;
        int retryCount = 0;
        do {
//            if (retryCount > 0) {
                LOGGER.info("Waiting for stack status {}, stack id: {}, current status {} ...", desiredStatus, stackId, stackStatus);
                sleep();
//            }
            stackStatus = RestUtil.entityPathRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER),
                    itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN), "stackId", stackId).
                    get("stacks/{stackId}/status").jsonPath().get("status");
            if (stackStatus == null) {
                stackStatus = "TERMINATED";
            }
            retryCount++;
        } while (!desiredStatus.equals(stackStatus) && !stackStatus.contains("FAILED") && retryCount < MAX_RETRY);
        LOGGER.info("Stack {} is in desired status {}", stackId, stackStatus);
        if (stackStatus.contains("FAILED")) {
            Assert.fail("The stack has failed");
        }
        if (retryCount == MAX_RETRY) {
            Assert.fail("Timeout happened");
        }
    }

    private void sleep() {
        try {
            Thread.sleep(POLLING_INTERVAL);
        } catch (InterruptedException e) {
            LOGGER.warn("Ex during wait: {}", e);
        }
    }
}
