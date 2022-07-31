package com.paulians.cinemaapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulians.cinemaapp.model.*;
import com.paulians.cinemaapp.repositories.CinemaRepository;
import com.paulians.cinemaapp.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {

    private final CinemaRepository cinemaRepository;
    private final TicketRepository ticketRepository;
    private final BusinessStatistics businessStatistics;

    @Autowired
    public TicketService(CinemaRepository repository, TicketRepository ticketRepository, BusinessStatistics businessStatistics) {
        this.cinemaRepository = repository;
        this.ticketRepository = ticketRepository;
        this.businessStatistics = businessStatistics;
    }

    public ResponseEntity<String> processReturn(TicketDetails ticketDetails) {
        Optional<Seat> seat = getAvailability(ticketDetails);

        ResponseEntity response = null;
        if (seat.isEmpty()) {
            response = new ResponseEntity(Map.of("error", "Wrong token!"), HttpStatus.BAD_REQUEST);
        } else {
            Optional<Seat> returnedSeat = cinemaRepository.getCinema().getSeats().stream()
                    .filter(s -> s.equals(seat.get()))
                    .findFirst();

            if (returnedSeat.isPresent()) {
                makeSeatAvailable(returnedSeat.get());
                ReturnedTicket returnedTicket = new ReturnedTicket(returnedSeat.get());
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    response = new ResponseEntity<>(objectMapper.writeValueAsString(returnedTicket), HttpStatus.OK);
                    refundUpdate(returnedSeat.get());
                } catch (JsonProcessingException e) {
                    response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } else {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return response;
    }

    private Optional<Seat> getAvailability(TicketDetails ticketDetails) {
        Optional<TicketDetails> detailsOptional = ticketRepository.getTicketDetailsList().stream()
                .filter(td -> td.equals(ticketDetails)) //if token is same
                .findFirst();

        if (detailsOptional.isEmpty()) { //token is expired
            return Optional.empty();
        } else {
            return Optional.of(detailsOptional.get().getTicket());
        }
    }

    public Cinema getAvailableSeatsInCinema() {
        return cinemaRepository.getCinema();
    }

    public ResponseEntity<String> processPurchase(Seat seat) {

        ResponseEntity<String> response = getAvailability(seat);

        if (response.getStatusCode().is2xxSuccessful()) {
            makeSeatNotAvailable(seat);
        }

        return response;
    }

    private ResponseEntity<String> getAvailability(Seat seat) {
        Optional<Seat> seatOpt = cinemaRepository.getCinema().getSeats().stream()
                .filter(s -> s.equals(seat))
                .findFirst();

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity seatInfo = null;

        if (seatOpt.isEmpty()) {
            seatInfo = new ResponseEntity(Map.of("error", "The number of a row or a column is out of bounds!"), HttpStatus.BAD_REQUEST);
        } else if (!seatOpt.get().isAvailable()) {
            seatInfo = new ResponseEntity(Map.of("error", "The ticket has been already purchased!"), HttpStatus.BAD_REQUEST);
        } else {
            try {
                TicketDetails ticketDetails = new TicketDetails(seatOpt.get());
                ticketRepository.getTicketDetailsList().add(ticketDetails);
                seatInfo = new ResponseEntity(objectMapper.writeValueAsString(ticketDetails), HttpStatus.OK);
                purchaseUpdate(seatOpt.get());
            } catch (JsonProcessingException e) {
                seatInfo = new ResponseEntity(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return seatInfo;
    }

    //purchase ticket
    private synchronized void makeSeatNotAvailable(Seat existingSeat) {
        cinemaRepository.getCinema().getAvailableSeats().stream()
                .filter(s -> s.equals(existingSeat))
                .forEach(s -> s.setAvailable(false));
    }

    private synchronized void makeSeatAvailable(Seat existingSeat) {
        cinemaRepository.getCinema().getSeats().stream()
                .filter(s -> s.equals(existingSeat))
                .forEach(s -> s.setAvailable(true));
    }

    public ResponseEntity<String> getStats(String password) {
        ResponseEntity response = null;
        if (password == null) {
            response = new ResponseEntity(Map.of("error", "The password is wrong!"), HttpStatus.UNAUTHORIZED);
        } else if (password.compareTo("super_secret") == 0) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                response = new ResponseEntity(objectMapper.writeValueAsString(businessStatistics), HttpStatus.OK);
            } catch (JsonProcessingException e) {
                response = new ResponseEntity(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response = new ResponseEntity(Map.of("error", "The password is wrong!"), HttpStatus.UNAUTHORIZED);
        }

        return response;
    }

    private void purchaseUpdate(Seat seat) {
        businessStatistics.setCurrent_income(businessStatistics.getCurrent_income() + seat.getPrice());
        businessStatistics.setNumber_of_purchased_tickets(businessStatistics.getNumber_of_purchased_tickets() + 1);
        businessStatistics.setNumber_of_available_seats(businessStatistics.getNumber_of_available_seats() - 1);
    }

    private void refundUpdate(Seat seat) {
        businessStatistics.setCurrent_income(businessStatistics.getCurrent_income() - seat.getPrice());
        businessStatistics.setNumber_of_purchased_tickets(businessStatistics.getNumber_of_purchased_tickets() - 1);
        businessStatistics.setNumber_of_available_seats(businessStatistics.getNumber_of_available_seats() + 1);
    }
}
