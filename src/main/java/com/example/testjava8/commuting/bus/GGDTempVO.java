package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.io.Serializable;

/**
 *  버스 정류장 INFO
 */
@Data
public class GGDTempVO implements Serializable {

    //  버스 노선 id
    @CsvBindByName(column = "순번")
    private String idx;

    //  버스 노선 명
    @CsvBindByName(column = "ROUTE_ID")
    private String route_id;

    //  정류소 고유번호
    @CsvBindByName(column = "ROUTE_NM")
    private String route_nm;

    //  정류소 고유번호
    @CsvBindByName(column = "STA_ORDER")
    private String seq;

    //	정류소 이름.
    @CsvBindByName(column = "STATION_NM")
    private String st_nm;

    //  X좌표 (WGS 84)
    @CsvBindByName(column = "STATION_ID")
    private String st_id;

    //  Y좌표 (WGS 84)
    @CsvBindByName(column = "MOBILE_NO")
    private String ars_id;

}
