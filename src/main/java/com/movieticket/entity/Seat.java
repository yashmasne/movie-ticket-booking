package com.movieticket.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"screen_id", "seat_code"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    @JsonIgnoreProperties({"seats", "cinema"})
    private Screen screen;

    @Column(name = "seat_row", nullable = false, length = 5)
    private String seatRow; // "A", "B", "C", etc.

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3, etc.

    @Column(name = "seat_code", nullable = false, length = 10)
    private String seatCode; // "A1", "A2", "D5", etc.

    @Column(nullable = false, length = 20)
    private String tier = "STANDARD"; // STANDARD, PREMIUM, VIP

    @Column(name = "price_multiplier", nullable = false)
    private Double priceMultiplier = 1.0;

    public Seat() {}

    public Seat(Screen screen, String seatRow, Integer seatNumber, String seatCode, String tier, Double priceMultiplier) {
        this.screen = screen;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.tier = tier;
        this.priceMultiplier = priceMultiplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(Double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }
}
