package com.kumuluz.ee.samples.tutorial.customers.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestProperties {

    @ConfigValue(value = "external-services.order-service.enabled", watch = true)
    private boolean orderServiceEnabled;

    public boolean isOrderServiceEnabled() {
        return orderServiceEnabled;
    }

    public void setOrderServiceEnabled(boolean orderServiceEnabled) {
        this.orderServiceEnabled = orderServiceEnabled;
    }
}
