package com.kumuluz.ee.samples.tutorial.orders.api.v1.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

@Health
@ApplicationScoped
public class SampleHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {

        if (true){
            return HealthCheckResponse.named(SampleHealthCheck.class.getName()).up().build();
        }else{
            return HealthCheckResponse.named(SampleHealthCheck.class.getName()).down().build();
        }


    }
}
