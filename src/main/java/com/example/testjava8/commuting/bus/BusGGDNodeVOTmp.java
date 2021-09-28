package com.example.testjava8.commuting.bus;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class BusGGDNodeVOTmp {

    @CsvBindByName(column = "adm_nm")
    private String adm_nm;

    @CsvBindByName(column = "node_id")
    private String node_id;

    @CsvBindByName(column = "node_no")
    private String node_no;

    @CsvBindByName(column = "adm_cd")
    private String adm_cd;
}
