package com.example.testjava8.commuting.subway;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * @User : wspark
 * @Date : 2021-09-02
 */
@Data
public class SubwayTakeTIme {

    @CsvBindByName(column = "전철역코드")
    private String subwayCd;

    @CsvBindByName(column = "전철역명")
    private String subwayNm;

    @CsvBindByName(column = "전철명명(영문)")
    private String subwayEnNm;

    @CsvBindByName(column = "호선")
    private String subwayLine;

    @CsvBindByName(column = "외부코드")
    private String etcCd;

    @CsvBindByName(column = "역간소요분")
    private String takeMin;

    @CsvBindByName(column = "출발지전철역")
    private String sourceSubway;

    @CsvBindByName(column = "도착지전철역")
    private String targetSubway;
}
