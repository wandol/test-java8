package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.*;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TempMain {
    @SneakyThrows
    public static void main(String[] args) {
        CommUtils utils = new CommUtils();


        // 경기도 버스 csv 만들기.
        List<GGDTempVO> allGdd = new LinkedList<>();
        List<BusStationVO> gddSt = utils.readCsv(BusStationVO.class, "csvs/METRO-GGDB-ST-INFO.csv");

        List<GGDTempVO> gdd1 = utils.readCsv(GGDTempVO.class,"csvs/GGD_RouteStationInfo_M1.csv");
        List<GGDTempVO> gdd2 = utils.readCsv(GGDTempVO.class,"csvs/GGD_RouteStationInfo_M2.csv");
        List<GGDTempVO> gdd3 = utils.readCsv(GGDTempVO.class,"csvs/GGD_RouteStationInfo_M3.csv");

        allGdd.addAll(gdd1);
        allGdd.addAll(gdd2);
        allGdd.addAll(gdd3);

        List<BusNodeVO> gddStNew = new LinkedList<>();
        Set<String> notIn = new HashSet<>();
        allGdd.forEach(v -> {
            List<BusStationVO> temp = gddSt.stream().filter(ii -> ii.getSt_id().equals(v.getSt_id())).collect(Collectors.toList());
            try{
                if(temp.size() == 1){
                    BusNodeVO vo = new BusNodeVO();
                    vo.setBus_route_id(v.getRoute_id());
                    vo.setBus_route_nm(v.getRoute_nm());
                    vo.setArs_id(v.getArs_id());
                    vo.setSt_id(v.getSt_id());
                    vo.setSt_nm(v.getSt_nm());
                    vo.setGps_x(temp.get(0).getGps_x());
                    vo.setGps_y(temp.get(0).getGps_y());
                    vo.setPos_x(temp.get(0).getPos_x());
                    vo.setPos_y(temp.get(0).getPos_y());
                    vo.setSeq(v.getSeq());
                    vo.setDist_next_st("");
                    vo.setTrn_st_id("");
                    gddStNew.add(vo);
                }else{
                    BusNodeVO vo = new BusNodeVO();
                    vo.setBus_route_id(v.getRoute_id());
                    vo.setBus_route_nm(v.getRoute_nm());
                    vo.setArs_id(v.getArs_id());
                    vo.setSt_id(v.getSt_id());
                    vo.setSt_nm(v.getSt_nm());
                    vo.setGps_x("");
                    vo.setGps_y("");
                    vo.setPos_x("");
                    vo.setPos_y("");
                    vo.setSeq(v.getSeq());
                    vo.setDist_next_st("");
                    vo.setTrn_st_id("");
                    gddStNew.add(vo);
                }
            }catch(Exception e){
                e.printStackTrace();
                notIn.add(v.getSt_id());
            }
        });

        gddStNew.forEach(System.out::println);
        System.out.println("======================================");
        notIn.forEach(System.out::println);

        CSVWriter writer = new CSVWriter(new FileWriter("d:/METRO-GGDB-NODE-INFO.csv"));
        String[] cate = {"bus_route_id","bus_route_nm","ars_id","st_id","st_nm","gps_x","gps_y",
                "pos_x","pos_y","seq","dist_next_st","trn_st_id"};
        writer.writeNext(cate);
        gddStNew.forEach(v -> {
            writer.writeNext(new String[]{v.getBus_route_id(),v.getBus_route_nm(),v.getArs_id(),v.getSt_id(),
                    v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),v.getPos_y(),v.getSeq()
                    ,v.getDist_next_st(),v.getTrn_st_id()});
        });
        writer.close();



        //  경기도 버스 좌표와 노선 경로 구해 csv
//        List<BusGGDNodeVOTmp> list = utils.readCsv(BusGGDNodeVOTmp.class, Constants.GyongGido_BUS_ALL_NODE_INFO_CSV_PATH);
//        LinkedList<BusGGDNodeVO> allBusStByNode = new LinkedList<>();
//        for (BusGGDNodeVOTmp v : list) {
//            StringBuffer url = new StringBuffer();
//            url.append("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteAcctoThrghSttnList?");
//            url.append("serviceKey=" + Constants.SEOUL_BUS_API_KEY);
//            url.append("&numOfRows=1000&pageNo=1&");
//            url.append("cityCode=" + v.getAdm_cd() + "&routeId=GGB" + v.getNode_id());
//            System.out.println(url.toString());
//            LinkedList<BusGGDNodeVO> temp = utils.getListVO(BusGGDNodeVO.class, url.toString(), "item");
//            if(temp == null) {
//                utils.writeDataToCsv(allBusStByNode,
//                        "/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/csvs/METRO_GGD_NODE_INFO_TEMP.csv");
//                throw new Exception("만료된 API");
//            }else{
//                if(temp.size() == 0){
//                    FileWriter fw, fw_append; // FileWriter 선언
//                    try {
//                        fw = new FileWriter("/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/emptyBus.txt", true); // 파일이 있을경우 덮어쓰기
//                        fw.write(v.getNode_id()+"\n");
//                        fw.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//            System.out.println(temp.toString());
//            allBusStByNode.addAll(temp);
//        }
//        utils.writeDataToCsv(allBusStByNode,
//                "/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/csvs/METRO_GGD_NODE_INFO_TEMP.csv");

        //  경기도 버스 좌표로 naver api를 이용하여 각 정류소간의 소요시간 산정.
        //  https://map.naver.com/v5/api/transit/directions/point-to-point?start=126.9328667,37.3727833,name=곡란초등학교&goal=126.9316167,37.3751167,name=개나리후문&mode=TIME&lang=ko
//        List<BusGGDNodeVO> gBusNodeList = utils.readCsv(BusGGDNodeVO.class,
//                "csvs/METRO_GGD_NODE_INFO_TEMP.csv");

//        for (int i = 0; i < 9; i++) {
//            if(i < 8){
//                StringBuffer url = new StringBuffer();
//                url.append("https://map.naver.com/v5/api/transit/directions/point-to-point?");
//                url.append("start="+ URLEncoder.encode(gBusNodeList.get(i).getGpslong()+","+gBusNodeList.get(i).getGpslati()+",name=" + gBusNodeList.get(i).getNodenm(),"utf-8"));
//                url.append("&goal="+ URLEncoder.encode(gBusNodeList.get(i+1).getGpslong()+","+gBusNodeList.get(i+1).getGpslati()+",name=" + gBusNodeList.get(i+1).getNodenm(),"utf-8"));
//                url.append("&mode=TIME&lang=ko");
//                System.out.println(url.toString());
//                String takeMin = utils.getTakeMinData(url.toString(), "duration");
//            }
//        }
//        gBusNodeList.forEach(v -> {
//
//        });
    }
}
