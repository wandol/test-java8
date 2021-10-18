package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.io.Serializable;

/**
 *  버스 정류장 INFO
 */
@Data
public class BusNodeVO implements Serializable {

    //  버스 노선 id
    @CsvBindByName(column = "bus_route_id")
    private String bus_route_id;

    //  버스 노선 명
    @CsvBindByName(column = "bus_route_nm")
    private String bus_route_nm;

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

    //  노선 순번
    @CsvBindByName(column = "seq")
    private String seq;

    //  다음정류장과 거리(m)
    @CsvBindByName(column = "dist_next_st")
    private String dist_next_st;

    //  다음정류장과 거리(m)
    @CsvBindByName(column = "time_next_st")
    private String time_next_st;

    //  회차 정류장 id
    @CsvBindByName(column = "trn_st_id")
    private String trn_st_id;

    //  도시명
    @CsvBindByName(column = "city_nm")
    private String city_nm;

}
