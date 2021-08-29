package com.example.testjava8;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Subway {

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

    @CsvBindByName(column = "걸린시간")
    private String takeMin;
}
