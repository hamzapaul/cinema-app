package com.paulians.cinemaapp.repositories;

import com.paulians.cinemaapp.model.Cinema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CinemaRepository {

    private final Cinema cinema;

    @Autowired
    public CinemaRepository(Cinema cinema) {
        this.cinema = cinema;
    }

    public Cinema getCinema() {
        return cinema;
    }
}
