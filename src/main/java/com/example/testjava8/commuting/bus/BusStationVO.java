package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.io.Serializable;

/**
 *  버스 정류장 INFO
 */
@Data
public class BusStationVO implements Serializable {

    //  정류소 고유번호
    @CsvBindByName(column = "ars_id")
    private String ars_id;

    //  정류소 고유번호
    @CsvBindByName(column = "st_id")
    private String st_id;

    //	정류소 이름.
    @CsvBindByName(column = "st_nm")
    private String st_nm;

    //  X좌표 (WGS 84)
    @CsvBindByName(column = "gps_x")
    private String gps_x;

    //  Y좌표 (WGS 84)
    @CsvBindByName(column = "gps_y")
    private String gps_y;

    //  좌표X (GRS80)
    @CsvBindByName(column = "pos_x")
    private String pos_x;

    //  좌표Y (GRS80)
    @CsvBindByName(column = "pos_y")
    private String pos_y;

    //  회차지 여부 (Y:회차, N:회차지아님, :알수없음.)
    @CsvBindByName(column = "trans_yn")
    private String transYn;

    //  도시명
    @CsvBindByName(column = "city_nm")
    private String city_nm;

}
