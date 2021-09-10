package com.example.testjava8.commuting;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @User : wspark
 * @Date : 2021-08-27
 */
@Slf4j
public class Subway {

//    CREATE  (a:Location {name: 'A'}),
//            (a)-[:ROAD {cost: 50}]->(b);
    //  todo 지하철 노드 생성

    public static void main(String[] args) {
        ConcurrentMap<String,String> list = makeNodeSubway("Y:\\work\\그래프db\\전국철도역명호선명.txt");

    }

    //  todo 지하철 노드 생성
    //  todo 지하철 노드 생성

    public static ConcurrentMap<String,String> makeNodeSubway(String filepath){
        File file = new File(filepath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            ConcurrentMap<String,String>  subway = new ConcurrentHashMap<>();
            String line;
            while ((line = br.readLine()) != null) {
                log.info(line);
                subway.put(line.split(",")[0],line.split(",")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
