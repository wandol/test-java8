package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class NearByVO {

    private String arsId;

    private String dist;

    private String gpsX;

    private String gpsY;

    private String posX;

    private String posY;

    private String stationId;

    private String stationNm;

    private String stationTp;
}
