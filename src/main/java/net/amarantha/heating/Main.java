package net.amarantha.heating;

import net.amarantha.heating.hardware.impl.RaspPiHeatingController;
import net.amarantha.heating.service.HeatingService;
import net.amarantha.heating.webservice.HeatingResource;

public class Main {

    public static void main(String[] args) {

        HeatingResource.startWebService(
                HeatingService.withController(new RaspPiHeatingController())
                        .startService()
        );

        while ( true ) { }

    }

}
