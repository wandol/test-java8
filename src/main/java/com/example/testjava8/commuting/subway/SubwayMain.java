package com.example.testjava8.commuting.subway;

import com.example.testjava8.commuting.CommUtils;
import com.example.testjava8.commuting.Constants;
import com.example.testjava8.commuting.GraphDBModule;
import com.example.testjava8.commuting.GraphDbTest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubwayMain {
    public static void main(String[] args) {
        GraphDBModule gt = new GraphDBModule("bolt://localhost:7687","neo4j","wandol");
        CommUtils utils = new CommUtils();
        //  전체 초기화
        gt.DELETEAllnode();

        //  지하철 노드 그래프디비 로드.
        //  외부코드,전철명명(영문),전철역명,전철역코드,호선,출발지전철역,도착지전철역,역간소요분,
        gt.voidQuery(
                "LOAD CSV FROM 'file:///csvs/METRO-ALL.csv' as line " +
                " CREATE (:Subway {subwayCd: line[0], subwayName:line[3], transGubun:line[6], subwayLine:line[1]," +
                " subwayXcon:line[7], subwayYcon:line[8], sriKcode:line[10], sriTcode:line[11], sriMcode:line[12]," +
                " sriBcode:line[13], sriCode:line[14], sriSubwayName:line[15]}) ");

        //  지하철역 소요시간 데이터 가져오기
        //  지하철역 환승 소요시간 데이터 가져오기
        //  지하철역을 노선별로 연결,
//        try {
//            List<SubwayTakeTIme> subwayTakeTimes = utils.readCsv(SubwayTakeTIme.class,
//                    Constants.METRO_TAKEMIN_INFO_CSV_PATH);
//            List<SubwayTransVO> transTakeTimes = utils.readCsv(SubwayTransVO.class,
//                    Constants.METRO_TRANS_TAKEMIN_INFO_CSV_PATH);
//            Optional.ofNullable(subwayTakeTimes).ifPresent(gt::makeMetroRelationShip);
//            Optional.ofNullable(transTakeTimes).ifPresent(gt::makeMetroTransRelationShip);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
