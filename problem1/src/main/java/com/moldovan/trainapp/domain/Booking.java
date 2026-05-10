package com.moldovan.trainapp.domain;

public class Booking {

    private final String id;
    private final String customerEmail;
    private final String trainId;
    private final String fromStationId;
    private final String toStationId;
    private final int seatCount;

    public Booking(String id, String customerEmail, String trainId,
                   String fromStationId, String toStationId, int seatCount) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.trainId = trainId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.seatCount = seatCount;
    }

    public String getId() { return id; }
    public String getCustomerEmail() { return customerEmail; }
    public String getTrainId() { return trainId; }
    public String getFromStationId() { return fromStationId; }
    public String getToStationId() { return toStationId; }
    public int getSeatCount() { return seatCount; }

    @Override
    public String toString() {
        return "Booking{" + id + " email=" + customerEmail + " train=" + trainId
                + " " + fromStationId + "->" + toStationId + " seats=" + seatCount + "}";
    }
}
