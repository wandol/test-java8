package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class TempVO {

    @CsvBindByName(column = "city_code")
    private String city_code;

    @CsvBindByName(column = "city_nm")
    private String city_nm;
}
