package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusStationVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class CommUtils {

    @Autowired
    public Environment env;
    /**
     *  csv 파일 읽어 리스트 vo로 반환.
     * @param type
     * @param path
     * @param <T>
     * @return
     * @throws FileNotFoundException
     */
    public static <T> List<T> readCsv(Class<T> type, String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        List<T> t = new CsvToBeanBuilder<T>(new FileReader(resource.getFile()))
                .withType(type)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                .withIgnoreEmptyLine(true)
                .build()
                .parse();
        return t;
    }

    /**
     *  csv write
     * @param list
     * @param filePath
     */
    public static void writeDataToCsv(LinkedList<BusStationVO> list, String filePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            String[] cate = {"arsId","beginTm","busRouteId","busRouteNm","direction",
                    "gpsX","gpsY","lastTm","posX","posY","routeType","sectSpd","section","seq",
                    "station","stationNm","stationNo","transYn","fullSectDist","trnstnid"};
            writer.writeNext(cate);
            list.forEach(v -> {
                writer.writeNext(new String[]{v.getArsId(),v.getBeginTm(),v.getBusRouteId(),v.getBusRouteNm(),
                        v.getDirection(),v.getGpsX(),v.getGpsY(),v.getLastTm(),v.getPosX(),v.getPosY(),
                        v.getRouteType(),v.getSectSpd(),v.getSection(),v.getSeq(),v.getStation(),v.getStationNm(),
                        v.getStationNo(),v.getTransYn(),v.getFullSectDist(),v.getTrnstnid()});
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  특정 파라미터로 리스트vo 값 가져오기.
     * @param apiUrl
     * @return
     */
    public static LinkedList<BusStationVO> getListVO(String apiUrl) {
        LinkedList<BusStationVO> returnList = new LinkedList<>();
        ResponseEntity<String> res = new RestTemplate().getForEntity(apiUrl, String.class);
        if(res.getBody() != null){
            Document doc = null;
            try {
                doc = Jsoup.connect(apiUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc != null){
                Elements itemList = doc.getElementsByTag("itemList");
                ObjectMapper xmlMapper = new XmlMapper();
                itemList.forEach(vo -> {
                    try {
                        returnList.add(xmlMapper.readValue(vo.toString(), BusStationVO.class));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return  returnList;
    }

    /**
     *  api url 만들기.
     * @param domain
     * @param paramValue
     * @param paramName
     * @return
     */
    public static String makeApiUrl(String domain, String paramValue, String paramName) {
        StringBuffer returnUrl = new StringBuffer();
        if(domain != null &&!"".equals(domain)){
            returnUrl.append(domain);
            returnUrl.append("serviceKey=" + Constants.SEOUL_BUS_API_KEY);
            returnUrl.append("&"+paramName+"="+paramValue);
        }
        return returnUrl.toString();
    }
}
