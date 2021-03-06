package com.sequenceiq.cloudbreak.service.stack.connector.openstack;

import static java.util.Collections.singletonMap;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.controller.InternalServerException;
import com.sequenceiq.cloudbreak.domain.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.OpenStackTemplate;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Service
public class HeatTemplateBuilder {

    public static final String CB_INSTANCE_GROUP_NAME = "cb_instance_group_name";
    private static final String MOUNT_PREFIX = "/mnt/fs";
    private static final String DEVICE_PREFIX = "/dev/vd";
    private static final char[] DEVICE_CHAR = {'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};

    @Autowired
    private Configuration freemarkerConfiguration;

    public String build(String templatePath, List<InstanceGroup> instanceGroups, String userData) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("agents", buildInstances(instanceGroups));
            model.put("userdata", formatUserData(userData));
            return processTemplateIntoString(freemarkerConfiguration.getTemplate(templatePath, "UTF-8"), model);
        } catch (IOException | TemplateException e) {
            throw new InternalServerException("Failed to process the OpenStack HeatTemplate", e);
        }
    }

    private List<OpenStackInstance> buildInstances(List<InstanceGroup> instanceGroups) {
        List<OpenStackInstance> agents = new ArrayList<>();
        for (InstanceGroup group : instanceGroups) {
            OpenStackTemplate template = (OpenStackTemplate) group.getTemplate();
            for (int i = 0; i < group.getNodeCount(); i++) {
                List<OpenStackVolume> volumes = buildVolumes(template.getVolumeCount(), template.getVolumeSize());
                agents.add(new OpenStackInstance(template.getInstanceType(), volumes, singletonMap(CB_INSTANCE_GROUP_NAME, group.getGroupName())));
            }
        }
        return agents;
    }

    private List<OpenStackVolume> buildVolumes(int numDisk, int size) {
        List<OpenStackVolume> volumes = Lists.newArrayList();
        for (int i = 0; i < numDisk; i++) {
            volumes.add(new OpenStackVolume(MOUNT_PREFIX + (i + 1), DEVICE_PREFIX + DEVICE_CHAR[i], size));
        }
        return volumes;
    }

    private String formatUserData(String userData) {
        String[] lines = userData.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            // be aware of the OpenStack Heat template formatting
            sb.append("            " + lines[i] + "\n");
        }
        return sb.toString();
    }

}