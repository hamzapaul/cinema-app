package com.paulians.cinemaapp.repositories;

import com.paulians.cinemaapp.model.TicketDetails;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketRepository {

    private List<TicketDetails> ticketDetailsList;

    public TicketRepository() {
        this.ticketDetailsList = new ArrayList<>();
    }

    public List<TicketDetails> getTicketDetailsList() {
        return ticketDetailsList;
    }

    public void setTicketDetailsList(List<TicketDetails> ticketDetailsList) {
        this.ticketDetailsList = ticketDetailsList;
    }
}
