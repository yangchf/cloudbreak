package com.sequenceiq.cloudbreak.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sequenceiq.ambari.client.AmbariClient;
import com.sequenceiq.ambari.client.InvalidBlueprintException;
import com.sequenceiq.cloudbreak.controller.BadRequestException;
import com.sequenceiq.cloudbreak.controller.json.BlueprintJson;
import com.sequenceiq.cloudbreak.controller.json.JsonHelper;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;

@Component
public class BlueprintConverter extends AbstractConverter<BlueprintJson, Blueprint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlueprintConverter.class);

    @Autowired
    private JsonHelper jsonHelper;

    @Override
    public BlueprintJson convert(Blueprint entity) {
        MDCBuilder.buildMdcContext(entity);
        BlueprintJson blueprintJson = new BlueprintJson();
        blueprintJson.setId(String.valueOf(entity.getId()));
        blueprintJson.setBlueprintName(entity.getBlueprintName());
        blueprintJson.setName(entity.getName());
        blueprintJson.setPublicInAccount(entity.isPublicInAccount());
        blueprintJson.setDescription(entity.getDescription() == null ? "" : entity.getDescription());
        blueprintJson.setHostGroupCount(entity.getHostGroupCount());
        try {
            blueprintJson.setAmbariBlueprint(jsonHelper.createJsonFromString(entity.getBlueprintText()));
        } catch (Exception e) {
            LOGGER.error("Blueprint cannot be converted to JSON.", e);
            blueprintJson.setAmbariBlueprint(new TextNode(e.getMessage()));
        }
        return blueprintJson;
    }

    @Override
    public Blueprint convert(BlueprintJson json) {
        Blueprint blueprint = new Blueprint();
        if (json.getUrl() != null && !json.getUrl().isEmpty()) {
            String sourceUrl = json.getUrl().trim();
            try {
                String urlText = readUrl(sourceUrl);
                jsonHelper.createJsonFromString(urlText);
                blueprint.setBlueprintText(urlText);
            } catch (Exception e) {
                throw new BadRequestException("Cannot download ambari blueprint from: " + sourceUrl, e);
            }
        } else {
            blueprint.setBlueprintText(json.getAmbariBlueprint());
        }
        validateBlueprint(blueprint.getBlueprintText());
        blueprint.setName(json.getName());
        blueprint.setDescription(json.getDescription());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(blueprint.getBlueprintText());
            blueprint.setBlueprintName(getBlueprintName(root));
            blueprint.setHostGroupCount(countHostGroups(root));
        } catch (IOException e) {
            throw new BadRequestException("Invalid Blueprint: Failed to parse JSON.", e);
        }

        return blueprint;
    }

    public Blueprint convert(BlueprintJson json, boolean publicInAccount) {
        Blueprint blueprint = convert(json);
        blueprint.setPublicInAccount(publicInAccount);
        return blueprint;
    }

    public Blueprint convert(String name, String blueprintText, boolean publicInAccount) {
        Blueprint blueprint = new Blueprint();
        blueprint.setName(name);
        blueprint.setBlueprintText(blueprintText);
        blueprint.setPublicInAccount(publicInAccount);
        validateBlueprint(blueprint.getBlueprintText());
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(blueprint.getBlueprintText());
            blueprint.setBlueprintName(getBlueprintName(root));
            blueprint.setHostGroupCount(countHostGroups(root));
        } catch (IOException e) {
            throw new BadRequestException("Invalid Blueprint: Failed to parse JSON.", e);
        }

        return blueprint;
    }

    private String getBlueprintName(JsonNode root) {
        return root.get("Blueprints").get("blueprint_name").asText();
    }

    private int countHostGroups(JsonNode root) {
        int hostGroupCount = 0;
        Iterator<JsonNode> hostGroups = root.get("host_groups").elements();
        while (hostGroups.hasNext()) {
            hostGroups.next();
            hostGroupCount++;
        }
        return hostGroupCount;
    }

    private String readUrl(String url) throws IOException {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        String str;
        StringBuffer sb = new StringBuffer();
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return sb.toString();
    }

    private void validateBlueprint(String blueprintText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(blueprintText);
            hasBlueprintInBlueprint(root);
            hasBlueprintNameInBlueprint(root);
            hasHostGroupInBlueprint(root);
            new AmbariClient().validateBlueprint(blueprintText);
        } catch (InvalidBlueprintException e) {
            throw new BadRequestException("Invalid Blueprint: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new BadRequestException("Invalid Blueprint: Failed to parse JSON.", e);
        }
    }

    private void hasHostGroupInBlueprint(JsonNode root) {
        if (root.path("host_groups").isMissingNode() || !root.path("host_groups").isArray()) {
            throw new BadRequestException("Invalid blueprint: 'host_groups' node is missing from JSON or is not an array.");
        }
    }

    private void hasBlueprintNameInBlueprint(JsonNode root) {
        if (root.path("Blueprints").path("blueprint_name").isMissingNode()) {
            throw new BadRequestException("Invalid blueprint: 'blueprint_name' under 'Blueprints' is missing from JSON.");
        }
    }

    private void hasBlueprintInBlueprint(JsonNode root) {
        if (root.path("Blueprints").isMissingNode()) {
            throw new BadRequestException("Invalid blueprint: 'Blueprints' node is missing from JSON.");
        }
    }
}
