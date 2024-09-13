package com.mobwal.walker.beautil.v1;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.Date;

public class wBeacon {
    public wBeacon() {
        this.date = new Date();
    }

    public wBeacon(@NonNull Beacon beacon) {
        this(beacon.getId1(), beacon.getIdentifiers().size() > 1 ? beacon.getId2() : null, beacon.getIdentifiers().size() > 2 ? beacon.getId3() : null);

        this.txPower = beacon.getTxPower();
        this.rssi = beacon.getRssi();
        this.distance = beacon.getDistance();

        this.beacon = beacon;
    }

    protected wBeacon(@NonNull Identifier id1, Identifier id2, Identifier id3) {
        this();

        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
    }

    public wBeacon(@NonNull String csvLine) {
        this();
        String[] data = csvLine.split(";");

        if(data.length > 0) {
            this.id1 = Identifier.parse(data[0]);
            this.id2 = data.length > 1 ? Identifier.parse(data[1]) : null;
            this.id3 = data.length > 2 ? Identifier.parse(data[2]) : null;
            this.x = data.length > 3 ? Double.valueOf(data[3]) : null;
            this.y = data.length > 4 ? Double.valueOf(data[4]) : null;
        }
    }

    public wBeacon(@NonNull JsonObject jsonObject) {
        this();

        this.id1 = jsonObject.has("id1") && !jsonObject.get("id1").isJsonNull() ? Identifier.parse(jsonObject.get("id1").getAsString()) : null;
        this.id2 = jsonObject.has("id2") && !jsonObject.get("id2").isJsonNull() ? Identifier.parse(jsonObject.get("id2").getAsString()) : null;
        this.id3 = jsonObject.has("id3") && !jsonObject.get("id3").isJsonNull() ? Identifier.parse(jsonObject.get("id3").getAsString()) : null;
        this.x = jsonObject.has("x") && !jsonObject.get("x").isJsonNull() ? jsonObject.get("x").getAsDouble() : null;
        this.y = jsonObject.has("y") && !jsonObject.get("y").isJsonNull() ? jsonObject.get("y").getAsDouble() : null;
    }

    public Beacon beacon;

    @Expose
    public Date date;

    @Expose
    public Identifier id1;
    @Expose
    public Identifier id2;
    @Expose
    public Identifier id3;
    /**
     * Важно отметить, что точность оценок расстояния приблизительна.
     * В целом, они, как правило, намного точнее на коротких расстояниях и имеют высокую степень вариации на больших расстояниях.
     * Когда библиотека сообщает, что маяк находится на расстоянии 5 метров, на самом деле это может быть где-то между 2 и 10 метрами.
     * На больших расстояниях оценка в 30 метров на самом деле может составлять от 20 до 40 метров.
     */
    @Expose
    public double distance;
    /**
     * Уровень мощности сигнала на расстоянии 1 метр
     */
    @Expose
    public int txPower;
    /**
     * Индикатор мощности принимаемого сигнала. Он основывается на измерении силы сигнала между двумя устройствами: чем ближе устройства друг к другу, тем сильнее сигнал.
     */
    @Expose
    public int rssi;

    /**
     * Положение по оси X
     */
    @Expose
    public Double x;

    /**
     * Положение по оси Y
     */
    @Expose
    public Double y;

    /**
     * Регион
     */
    public Region region;
}
