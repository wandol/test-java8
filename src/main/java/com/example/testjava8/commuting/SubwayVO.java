package com.example.testjava8.commuting;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * @User : wspark
 * @Date : 2021-08-31
 */
@Data
public class SubwayVO {

    @CsvBindByName(column = "역번호")
    private String subwayCd;

    @CsvBindByName(column = "역사명")
    private String subwayNm;

    @CsvBindByName(column = "역문역사명")
    private String subwayEnNm;

    @CsvBindByName(column = "노선명")
    private String subwayLine;

    @CsvBindByName(column = "노선번호")
    private String subwayLineCd;

    @CsvBindByName(column = "환승역구분")
    private String transGubun;

    @CsvBindByName(column = "환승노선번호")
    private String transNo;

    @CsvBindByName(column = "환승노선명")
    private String transNm;

    @CsvBindByName(column = "역위도")
    private String yCon;

    @CsvBindByName(column = "역경도")
    private String xCon;

    @CsvBindByName(column = "출발지전철역")
    private String sourceSubway;

    @CsvBindByName(column = "도착지전철역")
    private String targetSubway;

    @CsvBindByName(column = "역간소요분")
    private String takeMin;

    @CsvBindByName(column = "운영기관명")
    private String operationNm;

    @CsvBindByName(column = "kcode")
    private int kcode;

    @CsvBindByName(column = "tcode")
    private int tcode;

    @CsvBindByName(column = "mcode")
    private int mcode;

    @CsvBindByName(column = "bcode")
    private int bcode;

    @CsvBindByName(column = "code")
    private int code;

    @CsvBindByName(column = "sriSubwayName")
    private String sriSubwayName;

}
