package com.example.testjava8.commuting.bus;

import lombok.Data;

import java.io.Serializable;

/**
 *  버스 정류장 INFO
 */
@Data
public class BusStationVO implements Serializable {

    //  정류소 고유번호
    private String arsId;

    //  첫차 시간
    private String beginTm;

    //  노선명
    private String busRouteNm;

    //  노선 ID
    private String busRouteId;

    //  진행방향
    private String direction;

    //  X좌표 (WGS 84)
    private String gpsX;

    //  Y좌표 (WGS 84)
    private String gpsY;

    //  막차 시간
    private String lastTm;

    //  좌표X (GRS80)
    private String posX;

    //  좌표Y (GRS80)
    private String posY;

    //  노선 유형 (1:공항, 2:마을, 3:간선, 4:지선, 5:순환, 6:광역, 7:인천, 8:경기, 9:폐지, 0:공용)
    private String routeType;

    //  구간속도
    private String sectSpd;

    //  구간 ID
    private String section;

    //  순번
    private int seq;

    //  정류소 ID
    private String station;

    //  정류소 이름
    private String stationNm;

    //  정류소 고유번호
    private String stationNo;

    //  회차지 여부 (Y:회차, N:회차지아님)
    private String transYn;

    //  정류소간 거리
    private String fullSectDist;

    //  회차지 정류소ID
    private String trnstnid;

}
