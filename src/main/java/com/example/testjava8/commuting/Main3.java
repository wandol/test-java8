package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusNodeVO;
import com.example.testjava8.commuting.bus.BusStationVO;

import java.io.IOException;
import java.util.List;

public class Main3 {
    public static void main(String[] args) throws IOException {
        CommUtils utils = new CommUtils();
        List<BusNodeVO> list = utils.readCsv(BusNodeVO.class,"csvs/METRO-SEB-NODE-INFO.csv");
        list.forEach(System.out::println);
    }
}
