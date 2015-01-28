package com.sequenceiq.cloudbreak.domain;

public enum AwsVolumeType {

    Standard("standard"),
    Io1("io1"),
    Ephemeral("ephemeral"),
    Gp2("gp2");

    private String value;

    private AwsVolumeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
