package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Temp3VO {

    //  버스 노선 id
    @CsvBindByName(column = "adm_nm")
    private String adm_nm;

    //  버스 노선 명
    @CsvBindByName(column = "adm_cd")
    private String adm_cd;

    //  정류소 고유번호
    @CsvBindByName(column = "node_id")
    private String node_id;

    //  정류소 고유번호
    @CsvBindByName(column = "node_no")
    private String node_no;

    //  정류소 고유번호
    @CsvBindByName(column = "centerYn")
    private String centerYn;

    //  정류소 고유번호
    @CsvBindByName(column = "districtCd")
    private String districtCd;

    //  정류소 고유번호
    @CsvBindByName(column = "mobileNo")
    private String mobileNo;

    //  정류소 고유번호
    @CsvBindByName(column = "regionName")
    private String regionName;

    //  정류소 고유번호
    @CsvBindByName(column = "stationId")
    private String stationId;

    //  정류소 고유번호
    @CsvBindByName(column = "stationName")
    private String stationName;
    //  정류소 고유번호
    @CsvBindByName(column = "x")
    private String x;
    //  정류소 고유번호
    @CsvBindByName(column = "y")
    private String y;
    //  정류소 고유번호
    @CsvBindByName(column = "stationSeq")
    private String stationSeq;
    //  정류소 고유번호
    @CsvBindByName(column = "turnYn")
    private String turnYn;

}
