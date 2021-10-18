package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusStationVO;
import com.example.testjava8.commuting.subway.SubwayTakeTimeVO;
import com.example.testjava8.commuting.subway.SubwayTransVO;
import com.example.testjava8.commuting.subway.SubwayVO;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class GraphDBModule implements AutoCloseable{

    private final Driver driver;

    public GraphDBModule(String uri, String user, String password) {
        Config config = Config.builder()
                .withMaxConnectionLifetime( 30, TimeUnit.MINUTES )
                .withConnectionTimeout( 15, TimeUnit.SECONDS )
                .withMaxConnectionPoolSize( 50 )
                .withConnectionAcquisitionTimeout( 2, TimeUnit.MINUTES )
                .withLogging(Logging.console(Level.INFO))
                .build();
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user,password),config);
    }


    @Override
    public void close() {
        driver.close();
    }

    /**
     *  모든 노드, 관계 삭제.
     * @return
     */
    public void DELETEAllnode(){
        try ( Session session = driver.session() )
        {
            session.writeTransaction( tx -> {
                tx.run( "MATCH (n) DETACH DELETE n");
                return 1;
            } );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  csvLoad
     *  csv 파일은 import 경로에 있어야 하며 csv안의 요소는 각자 알아야 함.
     */
    public void voidQuery(String query){
        try ( Session session = driver.session() )
        {
            session.writeTransaction( tx -> {
                Result result = tx.run(query);
                return 1;
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     *  지하철 호선별로 relationship 명은 TAKE_MIN
     *
     * @param list
     */
    public void makeMetroRelationShip(List<SubwayTakeTimeVO> list){
        //  지하철 호선별로 나눈다. 호선별로 relationship 생성.
        Map<String, Set<SubwayTakeTimeVO>> map = list.stream()
                .collect(Collectors.groupingBy(SubwayTakeTimeVO::getSubwayEnLine, Collectors.toSet()));
        log.debug(map.toString());
        Optional.ofNullable(map).ifPresent(v -> {
            v.forEach((key, value) -> {
                List<SubwayTakeTimeVO> list1 = value.stream().sorted(Comparator.comparing(SubwayTakeTimeVO::getSubwayCd))
                        .collect(Collectors.toList());
                for (SubwayTakeTimeVO vo : list1) {
                    String lineName = key;
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:" + vo.getSubwayEnLine() + " {subwayName: '").append(vo.getSourceSubway()).append("',subwayLine: '")
                            .append(vo.getSubwayLine()).append("'}),").append(" (d:" + vo.getSubwayEnLine() + " {subwayName: '")
                            .append(vo.getTargetSubway()).append("',subwayLine: '").append(vo.getSubwayLine()).append("'})");
                    sb.append(" create (s)-[:TAKE_MIN {cost: ").append(vo.getTakeMin()).append("}]->(d), (d)-[:TAKE_MIN {cost: ")
                            .append(vo.getTakeMin()).append("}]->(s) ");
                    voidQuery(sb.toString());
                }
            });
        });
    }

    /**
     *  지하철 환승역 relationship 명은 TAKE_MIN
     *
     * @param list
     */
    public void makeMetroTransRelationShip(List<SubwayTransVO> list){
        list.forEach(vo -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Match (s:"+vo.getSubwaySourceLineEnNm()+" {subwayName: '").append(vo.getSubwayNm()).append("',subwayLine: '")
                    .append(vo.getSubwaySourceLineNm()).append("'}),").append(" (d:"+ vo.getSubwayTargetLineEnNm() +" {subwayName: '")
                    .append(vo.getSubwayNm()).append("',subwayLine: '").append(vo.getSubwayTargetLineNm()).append("'})");
            sb.append(" create (s)-[:TAKE_MIN {cost: ").append(vo.getTakeMin()).append("}]->(d), (d)-[:TAKE_MIN {cost: ")
                    .append(vo.getTakeMin()).append("}]->(s) ");
            voidQuery(sb.toString());
        });
    }

    /**
     *  서울 버스 노선별로 relationship 연결   TAKE_MIN
     * @param busStationVOS
     */
//    public void makeSebRelationShip(List<BusStationVO> busStationVOS) {
//        CommUtils utils = new CommUtils();
//        //  정류장 별로 그룹화
//        Map<String, Set<BusStationVO>> map = busStationVOS.stream()
//                .collect(Collectors.groupingBy(BusStationVO::getBusRouteId, Collectors.toSet()));
//        Optional.of(map).ifPresent(list -> {
//            list.forEach((key, value) -> {
//                List<BusStationVO> temp = new ArrayList<>(value).stream().sorted(Comparator.comparing(BusStationVO::getSeq)).collect(Collectors.toList());
//                log.info(temp.toString());
//                IntStream.range(0, (temp.size() - 1)).parallel().forEach(i -> {
//                    String takeMin = "";
//                    String tempDist;
//
//                    if(i < (temp.size() - 1)) tempDist = temp.get(i + 1).getFullSectDist();
//                    else tempDist = temp.get(i).getFullSectDist();
//                    takeMin = utils.makeBusTakeMinBYDistinct(tempDist);
//
//                    StringBuilder sb = new StringBuilder();
//                    if(i < (temp.size() - 1)){
//                        sb.append("Match (s:Bus {st_id: '").append(temp.get(i).getStation()).append("'}),")
//                                .append(" (d:Bus {st_id: '").append(temp.get(i + 1).getStation()).append("'})");
//                        sb.append(" create (s)-[:TAKE_MIN {cost: ").append(takeMin).append("}]->(d) ");
//                    }
//                    log.info(sb.toString());
//                    voidQuery(sb.toString());
//                });
//            });
//        });
//        busStationVOS.forEach(vo -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Match (s:Bus {subwayName: '").append(vo.getSubwayNm()).append("',subwayLine: '")
//                    .append(vo.getSubwaySourceLineNm()).append("'}),").append(" (d:Subway {subwayName: '")
//                    .append(vo.getSubwayNm()).append("',subwayLine: '").append(vo.getSubwayTargetLineNm()).append("'})");
//            sb.append(" create (s)-[:TAKE_MIN {cost: ").append(vo.getTakeMin()).append("}]->(d), (d)-[:TAKE_MIN {cost: ")
//                    .append(vo.getTakeMin()).append("}]->(s) ");
//            voidQuery(sb.toString());
//            log.info(sb.toString());
//        });
//    }

    /**
     *  수도권 모든 지하철역 Graph db 노드화.
     * @param list
     */
    public void makeMetroAllNode(List<SubwayVO> list) {

        //  호선변로 나눠서 노드화.
        Map<String, Set<SubwayVO>> map = list.stream()
                .collect(Collectors.groupingBy(SubwayVO::getSubwayEnLine, Collectors.toSet()));

        Optional.ofNullable(map).ifPresent(v -> {
            v.forEach((key, value) -> {
                //  한 호선의 list vo
                List<SubwayVO> lineList = value.stream().sorted(Comparator.comparing(SubwayVO::getSubwayCd))
                        .collect(Collectors.toList());
                log.debug(lineList.toString());
                for (SubwayVO vo : lineList) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("CREATE (:" + vo.getSubwayEnLine() +" { " +
                            "               subwayCd: '" + vo.getSubwayCd() + "'," +
                            "               subwayName:'" + vo.getSubwayNm() + "', " +
                            "               transGubun:'" + vo.getTransGubun() + "', " +
                            "               subwayLine:'" + vo.getSubwayLine() + "', " +
                            "               subwayXcon:'" + vo.getXCon() + "', " +
                            "               subwayYcon:'" + vo.getYCon() + "', " +
                            "               sriKcode:" + vo.getKcode() + ", " +
                            "               sriTcode:" + vo.getTcode() + ", " +
                            "               sriMcode:" + vo.getMcode() + ", " +
                            "               sriBcode:" + vo.getBcode() + ", " +
                            "               sriCode:" + vo.getCode() + ", " +
                            "               sriSubwayName:'" + vo.getSriSubwayName() + "'})  " );
                    voidQuery(sb.toString());
                }
            });
        });
    }
}
