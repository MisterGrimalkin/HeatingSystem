package net.amarantha.heating;

import com.google.inject.Guice;
import com.google.inject.Inject;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.webservice.HeatingResource;

public class Application {

    @Inject private HeatingService service;
    @Inject private HeatingResource resource;

    public void startApplication() {

        service.startHeatingService();
        resource.startWebService();

        while ( true ) { }

    }

    public static void main(String[] args) {
        Guice.createInjector(new ApplicationModule())
                .getInstance(Application.class)
                        .startApplication();
    }

}
