package com.sequenceiq.cloudbreak.converter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.ec2.model.InstanceType;
import com.sequenceiq.cloudbreak.controller.json.TemplateJson;
import com.sequenceiq.cloudbreak.controller.validation.AwsTemplateParam;
import com.sequenceiq.cloudbreak.domain.AwsInstanceType;
import com.sequenceiq.cloudbreak.domain.AwsTemplate;
import com.sequenceiq.cloudbreak.domain.CloudPlatform;

public class AwsTemplateConverterTest {

    private static final long DUMMY_ID = 1L;
    private static final String DUMMY_SSH_LOCATION = "127.0.0.0/0";
    private static final String DUMMY_AMI_ID = "dummyAmiId";
    private static final String DUMMY_KEY_NAME = "dummyKeyName";
    private static final String DUMMY_DESCRIPTION = "dummyDescription";
    private static final String DUMMY_NAME = "dummyName";

    private AwsTemplateConverter underTest;

    private AwsTemplate awsTemplate;

    private TemplateJson templateJson;

    @Before
    public void setUp() {
        underTest = new AwsTemplateConverter();
        awsTemplate = creteAwsTemplate();
        templateJson = createTemplateJson();
    }

    @Test
    public void testConvertAwsTemplateConverterEntityToJson() {
        // GIVEN
        // WHEN
        TemplateJson result = underTest.convert(awsTemplate);
        assertEquals(result.getCloudPlatform(), awsTemplate.cloudPlatform());
        assertEquals(result.getDescription(), awsTemplate.getDescription());
        assertEquals(result.getParameters().get(AwsTemplateParam.INSTANCE_TYPE.getName()), awsTemplate.getInstanceType().name());
    }

    @Test
    public void testConvertAwsTemplateConverterJsonToEntity() {
        // GIVEN
        // WHEN
        AwsTemplate result = underTest.convert(templateJson);
        assertEquals(result.cloudPlatform(), templateJson.getCloudPlatform());
    }

    private TemplateJson createTemplateJson() {
        TemplateJson templateJson = new TemplateJson();
        templateJson.setCloudPlatform(CloudPlatform.AWS);
        templateJson.setDescription(DUMMY_DESCRIPTION);
        templateJson.setName(DUMMY_NAME);
        Map<String, Object> props = new HashMap<>();
        props.put(AwsTemplateParam.INSTANCE_TYPE.getName(), InstanceType.C1Medium.name());
        props.put(AwsTemplateParam.SSH_LOCATION.getName(), DUMMY_SSH_LOCATION);
        templateJson.setVolumeCount(2);
        templateJson.setVolumeSize(60);
        props.put(AwsTemplateParam.VOLUME_TYPE.getName(), "Gp2");
        templateJson.setParameters(props);
        return templateJson;
    }

    private AwsTemplate creteAwsTemplate() {
        AwsTemplate awsTemplate = new AwsTemplate();
        awsTemplate.setId(DUMMY_ID);
        awsTemplate.setDescription(DUMMY_DESCRIPTION);
        awsTemplate.setInstanceType(AwsInstanceType.C1Medium);
        awsTemplate.setSshLocation(DUMMY_SSH_LOCATION);
        awsTemplate.setName(DUMMY_NAME);
        awsTemplate.setPublicInAccount(true);
        return awsTemplate;
    }

}
