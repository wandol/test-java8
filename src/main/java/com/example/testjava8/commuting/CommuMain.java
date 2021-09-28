package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.*;
import com.example.testjava8.commuting.subway.SubwayTakeTIme;
import com.example.testjava8.commuting.subway.SubwayTransVO;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @User : wspark
 * @Date : 2021-09-15
 */
public class CommuMain {
    @SneakyThrows
    public static void main(String[] args) {
        GraphDBModule gt = new GraphDBModule("bolt://localhost:7687","neo4j","wandol");
        CommUtils utils = new CommUtils();

        //  전체 초기화
        gt.DELETEAllnode();

        //  ======================================= 지하철 ==================================================
        //  지하철 노드 그래프디비 로드.
        //  외부코드,전철명명(영문),전철역명,전철역코드,호선,출발지전철역,도착지전철역,역간소요분,
        gt.voidQuery(
                "LOAD CSV FROM 'file:///METRO-ALL.csv' as line " +
                        " CREATE (:Subway {subwayCd: line[0], subwayName:line[3], transGubun:line[6], subwayLine:line[1]," +
                        " subwayXcon:toFloat(line[7]), subwayYcon:toFloat(line[8]), sriKcode:line[10], sriTcode:line[11], sriMcode:line[12]," +
                        " sriBcode:line[13], sriCode:line[14], sriSubwayName:line[15]}) ");

        //  지하철역 소요시간 데이터 가져오기
        //  지하철역 환승 소요시간 데이터 가져오기
        //  지하철역을 노선별로 연결,
        try {
            List<SubwayTakeTIme> subwayTakeTimes = utils.readCsv(SubwayTakeTIme.class,
                    Constants.METRO_TAKEMIN_INFO_CSV_PATH);
            List<SubwayTransVO> transTakeTimes = utils.readCsv(SubwayTransVO.class,
                    Constants.METRO_TRANS_TAKEMIN_INFO_CSV_PATH);
            Optional.ofNullable(subwayTakeTimes).ifPresent(gt::makeMetroRelationShip);
            Optional.ofNullable(transTakeTimes).ifPresent(gt::makeMetroTransRelationShip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  ======================================= 버스 ==================================================

        List<BusRouteVO> busRouteVOList = null;
        //  todo 서울 전체 노선ID를 가지고 온다.
        try {
            busRouteVOList = utils.readCsv(BusRouteVO.class, Constants.SEOUL_BUS_ALL_NODE_INFO_CSV_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //  todo 노선ID를 가지고 정류장 리스트를 가지고 온다.
        //  http://ws.bus.go.kr/api/rest/busRouteInfo/getStaionByRoute?busRouteId=100100124&serviceKey=
//        LinkedList<BusStationVO> busStationVOLis = utils.getAllBusStByNode(busRouteVOList);

        //  todo 정류장 리스트를 csv로 떨군다. ( db화 )
//        utils.writeDataToCsv(busStationVOLis,Constants.SEOUL_BUS_ALL_NODE_PATH_INFO_CSV_PATH);

        //  todo 정류장 리스트 graph db 화
        gt.voidQuery(
                " LOAD CSV WITH HEADERS FROM 'file:///METRO_SEB_ST_INFO.csv' as line " +
                        " CREATE (:Bus {ars_id: line['ars_id'], gps_x:line['gps_x'], gps_y:line['gps_y'], pos_x:line['pos_x']," +
                        "   pos_y:line['pos_y'], trans_yn:line['trans_yn'], st_id:line['st_id'], st_nm:line['st_nm'], " +
                        "   route_ids:line['route_ids'], route_nms:line['route_nms'], area_nm:line['area_nm']}) ");
        gt.voidQuery(
                " LOAD CSV WITH HEADERS FROM 'file:///METRO_GGD_ST_INFO.csv' as line " +
                        " CREATE (:Bus {ars_id: line['ars_id'], gps_x:line['gps_x'], gps_y:line['gps_y'], pos_x:line['pos_x']," +
                        "   pos_y:line['pos_y'], st_id:line['st_id'], st_nm:line['st_nm'], " +
                        "   route_ids:line['route_ids'], route_nms:line['route_nms'], area_nm:line['area_nm']}) ");
        //  todo 정류장 리스트 관계형성.
        try {
            List<BusStationVO> sebNodeStationList = utils.readCsv(BusStationVO.class,
                    Constants.SEOUL_BUS_ALL_NODE_PATH_INFO_CSV_PATH);
            Optional.ofNullable(sebNodeStationList).ifPresent(gt::makeSebRelationShip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  연결종료
        gt.close();


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

        //  ======================================= 공고 ==================================================

        //  Graph name load
//        gt.voidQuery(
//                "CALL gds.graph.drop('subway-test') YIELD graphName;\n" +
//                "\n" +
//                "CALL gds.graph.create(\n" +
//                "    'PUBLIC-TRANSPORT',\n" +
//                "    '*',\n" +
//                "    'TAKE_MIN',\n" +
//                "    {\n" +
//                "        relationshipProperties: 'cost'\n" +
//                "    }\n" +
//                ") ");
    }

}

