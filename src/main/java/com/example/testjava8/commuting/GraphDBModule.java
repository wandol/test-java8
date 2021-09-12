package com.example.testjava8.commuting;

import com.example.testjava8.commuting.subway.SubwayTakeTIme;
import com.example.testjava8.commuting.subway.SubwayTransVO;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    public void makeMetroRelationShip(List<SubwayTakeTIme> list){
        //  지하철 호선별로 나눈다. 호선별로 relationship 생성.
        Map<String, Set<SubwayTakeTIme>> map = list.stream()
                .collect(Collectors.groupingBy(SubwayTakeTIme::getSubwayLine, Collectors.toSet()));

        Optional.ofNullable(map).ifPresent(v -> {
            v.forEach((key, value) -> {
                List<SubwayTakeTIme> list1 = value.stream().sorted(Comparator.comparing(SubwayTakeTIme::getSubwayCd))
                        .collect(Collectors.toList());
                for (SubwayTakeTIme vo : list1) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '").append(vo.getSourceSubway()).append("',subwayLine: '")
                        .append(vo.getSubwayLine()).append("'}),").append(" (d:Subway {subwayName: '")
                        .append(vo.getTargetSubway()).append("',subwayLine: '").append(vo.getSubwayLine()).append("'})");
                    sb.append(" create (s)-[:TAKE_MIN {cost: ").append(vo.getTakeMin()).append("}]->(d), (d)-[:TAKE_MIN {cost: ")
                        .append(vo.getTakeMin()).append("}]->(s) ");
                    voidQuery(sb.toString());
                    log.info(sb.toString());
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
            sb.append("Match (s:Subway {subwayName: '").append(vo.getSubwayNm()).append("',subwayLine: '")
                .append(vo.getSubwaySourceLineNm()).append("'}),").append(" (d:Subway {subwayName: '")
                .append(vo.getSubwayNm()).append("',subwayLine: '").append(vo.getSubwayTargetLineNm()).append("'})");
            sb.append(" create (s)-[:TAKE_MIN {cost: ").append(vo.getTakeMin()).append("}]->(d), (d)-[:TAKE_MIN {cost: ")
                .append(vo.getTakeMin()).append("}]->(s) ");
            voidQuery(sb.toString());
            log.info(sb.toString());
        });
    }
}