package com.movieticket.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @JsonBackReference
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name; // e.g. "Screen 1 - IMAX"

    @Column(nullable = false, length = 50)
    private String screenType = "Standard 2D"; // IMAX 3D, 4DX, Dolby Atmos, Standard 2D

    @Column(nullable = false)
    private Integer totalRows = 8;

    @Column(nullable = false)
    private Integer totalCols = 10;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    public Screen() {}

    public Screen(Cinema cinema, String name, String screenType, Integer totalRows, Integer totalCols) {
        this.cinema = cinema;
        this.name = name;
        this.screenType = screenType;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalCols() {
        return totalCols;
    }

    public void setTotalCols(Integer totalCols) {
        this.totalCols = totalCols;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
