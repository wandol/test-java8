package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusGGDNodeVO;
import com.example.testjava8.commuting.bus.BusGGDNodeVOTmp;
import com.example.testjava8.commuting.subway.SubwayTakeTIme;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class TempMain {
    @SneakyThrows
    public static void main(String[] args) {
        CommUtils utils = new CommUtils();

        //  경기도 버스 좌표와 노선 경로 구해 csv
        List<BusGGDNodeVOTmp> list = utils.readCsv(BusGGDNodeVOTmp.class, Constants.GyongGido_BUS_ALL_NODE_INFO_CSV_PATH);
        LinkedList<BusGGDNodeVO> allBusStByNode = new LinkedList<>();
        for (BusGGDNodeVOTmp v : list) {
            StringBuffer url = new StringBuffer();
            url.append("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteAcctoThrghSttnList?");
            url.append("serviceKey=" + Constants.SEOUL_BUS_API_KEY);
            url.append("&numOfRows=1000&pageNo=1&");
            url.append("cityCode=" + v.getAdm_cd() + "&routeId=GGB" + v.getNode_id());
            System.out.println(url.toString());
            LinkedList<BusGGDNodeVO> temp = utils.getListVO(BusGGDNodeVO.class, url.toString(), "item");
            if(temp == null) {
                utils.writeDataToCsv(allBusStByNode,
                        "/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/csvs/METRO_GGD_NODE_INFO_TEMP.csv");
                throw new Exception("만료된 API");
            }else{
                if(temp.size() == 0){
                    FileWriter fw, fw_append; // FileWriter 선언
                    try {
                        fw = new FileWriter("/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/emptyBus.txt", true); // 파일이 있을경우 덮어쓰기
                        fw.write(v.getNode_id()+"\n");
                        fw.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            System.out.println(temp.toString());
            allBusStByNode.addAll(temp);
        }
        utils.writeDataToCsv(allBusStByNode,
                "/Users/wandol/Documents/development/intellijWorkspace/test-java8/src/main/resources/csvs/METRO_GGD_NODE_INFO_TEMP.csv");

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
