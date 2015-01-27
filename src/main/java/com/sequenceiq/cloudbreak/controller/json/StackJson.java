package com.sequenceiq.cloudbreak.controller.json;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.domain.CloudPlatform;
import com.sequenceiq.cloudbreak.domain.Status;
import com.sequenceiq.cloudbreak.domain.SubnetJson;

public class StackJson implements JsonEntity {

    private Long id;
    @Size(max = 40, min = 5, message = "The length of the name has to be in range of 5 to 40")
    @Pattern(regexp = "([a-z][-a-z0-9]*[a-z0-9])",
            message = "The name can only contain lowercase alphanumeric characters and hyphens and has start with an alphanumeric character")
    @NotNull
    private String name;
    private String region;
    private String owner;
    private String account;
    private boolean publicInAccount;
    private CloudPlatform cloudPlatform;
    @NotNull
    private Long credentialId;
    private Status status;
    private String ambariServerIp;
    @Size(max = 15, min = 5, message = "The length of the username has to be in range of 5 to 15")
    @Pattern(regexp = "([a-z][-a-z0-9]*[a-z0-9])",
            message = "The username can only contain lowercase alphanumeric characters and hyphens and has start with an alphanumeric character")
    @NotNull
    private String userName;
    @Size(max = 15, min = 5, message = "The length of the password has to be in range of 5 to 15")
    @Pattern(regexp = "([a-z][-a-z0-9]*[a-z0-9])",
            message = "The password can only contain lowercase alphanumeric characters and hyphens and has start with an alphanumeric character")
    @NotNull
    private String password;
    private String image;
    private String hash;
    private ClusterResponse cluster;
    private String statusReason;
    private List<InstanceGroupJson> instanceGroups = new ArrayList<>();
    private List<SubnetJson> allowedSubNets = new ArrayList<>();

    public StackJson() {
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<InstanceGroupJson> getInstanceGroups() {
        return instanceGroups;
    }

    public void setInstanceGroups(List<InstanceGroupJson> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

    @JsonProperty("cloudPlatform")
    public CloudPlatform getCloudPlatform() {
        return cloudPlatform;
    }

    @JsonIgnore
    public void setCloudPlatform(CloudPlatform cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public Long getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(Long credentialId) {
        this.credentialId = credentialId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<SubnetJson> getAllowedSubNets() {
        return allowedSubNets;
    }

    public void setAllowedSubNets(List<SubnetJson> allowedSubNets) {
        this.allowedSubNets = allowedSubNets;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonIgnore
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("statusReason")
    public String getStatusReason() {
        return statusReason;
    }

    @JsonIgnore
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    @JsonProperty("ambariServerIp")
    public String getAmbariServerIp() {
        return ambariServerIp;
    }

    @JsonIgnore
    public void setAmbariServerIp(String ambariServerIp) {
        this.ambariServerIp = ambariServerIp;
    }

    @JsonProperty("hash")
    public String getHash() {
        return hash;
    }

    @JsonIgnore
    public void setHash(String hash) {
        this.hash = hash;
    }

    @JsonProperty("cluster")
    public ClusterResponse getCluster() {
        return cluster;
    }

    @JsonIgnore
    public void setCluster(ClusterResponse cluster) {
        this.cluster = cluster;
    }

    @JsonProperty("owner")
    public String getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonProperty("account")
    public String getAccount() {
        return account;
    }

    @JsonIgnore
    public void setAccount(String account) {
        this.account = account;
    }

    @JsonProperty("public")
    public boolean isPublicInAccount() {
        return publicInAccount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonIgnore
    public void setPublicInAccount(boolean publicInAccount) {
        this.publicInAccount = publicInAccount;
    }
}
