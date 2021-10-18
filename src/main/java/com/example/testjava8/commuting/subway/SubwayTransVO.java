package com.example.testjava8.commuting.subway;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * @User : wspark
 * @Date : 2021-09-02
 */
@Data
public class SubwayTransVO {

    @CsvBindByName(column = "연번")
    private String id;

    @CsvBindByName(column = "호선")
    private String subwaySourceLineNm;

    @CsvBindByName(column = "영호선")
    private String subwaySourceLineEnNm;

    @CsvBindByName(column = "환승역명")
    private String subwayNm;

    @CsvBindByName(column = "환승노선")
    private String subwayTargetLineNm;

    @CsvBindByName(column = "환승영노선")
    private String subwayTargetLineEnNm;

    @CsvBindByName(column = "환승거리")
    private String transDis;

    @CsvBindByName(column = "환승소요시간")
    private String takeSec;

    @CsvBindByName(column = "역간소요분")
    private String takeMin;

}
