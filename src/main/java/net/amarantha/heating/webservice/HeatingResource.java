package net.amarantha.heating.webservice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.utility.PropertyManager;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;

@Singleton
@Path("heating")
public class HeatingResource {

    @Inject
    protected HeatingService service;

    @Inject
    protected PropertyManager props;


    @GET
    @Path("status")
    public Response getStatus() {
        return ok("Not Implemented");
    }

    @GET
    @Path("timer-events")
    public Response getTimerEvents() {
        return ok("Not Implemented");
    }

    @POST
    @Path("on")
    public Response switchHeatingOn() {
        service.switchHeating(ON);
        return ok();
    }

    @POST
    @Path("off")
    public Response switchHeatingOff() {
        service.switchHeating(OFF);
        return ok();
    }

    @POST
    @Path("override/on")
    public Response overrideOn() {
        service.setOverride(ON);
        return ok();
    }

    @POST
    @Path("override/off")
    public Response overrideOff() {
        service.setOverride(OFF);
        return ok();
    }

    @POST
    @Path("thermo/on")
    public Response thermoOn() {
        service.setThermo(ON);
        return ok();
    }

    @POST
    @Path("thermo/off")
    public Response thermoOff() {
        service.setThermo(OFF);
        return ok();
    }

    @POST
    @Path("timer/on")
    public Response timerOn() {
        service.setTimer(ON);
        return ok();
    }

    @POST
    @Path("timer/off")
    public Response timerOff() {
        service.setTimer(OFF);
        return ok();
    }

    @POST
    @Path("timer/remove")
    public Response removeTimerEvent(@QueryParam("id") String id) {
        service.removeTimerEvent(id);
        return ok();
    }

    @POST
    @Path("timer/add")
    public Response addTimerEvent(@QueryParam("type") String type, @QueryParam("time") String time) {
        String id = service.addTimerEvent("ON".equals(type) ? ON : OFF, time);
        return ok(id);
    }

    private Response ok() {
        return ok("Request Processed");
    }

    private Response ok(String content) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .entity(content)
                .build();
    }

}
