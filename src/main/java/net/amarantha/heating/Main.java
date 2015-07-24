package net.amarantha.heating;

import net.amarantha.heating.hardware.impl.RaspPiHeatingController;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.webservice.HeatingResource;

import static net.amarantha.heating.service.HeatingService.withController;

public class Main {

    public static void main(String[] args) {

        HeatingService service =
                withController(new RaspPiHeatingController())
//                .loadStateFromConfig()
                .startService();

        HeatingResource.startWebService(service);

        while ( true ) { }

    }

}
