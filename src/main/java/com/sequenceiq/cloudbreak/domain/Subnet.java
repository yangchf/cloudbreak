package com.sequenceiq.cloudbreak.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Subnet implements ProvisionEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String subnet;
    @ManyToOne
    private Stack stack;

    public Subnet() {
    }

    public Subnet(String subnet) {
        this.subnet = subnet;
    }

    public Long getId() {
        return id;
    }

    public String getSubnet() {
        return subnet;
    }

    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }
}
