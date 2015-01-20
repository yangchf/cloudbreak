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
public class GCCTemplateCreationTest extends AbstractCloudbreakIntegrationTest {
    @Autowired
    private Template gccTemplateCreationTemplate;
    private List<Addition> additions;
    private String instanceGroupName;
    private int nodeCount;

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
    @Parameters({ "gccName", "gccInstanceType", /*"gccZone",*/ "volumeType", "gccImageType", "volumeCount", "volumeSize"})
    public void testGCCTemplateCreation(@Optional("it-gcc-template") String gccName, @Optional("N1_STANDARD_2") String gccInstanceType,
            /*@Optional("ASIA_EAST1_A") String gccZone, */@Optional("HDD") String volumeType, @Optional("DEBIAN_HACK") String gccImageType,
            @Optional("1") String volumeCount, @Optional("30") String volumeSize) {
        // GIVEN
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("gccName", gccName);
        templateModel.put("gccInstanceType", gccInstanceType);
        //templateModel.put("gccZone", gccZone);
        templateModel.put("volumeType", volumeType);
        templateModel.put("gccImageType", gccImageType);
        templateModel.put("volumeCount", volumeCount);
        templateModel.put("volumeSize", volumeSize);
        // WHEN
        Response resourceCreationResponse = RestUtil.createEntityRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER), itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN), FreeMarkerUtil.renderTemplate(gccTemplateCreationTemplate, templateModel))
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
        if (instanceGroups != null) {
            for (InstanceGroup ig : instanceGroups) {
                RestUtil.entityPathRequest(itContext.getContextParam(CloudbreakITContextConstants.CLOUDBREAK_SERVER),
                        itContext.getContextParam(IntegrationTestContext.AUTH_TOKEN),
                        "templateId", ig.getTemplateId()).delete("/templates/{templateId}");
            }
        }
    }
}
