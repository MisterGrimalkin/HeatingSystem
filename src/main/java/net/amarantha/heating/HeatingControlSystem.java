package net.amarantha.heating;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.impl.MockHeatingController;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.utility.PropertyManager;
import net.amarantha.heating.webservice.HeatingResource;

public class HeatingControlSystem {

    // Application

    @Inject private HeatingService service;
    @Inject private HeatingResource resource;
    @Inject private PropertyManager props;

    public void startApplication() {

        service.startHeatingService();
        resource.startWebService();

        while ( true ) { }

    }


    // Setup

    private static class Module extends AbstractModule {
        @Override protected void configure() {
            bind(HeatingController.class).to(MockHeatingController.class);
        }
    }

    public static void main(String[] args) {
        Guice.createInjector(new Module())
                .getInstance(HeatingControlSystem.class)
                        .startApplication();
    }

}
