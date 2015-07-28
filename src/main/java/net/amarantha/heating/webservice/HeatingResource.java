package net.amarantha.heating.webservice;

import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.utility.PropertyManager;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;

@Path("heating")
@Singleton
public class HeatingResource {

    protected static HeatingService service;

    @Inject
    protected PropertyManager props;

    @Inject
    public HeatingResource(HeatingService service) {
        HeatingResource.service = service;
    }

    public void startWebService() {
        ResourceConfig rc = new ResourceConfig().packages("net.amarantha.heating.webservice");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(props.getString("ip","127.0.0.1")), rc);
    }

    @GET
    @Path("status")
    public static Response getStatus() {
        // TODO: JSON
        return ok("Not Implemented");
    }

    @GET
    @Path("timer-events")
    public static Response getTimerEvents() {
        // TODO: JSON
        return ok("Not Implemented");
    }

    @POST
    @Path("on")
    public static Response switchHeatingOn() {
        service.switchHeating(ON);
        return ok("Command Processed");
    }

    @POST
    @Path("off")
    public static Response switchHeatingOff() {
        service.switchHeating(OFF);
        return ok("Command Processed");
    }

    @POST
    @Path("override/on")
    public static Response overrideOn() {
        service.setOverride(ON);
        return ok("Command Processed");
    }

    @POST
    @Path("override/off")
    public static Response overrideOff() {
        service.setOverride(OFF);
        return ok("Command Processed");
    }

    @POST
    @Path("thermo/on")
    public static Response thermoOn() {
        service.setThermo(ON);
        return ok("Command Processed");
    }

    @POST
    @Path("thermo/off")
    public static Response thermoOff() {
        service.setThermo(OFF);
        return ok("Command Processed");
    }

    @POST
    @Path("timer/on")
    public static Response timerOn() {
        service.setTimer(ON);
        return ok("Command Processed");
    }

    @POST
    @Path("timer/off")
    public static Response timerOff() {
        service.setTimer(OFF);
        return ok("Command Processed");
    }

    @POST
    @Path("timer/remove")
    public static Response removeTimerEvent(@QueryParam("id") String id) {
        service.removeTimerEvent(id);
        return ok("Command Processed");
    }

    @POST
    @Path("timer/add")
    public static Response addTimerEvent(@QueryParam("type") String type, @QueryParam("time") String time) {
        String id = service.addTimerEvent("ON".equals(type)?ON:OFF,time);
        return ok(id);
    }

    private static Response ok(String content) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .entity(content)
                .build();
    }

}
