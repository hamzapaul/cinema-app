package com.paulians.cinemaapp.controllers;

import com.paulians.cinemaapp.model.Cinema;
import com.paulians.cinemaapp.model.Seat;
import com.paulians.cinemaapp.model.TicketDetails;
import com.paulians.cinemaapp.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CinemaController {

    private final TicketService service;

    @Autowired
    public CinemaController(TicketService service) {
        this.service = service;
    }

    @GetMapping("/seats")
    public Cinema getAvailableSeats() {
        return service.getAvailableSeatsInCinema();
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseSeat(@RequestBody Seat seat) {
        return service.processPurchase(seat);
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnTicket(@RequestBody TicketDetails ticketDetails) {
        return service.processReturn(ticketDetails);
    }

    @PostMapping("/stats")
    public ResponseEntity<String> stats(@RequestParam(value = "password", required = false) String password) {
        return service.getStats(password);
    }

}
