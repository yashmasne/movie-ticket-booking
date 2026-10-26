package com.movieticket.dto;

public class SeatDTO {
    private Long id;
    private String seatRow;
    private Integer seatNumber;
    private String seatCode;
    private String tier;
    private Double priceMultiplier;
    private Double calculatedPrice;
    private boolean booked;

    public SeatDTO() {}

    public SeatDTO(Long id, String seatRow, Integer seatNumber, String seatCode, String tier, Double priceMultiplier, Double calculatedPrice, boolean booked) {
        this.id = id;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.tier = tier;
        this.priceMultiplier = priceMultiplier;
        this.calculatedPrice = calculatedPrice;
        this.booked = booked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(Double calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
