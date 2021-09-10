package com.example.testjava8.commuting;

import com.konantech.konansearch.KSEARCH;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @User : wspark
 * @Date : 2021-08-23
 */
@Slf4j
public class GraphDbTest implements AutoCloseable {

    private final Driver driver;

    public GraphDbTest(String uri, String user, String password) {
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
    public void close() throws Exception {
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
        }
    }

    /**
     *  csvLoad
     *  csv 파일은 import 경로에 있어야 하며 csv안의 요소는 각자 알아야 함.
     *  외부코드,전철명명(영문),전철역명,전철역코드,호선,출발지전철역,도착지전철역,역간소요분,
     */
    public void csvLoadAndNodeCreate(String filepath){
        try ( Session session = driver.session() )
        {
            session.writeTransaction( tx -> {
                Result result = tx.run( "LOAD CSV FROM 'file:///" + filepath + "' as line " +
                        " CREATE (:Subway {subwayCd: line[0], subwayName:line[3], transGubun:line[6], subwayLine:line[1]" +
                        ", subwayXcon:line[7], subwayYcon:line[8], sriKcode:line[10], sriTcode:line[11], sriMcode:line[12]" +
                        ", sriBcode:line[13], sriCode:line[14], sriSubwayName:line[15]})  " );
                return 1;
            });
        }
    }

    /**
     *  relationship 명은 TAKE_MIN
     * @param list
     */
    public void makeRelationShip(List<SubwayTakeTIme> list){
        //  todo 한 호선의 첫 지하철 역의 정보를 가져온다.
        //  todo 역정보의 소요시간과 다음 역을 가지고 관계 형성.
        //  todo 다음역정보 를 통해 다시 반복
        //  지하철 호선별로 나눈다. 호선별로 relationship 생성.
        Map<String, Set<SubwayTakeTIme>> map = new ConcurrentHashMap<>();
        if(list != null && list.size() > 0){
            map = list.stream().collect(Collectors.groupingBy(SubwayTakeTIme::getSubwayLine, Collectors.toSet()));
        }
        if(!map.isEmpty()){
            map.forEach((key, value) -> {
                List<SubwayTakeTIme> list1 = value.stream().sorted(Comparator.comparing(SubwayTakeTIme::getSubwayCd)).collect(Collectors.toList());
                for (SubwayTakeTIme vo : list1) {
//                    log.info(vo.toString());
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getSourceSubway()+"',subwayLine: '"+vo.getSubwayLine()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getTargetSubway()+"',subwayLine: '"+vo.getSubwayLine()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
                    try (Session session = driver.session()) {
                        session.writeTransaction(tx -> {
                            Result result = tx.run(sb.toString());
                            return 1;
                        });
                    }
                    log.info(sb.toString());
                }
                for (SubwayTakeTIme vo : list1) {
//                    log.info(vo.toString());
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getTargetSubway()+"',subwayLine: '"+vo.getSubwayLine()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getSourceSubway()+"',subwayLine: '"+vo.getSubwayLine()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
                    try (Session session = driver.session()) {
                        session.writeTransaction(tx -> {
                            Result result = tx.run(sb.toString());
                            log.info(result.consume().query().text());
                            return 1;
                        });
                    }
                    log.info(sb.toString());
                }
            });
        }

    }

    public void makeTransRelationShip(List<SubwayTransVO> list){
        Map<String, Set<SubwayTransVO>> map = new ConcurrentHashMap<>();
        if(list != null && list.size() > 0){
            map = list.stream().collect(Collectors.groupingBy(SubwayTransVO::getSubwayNm, Collectors.toSet()));
        }
        if(!map.isEmpty()){
            map.forEach((key, value) -> {
                List<SubwayTransVO> list1 = value.stream().sorted(Comparator.comparing(SubwayTransVO::getSubwayNm)).collect(Collectors.toList());
                for (SubwayTransVO vo : list1) {
//                    log.info(vo.toString());
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getSubwayNm()+"',subwayLine: '"+vo.getSubwaySourceLineNm()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getSubwayNm()+"',subwayLine: '"+vo.getSubwayTargetLineNm()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
                    try (Session session = driver.session()) {
                        session.writeTransaction(tx -> {
                            Result result = tx.run(sb.toString());
                            return 1;
                        });
                    }
                    log.info(sb.toString());
                }
                for (SubwayTransVO vo : list1) {
//                    log.info(vo.toString());
                    StringBuffer sb = new StringBuffer();
                    sb.append("Match (s:Subway {subwayName: '"+vo.getSubwayNm()+"',subwayLine: '"+vo.getSubwayTargetLineNm()+"'})," +
                            " (d:Subway {subwayName: '"+vo.getSubwayNm()+"',subwayLine: '"+vo.getSubwaySourceLineNm()+"'})");
                    sb.append(" create (s)-[:ROAD {cost: "+vo.getTakeMin()+"}]->(d)");
                    try (Session session = driver.session()) {
                        session.writeTransaction(tx -> {
                            Result result = tx.run(sb.toString());
                            return 1;
                        });
                    }
                    log.info(sb.toString());
                }
            });
        }

    }

    static <T> List<T> readCsv(Class<T> type, String filepath) throws FileNotFoundException {
        List<T> subway = new CsvToBeanBuilder<T>(new FileReader(filepath))
                .withType(type)
                .build()
                .parse();
        return subway;
    }

    public List<SubwayTakeTIme> readCsv(String filepath) throws FileNotFoundException {
        List<SubwayTakeTIme> subway = new CsvToBeanBuilder<SubwayTakeTIme>(new FileReader(filepath))
                .withType(SubwayTakeTIme.class)
                .build()
                .parse();
        return subway;
    }
    public List<SubwayTransVO> readCsv2(String filepath) throws FileNotFoundException {
        List<SubwayTransVO> subway = new CsvToBeanBuilder<SubwayTransVO>(new FileReader(filepath))
                .withType(SubwayTransVO.class)
                .build()
                .parse();
        return subway;
    }
    //  todo module 호출하여 graph db return 값 가져오기.
    private String getTakeMinData() {
        try ( Session session = driver.session() )
        {
            session.writeTransaction( tx -> {
                Result result = tx.run( "MATCH (source:Subway {subwayName: '명일'}), (target:Subway {subwayName: '구로디지털단지'})\n" +
                        "CALL gds.shortestPath.dijkstra.stream('subway-test', {\n" +
                        "    sourceNode: source,\n" +
                        "    targetNode: target,\n" +
                        "    relationshipWeightProperty: 'cost'\n" +
                        "})\n" +
                        "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path\n" +
                        "RETURN\n" +
                        "    index,\n" +
                        "    gds.util.asNode(sourceNode).subwayName AS sourceNodeName,\n" +
                        "    gds.util.asNode(targetNode).subwayName AS targetNodeName,\n" +
                        "    totalCost,\n" +
                        "    [nodeId IN nodeIds | gds.util.asNode(nodeId).subwayLine] AS subwayLines,\n" +
                        "\t[nodeId IN nodeIds | gds.util.asNode(nodeId).subwayName] AS subwayNames,\n" +
                        "    costs,\n" +
                        "    nodes(path) as path\n" +
                        "ORDER BY index");
                while ( result.hasNext() )
                {
                    Record record = result.next();
                    System.out.println( record.get( "sourceNodeName" ).asString() + " " +  record.get("targetNodeName").asString()
                            + " " +  record.get("totalCost").asFloat() + " " +  record.get("subwayNames").asList() + " " +  record.get("subwayLines").asList());
                }
                return result;
            });
        }
        return ";";
    }

    public static List<RecruSubVO> dcSearch(String ip, String scn, String where, int limit) throws IOException {

        long handle;
        int rc = 0;
        int totalCount = 0;
        int columnSize;
        int rowSize;
        String[] fieldName;
        String[] fieldData;
        int offset = 0;
        String memIdx = "";
        String memIdx_pre = "";
        RecruSubVO recruitVO;

        ArrayList<RecruSubVO> resultList = new ArrayList<RecruSubVO>();

        KSEARCH ks = new KSEARCH();
        handle = ks.CreateHandle();

        ks.SetCharacterEncoding(handle, "UTF-8");
        while (offset <= totalCount){

            try {
                rc = ks.Search(handle, ip, scn, where,
                        "order by rec_idx asc" , "", "", offset, limit, ks.LC_KOREAN, ks.CS_UTF8);
                if (rc < 0) {
                    log.info(ks.GetErrorMessage(handle));
                    return null;
                }
            } catch (IOException e) {
                log.info("Error: " + e.getMessage());
                return null;
            }

            totalCount = ks.GetResult_TotalCount(handle);
            rowSize = ks.GetResult_RowSize(handle);
            columnSize = ks.GetResult_ColumnSize(handle);
            fieldName = new String[columnSize];
            fieldData = new String[columnSize];

            for (int i = 0; i < rowSize; i++) {
                recruitVO = new RecruSubVO();

                ks.GetResult_ColumnName(handle, fieldName, columnSize);
                ks.GetResult_Row(handle, fieldData, i);
                for (int j = 0; j < columnSize; j++) {
                    if(fieldName[j].equals("rec_idx"))
                        recruitVO.setRec_idx(fieldData[j]);
                    if(fieldName[j].equals("loc_mcd"))
                        recruitVO.setLoc_mcd(fieldData[j]);
                    if(fieldName[j].equals("loc_bcd"))
                        recruitVO.setLoc_bcd(fieldData[j]);
                    if(fieldName[j].equals("cat_mcls"))
                        recruitVO.setCat_mcls(fieldData[j]);
                    if(fieldName[j].equals("cat_kewd"))
                        recruitVO.setCat_kewd(fieldData[j]);
                    if(fieldName[j].equals("company_nm"))
                        recruitVO.setCompany_nm(fieldData[j]);
                    if(fieldName[j].equals("title"))
                        recruitVO.setTitle(fieldData[j]);
                    if(fieldName[j].equals("apply_cnt"))
                        recruitVO.setApply_cnt(fieldData[j]);
                    if(fieldName[j].equals("closing_dt"))
                        recruitVO.setClosing_dt(fieldData[j]);
                    if(fieldName[j].equals("subway_cd"))
                        recruitVO.setSubway_cd(fieldData[j]);
                    if(fieldName[j].equals("subway_cd_distance"))
                        recruitVO.setSubway_cd_distance(fieldData[j]);
                }
                resultList.add(recruitVO);
            }
            offset += limit;
        }
        return resultList;
    }

    public void recruitNodeCreate(List<RecruSubVO> list){
        try ( Session session = driver.session() )
        {
            list.forEach(v -> {
                session.writeTransaction( tx -> {
                    Result result = tx.run( " CREATE (:Recruit {rec_idx: '"+ v.getRec_idx() + "'," +
                                                                    " loc_mcd: '" + v.getLoc_mcd() +"'," +
                                                                    " loc_bcd: '" + v.getLoc_bcd() +"'," +
                                                                    " cat_mcls: '" + v.getCat_mcls() +"'," +
                                                                    " cat_kewd: '" + v.getCat_kewd() +"'," +
                                                                    " company_nm: '" + v.getCompany_nm().replaceAll("'","`") +"'," +
                                                                    " title: '" + v.getTitle().replaceAll("'","\"") +"'," +
                                                                    " apply_cnt: '" + v.getApply_cnt() +"'," +
                                                                    " closing_dt: '" + v.getClosing_dt() +"'," +
                                                                    " subway_cd: '" + v.getSubway_cd() +"'," +
                                                                    " subway_cd_distance: '" + v.getSubway_cd_distance() +"'" +
                                                                    "})  " );
                return 1;
            });
        });
        }
    }

    public static void main(String[] args) throws Exception {
        try(GraphDbTest gt = new GraphDbTest("bolt://localhost:7687","neo4j","wandol")){
            //  데이터 초기화
            gt.DELETEAllnode();

            //  지하철역 데이터 get 및 노드 생성
            gt.csvLoadAndNodeCreate("FInalSubwayInfo.csv");

            //  역간 소요시간 get 및 연결 (road) 생성
            List<SubwayTakeTIme> list = gt.readCsv("C:\\Neo4j Desktop\\relate-data\\dbmss\\dbms-2ae0785e-c577-4204-a219-a1b82cccf854\\import\\takeMinInfo.csv");
            gt.makeRelationShip(list);

            //  환승역 데이터 get 및 연결 (road) 생성
            List<SubwayTransVO> list1 = gt.readCsv2("C:\\Neo4j Desktop\\relate-data\\dbmss\\dbms-2ae0785e-c577-4204-a219-a1b82cccf854\\import\\trans-station-time.csv");
            gt.makeTransRelationShip(list1);

            //  todo 공고데이터 get 및 노드 생성
            List<RecruSubVO> recList = dcSearch("10.100.0.112:6166","recruit_search_graph","subway_cd != '0'",1000);
            gt.recruitNodeCreate(recList);
            Objects.requireNonNull(recList).forEach(v -> log.info(v.toString()));
            gt.makeRecruRelationShip(recList);

            //  todo 공고데이터 지하철역 연결 ( code 값 )

//            List<SubwayVO> subwayList = readCsv(SubwayVO.class,"c://FInalSubwayInfo.csv");
//            subwayList.forEach(v -> log.info(v.toString()));

        }
    }

    private void makeRecruRelationShip(List<RecruSubVO> recList) {
        if(recList != null && recList.size() > 0){
            recList.forEach(v -> {
                String subRecruMin = "0";
                if(v.getSubway_cd_distance() != null){
                    if(v.getSubway_cd_distance().indexOf(",") > 0){
                        String[] subCd = v.getSubway_cd_distance().split(",");
                        for (int i = 0; i < subCd.length; i++) {
                            if(subCd[i].indexOf("|") > 0){
                                String[] min = subCd[i].split("\\|");
                                if(v.getSubway_cd().equals(min[0])){
                                    subRecruMin = min[1];
                                }
                            }
                        }
                    }
                }
                StringBuffer sb = new StringBuffer();
                sb.append("Match (s:Subway {sriCode: '"+v.getSubway_cd()+"'})," +
                        " (d:Recruit {subway_cd: '"+v.getSubway_cd()+"', rec_idx: '"+ v.getRec_idx()+"'})");
                sb.append(" create (s)-[:ROAD {cost: "+subRecruMin+"}]->(d)");
                try (Session session = driver.session()) {
                    session.writeTransaction(tx -> {
                        Result result = tx.run(sb.toString());
                        return 1;
                    });
                }
                log.info(sb.toString());
            });
        }
    }

}
