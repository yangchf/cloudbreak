package com.sequenceiq.it.cloudbreak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.sequenceiq.it.IntegrationTestContext;
import com.sequenceiq.it.config.IntegrationTestConfiguration;
import com.sequenceiq.it.util.FreeMarkerUtil;
import com.sequenceiq.it.util.RestUtil;

import freemarker.template.Template;

@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public class AwsTemplateCreationTest extends AbstractCloudbreakIntegrationTest {
    @Autowired
    private Template awsTemplateCreationTemplate;
    private List<Addition> additions;

    private static class Addition {
        private String groupName;
        private int nodeCount;

        public Addition(String groupName, int nodeCount) {
            this.groupName = groupName;
            this.nodeCount = nodeCount;
        }

        public String getGroupName() {
            return groupName;
        }

        public int getNodeCount() {
            return nodeCount;
        }
    }

    @BeforeMethod
    @Parameters({ "templateAdditions" })
    public void setup(@Optional("master,1 slave_1,3") String templateAdditions) {
        additions = new ArrayList<>();
        String[] additionsArray = templateAdditions.split(" ");
        for (String additionsString : additionsArray) {
            String[] additionArray = additionsString.split(",");
            additions.add(new Addition(additionArray[0], Integer.valueOf(additionArray[1])));
        }
    }

    @Test
    @Parameters({ "awsTemplateName", "awsInstanceType", "awsVolumeType", "awsAmiId", "awsVolumeCount", "awsVolumeSize"})
    public void testAwsTemplateCreation(@Optional("it-aws-template") String awsTemplateName, @Optional("T2Medium") String awsInstanceType,
            @Optional("Standard") String awsVolumeType, @Optional("ami-953cbbe2") String awsAmiId,
            @Optional("1") String awsVolumeCount, @Optional("10") String awsVolumeSize) {
        // GIVEN
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("awsTemplateName", awsTemplateName);
        templateModel.put("awsInstanceType", awsInstanceType);
        templateModel.put("awsVolumeType", awsVolumeType);
        templateModel.put("awsAmiId", awsAmiId);
        templateModel.put("awsVolumeCount", awsVolumeCount);
        templateModel.put("awsVolumeSize", awsVolumeSize);
        templateModel.put("region", "EU_WEST_1");
        // WHEN
        Response resourceCreationResponse = RestUtil.createEntityRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER),
                itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN), FreeMarkerUtil.renderTemplate(awsTemplateCreationTemplate, templateModel))
                .post("/user/templates");
        // THEN
        checkResponse(resourceCreationResponse, HttpStatus.CREATED, ContentType.JSON);
        List<InstanceGroup> instanceGroups = itContext.getContextParam(CloudbreakITContextConstants.TEMPLATE_ID, List.class);
        if (instanceGroups == null) {
            instanceGroups = new ArrayList<>();
            itContext.putContextParam(CloudbreakITContextConstants.TEMPLATE_ID, instanceGroups, true);
        }
        String templateId = resourceCreationResponse.jsonPath().getString("id");
        for (Addition addition : additions) {
            instanceGroups.add(new InstanceGroup(templateId, addition.getGroupName(), addition.getNodeCount()));
        }
    }

    @AfterSuite(dependsOnGroups = "stack")
    public void cleanUp() {
        List<InstanceGroup> instanceGroups = itContext.getContextParam(CloudbreakITContextConstants.TEMPLATE_ID, List.class);
        if (instanceGroups != null && !instanceGroups.isEmpty()) {
            InstanceGroup ig = instanceGroups.get(0);
            RestUtil.entityPathRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER),
                    itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN),
                    "templateId", ig.getTemplateId()).delete("/templates/{templateId}");
        }
    }
}
