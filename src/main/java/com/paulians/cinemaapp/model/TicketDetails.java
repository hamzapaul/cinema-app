package com.paulians.cinemaapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class TicketDetails {

    private UUID token;
    private Seat ticket;

    public TicketDetails(Seat ticket) {
        this.token = UUID.randomUUID();
        this.ticket = ticket;
    }

    public TicketDetails(@JsonProperty("token") UUID token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketDetails that = (TicketDetails) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    //Getters and Setters
    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public Seat getTicket() {
        return ticket;
    }

    public void setTicket(Seat ticket) {
        this.ticket = ticket;
    }
}
