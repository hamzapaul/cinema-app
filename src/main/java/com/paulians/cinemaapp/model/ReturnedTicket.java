package com.paulians.cinemaapp.model;

public class ReturnedTicket {

    private Seat returned_ticket;

    public ReturnedTicket(Seat returnedTicket) {
        this.returned_ticket = returnedTicket;
    }

    public Seat getReturned_ticket() {
        return returned_ticket;
    }

    public void setReturned_ticket(Seat returned_ticket) {
        this.returned_ticket = returned_ticket;
    }
}
