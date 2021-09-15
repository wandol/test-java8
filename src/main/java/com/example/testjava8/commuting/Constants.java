package com.example.testjava8.commuting;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Constants {

    //  서울 버스 API URL
    public  static final String SEOUL_BUS_API_URL = "http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute?";

    //  서울 버스 API KEY
    public  static final String SEOUL_BUS_API_KEY = "%2F1T27kausQ5i6QmZz2F97BDjYCtKt%2B2JxO7achdmwnishpW%2BbbacYh69htyNR%2FaHNbvZRO8m70YINbyaOMk8ww%3D%3D";

    //  서울 버스 노선 정보 csv 경로
    public static final String SEOUL_BUS_ALL_NODE_INFO_CSV_PATH = "csvs/SEB-ROUTE-ID-LIST.csv";

    //  서울 버스 전체 노선에 대한 개별 경로
    public static final String SEOUL_BUS_ALL_NODE_PATH_INFO_CSV_PATH = "csvs/SEB-ALL-STATION.csv";

    //  서울 버스 정류장 정보 csv 경로
    public static final String SEOUL_BUS_ALL_ST_INFO_CSV_PATH = "csvs/SEB.csv";

    //  수도권 지하철 소요시간 정보 csv 경로
    public static final String METRO_TAKEMIN_INFO_CSV_PATH = "csvs/METRO-TAKEMIN.csv";

    //  수도권 지하철 환승 소요시간 정보 csv 경로
    public static final String METRO_TRANS_TAKEMIN_INFO_CSV_PATH = "csvs/METRO-TRANS.csv";

}
