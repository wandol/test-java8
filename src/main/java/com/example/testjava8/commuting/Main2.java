package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusNodeVO;
import com.example.testjava8.commuting.bus.BusStationVO;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main2 {
    public static void main(String[] args) throws Exception {
        // http://apis.data.go.kr/6410000/busrouteservice/getBusRouteStationList?serviceKey=%2F1T27kausQ5i6QmZz2F97BDjYCtKt%2B2JxO7achdmwnishpW%2BbbacYh69htyNR%2FaHNbvZRO8m70YINbyaOMk8ww%3D%3D&routeId=200000085
        // csvs/METRO_GGD_NODE_INFO.csv
        CommUtils utils = new CommUtils();

        //  경기도 버스 정류장 가져오기
//        List<BusStationVO> ggdst = utils.readCsv(BusStationVO.class,"csvs/METRO-GGDB-ST-INFO.csv");
//
//        //  경기도 노드 가져오기.
//        List<BusNodeVO> list1 = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO1.csv");
//        List<BusNodeVO> list2 = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO2.csv");
//        List<BusNodeVO> list3 = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO3.csv");
//        List<BusNodeVO> list4 = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO4.csv");
//        List<BusNodeVO> result = new LinkedList<>();
//        result.addAll(list1);
//        result.addAll(list2);
//        result.addAll(list3);
//        result.addAll(list4);
//
//        result.forEach(v -> {
//            List<BusStationVO> temp = ggdst.stream().filter(vv -> vv.getSt_id().equals(v.getSt_id())).limit(1).collect(Collectors.toList());
//            if(temp.size() > 0){
//                v.setPos_y(temp.get(0).getPos_y());
//                v.setPos_x(temp.get(0).getPos_x());
//            }
//
//        });
//
//        CSVWriter writer = new CSVWriter(new FileWriter("d:/METRO-GGDB-NODE-INFO.csv"));
//        String[] cate = {"bus_route_id","bus_route_nm","ars_id","st_id","st_nm","gps_x","gps_y",
//                "pos_x","pos_y","seq","dist_next_st","time_next_st","trn_st_id","city_nm"};
//        writer.writeNext(cate);
//        result.forEach(v -> {
//            writer.writeNext(new String[]{v.getBus_route_id(),v.getBus_route_nm(),v.getArs_id(),v.getSt_id(),
//                    v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),v.getPos_y(),v.getSeq()
//                    ,v.getDist_next_st(),v.getTime_next_st(),v.getTrn_st_id(),v.getCity_nm()});
//        });
//        writer.close();

        List<BusStationVO>  real  = new LinkedList<>();
        List<BusNodeVO> yy = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO-REAL.csv");
        Map<String, List<BusNodeVO>> map = yy.stream().collect(Collectors.groupingBy(BusNodeVO::getSt_id));

        List<BusStationVO> tt = new LinkedList<>();
        map.forEach((k,v) -> {
            BusStationVO vo = new BusStationVO();
            BusNodeVO tempvo = v.get(0);
            vo.setArs_id(tempvo.getArs_id());
            vo.setSt_id(k);
            vo.setGps_x(tempvo.getGps_x());
            vo.setGps_y(tempvo.getGps_y());
            vo.setPos_x(tempvo.getPos_x());
            vo.setPos_y(tempvo.getPos_y());
            vo.setCity_nm(tempvo.getCity_nm());
            vo.setSt_nm(tempvo.getSt_nm());
            vo.setTransYn("");
            tt.add(vo);
        });

//          지하철 - 버스 정류장 연결 csv만들기.
        CSVWriter writer = new CSVWriter(new FileWriter("d:/METRO-GGDB-ST-INFO.csv"));
        String[] cate = {"ars_id","st_id","st_nm","gps_x","gps_y","pos_x","pos_y","trans_yn","city_nm"};
        writer.writeNext(cate);
        tt.forEach(v -> {
            writer.writeNext(new String[]{v.getArs_id(),v.getSt_id(),v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),
                    v.getPos_y(), v.getTransYn(), v.getCity_nm()});
        });
        writer.close();
    }
}
