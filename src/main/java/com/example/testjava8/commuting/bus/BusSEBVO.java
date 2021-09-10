package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 *  서울 버스 정류장 VO
 */
@Data
public class BusSEBVO {

    //  정류장 번호. CITY_ABB+NODE_REAL_ID
    @CsvBindByName(column = "NODE_ID")
    private String NODE_ID;

    //  정류장 번호
    @CsvBindByName(column = "NODE_REAL_ID")
    private String NODE_REAL_ID;

    //  정류장 명
    @CsvBindByName(column = "NODE_NM")
    private String NODE_NM;

    //  위도
    @CsvBindByName(column = "GPS_LATI")
    private String GPS_LATI;

    //  경도
    @CsvBindByName(column = "GPS_LONG")
    private String GPS_LONG;

    //  데이터 수집시간.
    @CsvBindByName(column = "COLLECTD_TIME")
    private String COLLECTD_TIME;

    //
    @CsvBindByName(column = "NODE_MOBILE_ID")
    private String NODE_MOBILE_ID;

    //  도시 코드
    @CsvBindByName(column = "CITY_CODE")
    private String CITY_CODE;

    //  도시 코드명
    @CsvBindByName(column = "CITY_ABB")
    private String CITY_ABB;

    //  도시 명
    @CsvBindByName(column = "CITY_NAME")
    private String CITY_NAME;

}
