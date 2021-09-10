package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 *  버스 노선 리스트.
 */
@Data
public class BusRouteVO {

    //  노선 명
    @CsvBindByName(column = "ROUTE_NM")
    private String ROUTE_NM;

    //  노선 ID
    @CsvBindByName(column = "ROUTE_ID")
    private String ROUTE_ID;
}
