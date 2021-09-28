package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusGGDNodeVO;
import com.example.testjava8.commuting.bus.BusGGDNodeVOTmp;
import com.example.testjava8.commuting.bus.BusStationVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
public class CommUtils {

    /**
     *  csv 파일 읽어 리스트 vo로 반환.
     * @param type
     * @param path
     * @param <T>
     * @return
     * @throws FileNotFoundException
     */
    public <T> List<T> readCsv(Class<T> type, String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        List<T> t = new CsvToBeanBuilder<T>(new FileReader(resource.getFile()))
                .withType(type)
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
    public void writeDataToCsv(LinkedList<BusStationVO> list, String filePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            String[] cate = {"arsId","beginTm","busRouteId","busRouteNm","direction",
                    "gpsX","gpsY","lastTm","posX","posY","routeType","sectSpd","section","seq",
                    "station","stationNm","stationNo","transYn","fullSectDist","trnstnid"};
            writer.writeNext(cate);
            list.forEach(v -> {
                writer.writeNext(new String[]{v.getArsId(),v.getBeginTm(),v.getBusRouteId(),v.getBusRouteNm(),
                        v.getDirection(),v.getGpsX(),v.getGpsY(),v.getLastTm(),v.getPosX(),v.getPosY(),
                        v.getRouteType(),v.getSectSpd(),v.getSection(),String.valueOf(v.getSeq()),v.getStation(),v.getStationNm(),
                        v.getStationNo(),v.getTransYn(),v.getFullSectDist(),v.getTrnstnid()});
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  특정 파라미터로 리스트vo 값 가져오기.
     *
     * @param type
     * @param apiUrl
     * @return
     */
    public <T> LinkedList<T> getListVO(Class<T> type, String apiUrl,String findTagNm) throws Exception {
        LinkedList<T> returnList = new LinkedList<>();
//        UriComponents uri = UriComponentsBuilder.fromHttpUrl(apiUrl).build();
//        ResponseEntity<String> res = new RestTemplate().getForEntity(uri.toUri(), String.class);
//        if(res.getBody() != null){
        Document doc = null;
        try {
            doc = Jsoup.connect(apiUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(doc != null){
            Elements resCode = doc.getElementsByTag("resultCode");
            if(resCode.text().equals("99")){
                return null;
            }else{
                Elements itemList = doc.getElementsByTag(findTagNm);
                ObjectMapper xmlMapper = new XmlMapper();
                itemList.forEach(vo -> {
                    try {
                        returnList.add(xmlMapper.readValue(vo.toString(), type));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            }

        }
//        }
        return  returnList;
    }

    /**
     *  api url 만들기.
     * @param domain
     * @param paramValue
     * @param paramName
     * @return
     */
    public String makeApiUrl(String domain, String paramValue, String paramName) {
        StringBuffer returnUrl = new StringBuffer();
        if(domain != null &&!"".equals(domain)){
            returnUrl.append(domain);
            returnUrl.append("serviceKey=" + Constants.SEOUL_BUS_API_KEY);
            returnUrl.append("&"+paramName+"="+paramValue);
        }
        return returnUrl.toString();
    }

    /**
     *  거리(m)로 소요시간을 구한다.
     *
     * @param tempDis
     */
    public String makeBusTakeMinBYDistinct(String tempDis) {
        int temp = Integer.parseInt(tempDis);
        String returnVal = temp/100 <= 0 ? "1" : String.valueOf(temp/100);
        return returnVal;
    }

    public void writeDataToCsv(List<BusGGDNodeVO> list,String filePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            String[] cate = {"nodeid","nodenm","nodeno","gpslati","gpslong","nodeord","routeid"};
            writer.writeNext(cate);
            list.forEach(v -> {
                writer.writeNext(new String[]{v.getNodeid(),v.getNodenm(),v.getNodeno(),v.getGpslati(),
                        v.getGpslong(),v.getNodeord(),v.getRouteid()});
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTakeMinData(String url, String duration) throws MalformedURLException {
        String result = "";
        BufferedReader in = null;
        try {
            URL obj = new URL(url); // 호출할 url
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;
            StringBuffer sb = new StringBuffer();
            while((line = in.readLine()) != null) { // response를 차례대로 출력
                 sb.append(line);
            }
            System.out.println(sb.toString());
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(sb.toString());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray jarr = jsonObject.get("staticPaths").getAsJsonArray();
            for (JsonElement element : jarr) {
                JsonObject JsonObject = element.getAsJsonObject();
                String tt = JsonObject.get(duration).getAsString();
                System.out.println("duration = " + tt);

            }

        } catch(Exception e) {
            e.printStackTrace();
        }  finally { if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); } }

        return result;
    }
}
