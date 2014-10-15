package com.sequenceiq.cloudbreak.service.stack.connector.gcc;

public enum GccZone {

    US_CENTRAL1_A("us-central1-a", "us-central1"),
    US_CENTRAL1_B("us-central1-b", "us-central1"),
    US_CENTRAL1_F("us-central1-f", "us-central1"),
    EUROPE_WEST1_A("europe-west1-a", "europe-west1"),
    EUROPE_WEST1_B("europe-west1-b", "europe-west1"),
    ASIA_EAST1_A("asia-east1-a", "asia-east1"),
    ASIA_EAST1_B("asia-east1-b", "asia-east1");

    private final String value;
    private final String region;

    private GccZone(String value, String region) {
        this.value = value;
        this.region = region;
    }

    public String getValue() {
        return value;
    }

    public String getRegion() {
        return region;
    }
}
