package com.example.testjava8.commuting.bus;

import com.example.testjava8.commuting.CommUtils;
import com.example.testjava8.commuting.Constants;
import com.example.testjava8.commuting.GraphDBModule;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class BusMain {

    public static void main(String[] args) throws IOException {

        GraphDBModule gt = new GraphDBModule("bolt://localhost:7687","neo4j","wandol");
        CommUtils commUtils = new CommUtils();

        List<BusRouteVO> busRouteVOList = null;
        //  todo 서울 전체 노선ID를 가지고 온다.
        try {
            busRouteVOList = commUtils.readCsv(BusRouteVO.class, Constants.SEOUL_BUS_ALL_NODE_INFO_CSV_PATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //  todo 노선ID를 가지고 정류장 리스트를 가지고 온다.
        //  http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute?busRouteId=100100124&serviceKey=
//        LinkedList<BusStationVO> busStationVOLis = getAllBusStByNode(busRouteVOList);

        //  todo 정류장 리스트를 csv로 떨군다. ( db화 )
//        commUtils.writeDataToCsv(busStationVOLis,Constants.SEOUL_BUS_ALL_NODE_PATH_INFO_CSV_PATH);

        //  todo 정류장 리스트 graph db 화
        gt.voidQuery(
                " LOAD CSV WITH HEADERS FROM 'file:///csvs/SEB-ALL-STATION.csv' as line " +
                " CREATE (:Bus {arsId: line[0], beginTm:line[1], busRouteId:line[2], busRouteNm:line[3]," +
                        "   direction:line[4], gpsX:line[5], gpsY:line[6], lastTm:line[7], posX:line[8], " +
                        "   posY:line[9], routeType:line[10], sectSpd:line[11], section:line[12], seq:line[13], " +
                        "   station:line[14], stationNm:line[15], stationNo:line[16], transYn:line[17], " +
                        "   fullSectDist:line[18], trnstnid:line[19]}) ");

        //  todo 정류장 리스트 관계형성.
        //  todo 지하철역 좌표를 기준으로 약 1km반경의 주변 정류장을 찾아 버스정류장과 관계형성.
        //  http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?
        //  serviceKey=&tmX=127.1370392&tmY=37.5520712&radius=1000


//        ClassPathResource bus_seoul_all_csv_path = new ClassPathResource("csvs/SEB.csv");
//        try {
//            List<BusSEBVO> busAll = CommUtils.readCsv(BusSEBVO.class,bus_seoul_all_csv_path);
//            log.info(busAll.toString());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace(e);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * bus node id 로 버스정류장 리스트 ( 경로 ) 가져오기.
     * @param busRouteVOList
     * @return
     */
//    private static LinkedList<BusStationVO> getAllBusStByNode(List<BusRouteVO> busRouteVOList) {
//        CommUtils commUtils = new CommUtils();
//        LinkedList<BusStationVO> returnMap = new LinkedList<>();
//        if(busRouteVOList != null && busRouteVOList.size() > 0){
//            busRouteVOList.forEach(v -> {
//                String url = commUtils.makeApiUrl(Constants.SEOUL_BUS_API_URL,v.getROUTE_ID(),"busRouteId");
//                //  버스 node id 로 정류장 리스트 가져온다.
//                returnMap.addAll(commUtils.getListVO(url));
//            });
//        }
//        return returnMap;
//    }

}
