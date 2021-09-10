package com.example.testjava8.commuting;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @User : wspark
 * @Date : 2021-09-04
 */
@Slf4j
public class CsvRead {
    public static void main(String[] args) throws FileNotFoundException {
        // 환승역 연결을 위한 csv read
        List<SubwayTransVO> list = readCsv("C:\\Neo4j Desktop\\relate-data\\dbmss\\dbms-54c12005-ec3f-4117-bce7-3f5cf3d068ac\\import\\trans-station-time.csv");
        list.forEach(System.out::println);
        makeTransRelationShip(list);
    }

    public static List<SubwayTransVO> readCsv(String filepath) throws FileNotFoundException {
        List<SubwayTransVO> subway = new CsvToBeanBuilder<SubwayTransVO>(new FileReader(filepath))
                .withType(SubwayTransVO.class)
                .build()
                .parse();
        return subway;
    }
    public static void makeTransRelationShip(List<SubwayTransVO> list){
        Map<String, Set<SubwayTransVO>> map = new ConcurrentHashMap<>();
        if(list != null && list.size() > 0){
            map = list.stream().collect(Collectors.groupingBy(SubwayTransVO::getSubwayNm, Collectors.toSet()));
        }
        if(!map.isEmpty()){
            map.forEach((key, value) -> {
                List<SubwayTransVO> list1 = value.stream().sorted(Comparator.comparing(SubwayTransVO::getSubwayNm)).collect(Collectors.toList());
                for (SubwayTransVO vo : list1) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getSubwayNm()+"',getSubwayLineNm: '"+vo.getSubwaySourceLineNm()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getSubwayNm()+"',getSubwayLineNm: '"+vo.getSubwayTargetLineNm()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
//                    try (Session session = driver.session()) {
//                        session.writeTransaction(tx -> {
//                            Result result = tx.run(sb.toString());
//                            return 1;
//                        });
//                    }
                    log.info(sb.toString());
                }
                for (SubwayTransVO vo : list1) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getSubwayNm()+"',getSubwayLineNm: '"+vo.getSubwayTargetLineNm()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getSubwayNm()+"',getSubwayLineNm: '"+vo.getSubwaySourceLineNm()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
//                    try (Session session = driver.session()) {
//                        session.writeTransaction(tx -> {
//                            Result result = tx.run(sb.toString());
//                            log.info(result.consume().query().text());
//                            return 1;
//                        });
//                    }
                    log.info(sb.toString());
                }
            });
        }
    }
}
