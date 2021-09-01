package com.example.testjava8;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class post {


    public static void main(String[] args) throws Exception {
        //  소요시간 구하기

        //  todo 지하철 출발역,도착역 코드 가져오기
        List<Subway> list = readCsv("C:\\Users\\user\\IdeaProjects\\test-java8\\subwayTime.csv");
        for (Subway subway : list) {
            log.info(subway.toString());
            log.info("target : {} , source : {}, time : {} ",subway.getTargetSubway(),subway.getSourceSubway(),subway.getTakeMin());
        }
        //Map<String, Set<Subway>> map = list.stream().collect(Collectors.groupingBy(Subway::getSubwayLine,Collectors.toSet()));
        //  todo 소요시간 구함.
        //List<Subway> returnList = getTime(map);
        //  todo 소요시간 저장.
        //writeCsv(returnList,"C:\\Users\\user\\IdeaProjects\\test-java8\\real_sub.csv");

    }

    public static void writeCsv(List<Subway> returnList, String path) throws Exception {
        Writer writer  = new FileWriter(path);

        StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();
        sbc.write(returnList);
        writer.close();
    }
    private static List<Subway> getTime(Map<String, Set<Subway>> groupMap) {
        List<Subway> returnList = new ArrayList<>();
        groupMap.entrySet().forEach(map -> {
            List<Subway> sortedList = map.getValue().stream().sorted(Comparator.comparing(Subway::getEtcCd))
                    .collect(Collectors.toList());
            if(!sortedList.isEmpty()){
                //Test용 로컬 주소
                IntStream.range(0, sortedList.size()).forEach(i -> {
                    if(i != sortedList.size() - 1){
                        MultiValueMap<String,String> parameters = new LinkedMultiValueMap<>();
                        parameters.add("departureId", sortedList.get(i).getSubwayCd());
                        parameters.add("arrivalId", sortedList.get(i+1).getSubwayCd());

                        String url = "http://www.seoulmetro.co.kr/kr/getRouteSearchResult.do";
                        ResponseEntity<String> res = new RestTemplate().postForEntity(url, parameters, String.class);
                        if(res.getBody() != null){
                            Document doc = Jsoup.parseBodyFragment(Objects.requireNonNull(res.getBody()));
                            Subway sb = sortedList.get(i);
                            sb.setTakeMin(doc.getElementsByTag("totalTime").text());
                            sb.setSourceSubway(sortedList.get(i).getSubwayNm());
                            sb.setTargetSubway(sortedList.get(i+1).getSubwayNm());
                            returnList.add(sb);
                        }
                    }
                });
            }
        });
        return returnList;
    }

    public static List<Subway> readCsv(String filepath) throws FileNotFoundException {
        return new CsvToBeanBuilder(new FileReader(filepath))
                .withType(Subway.class)
                .build()
                .parse();
    }
}
