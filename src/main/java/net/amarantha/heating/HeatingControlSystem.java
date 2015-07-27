package net.amarantha.heating;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.impl.MockHeatingController;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.utility.PropertyManager;
import net.amarantha.heating.webservice.HeatingResource;

import java.io.IOException;

public class HeatingControlSystem extends GuiceServletContextListener {

    // Application

    @Inject private HeatingService service;
    @Inject private HeatingResource resource;
    @Inject private PropertyManager props;

    public void startApplication() {

        service.startHeatingService();
        startWebService();

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

    public void startWebService() {
        String url = "http://" + props.getString("ip", "127.0.0.1") + ":" + props.getString("port", "8001") + "/";
        Injector injector = getInjector();
//                server = GrizzlyHttpServerFactory.createHttpServer(url, rc, ioc);
        ResourceConfig rc = new PackagesResourceConfig( "net.amarantha.heating.webservice" );
        IoCComponentProviderFactory ioc = new GuiceComponentProviderFactory( rc, injector );
//        ResourceConfig rc = new ResourceConfig().packages("net.amarantha.heating.webservice");
        try {
            GrizzlyServerFactory.createHttpServer(url, rc, ioc).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        serve( "/heating/*" ).with( GuiceContainer.class );
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        ResourceConfig rc = new PackagesResourceConfig("net.amarantha.heating");
                        for (Class<?> resource : rc.getClasses()) {
                            bind(resource);
                        }
                        serve("/heating/*").with(GuiceContainer.class);
                    }
                }
        );
    }

}
