package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusNodeVO;
import com.example.testjava8.commuting.bus.Temp3VO;
import com.opencsv.CSVWriter;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Main1 {
    /**
     *  경기도 버스 좌표가 잘못되어 다시 구함.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // http://apis.data.go.kr/6410000/busrouteservice/getBusRouteStationList?serviceKey=%2F1T27kausQ5i6QmZz2F97BDjYCtKt%2B2JxO7achdmwnishpW%2BbbacYh69htyNR%2FaHNbvZRO8m70YINbyaOMk8ww%3D%3D&routeId=200000085
        // csvs/METRO_GGD_NODE_INFO.csv


        CommUtils utils = new CommUtils();

        List<String> removeKey = new LinkedList<>();
        ClassPathResource resource = new ClassPathResource("csvs/removeKey.txt");
        Path path = Paths.get("C:\\Users\\seok4\\IdeaProjects\\test-java8\\src\\main\\resources\\csvs\\removeKey.txt");

        removeKey = Files.readAllLines(path);

        List<BusNodeVO> list = utils.readCsv(BusNodeVO.class,"csvs/METRO-GGDB-NODE-INFO.csv");
        Map<String,List<BusNodeVO>> map = list.stream().collect(Collectors.groupingBy(BusNodeVO::getBus_route_id));
        if(removeKey.size() > 0){
            removeKey.forEach(map::remove);
        }
        List<BusNodeVO> result = new LinkedList<>();
        List<String> remove1 = new LinkedList<>();

        int index = 0;
        for (String key : map.keySet()) {
            StringBuffer url = new StringBuffer();
            url.append("http://apis.data.go.kr/6410000/busrouteservice/getBusRouteStationList?");
            url.append("serviceKey=" + Constants.SEOUL_BUS_API_KEY2);
            url.append("&routeId=" + key);
            System.out.println(url.toString());
            LinkedList<Temp3VO> temp = utils.getListVO(Temp3VO.class, url.toString(), "busRouteStationList");
            if(temp == null) {
                System.out.println(key);
                break;
                //throw new Exception("만료된 API");
            }else if(temp.size() == 0 ){
                System.out.println(key);
                break;
            }else{
                for (int j = 0; j < temp.size(); j++) {
                    BusNodeVO vo = new BusNodeVO();
                    vo.setArs_id(temp.get(j).getMobileNo() != null?temp.get(j).getMobileNo().trim():"");
                    vo.setBus_route_id(key);
                    vo.setBus_route_nm(map.get(key).get(0).getBus_route_nm());
                    vo.setSeq(temp.get(j).getStationSeq());
                    vo.setTime_next_st("");
                    vo.setDist_next_st("");
                    vo.setTrn_st_id("");
                    vo.setPos_y("");
                    vo.setPos_x("");
                    vo.setGps_y(temp.get(j).getY());
                    vo.setGps_x(temp.get(j).getX());
                    vo.setSt_nm(temp.get(j).getStationName());
                    vo.setSt_id(temp.get(j).getStationId());
                    vo.setCity_nm(temp.get(j).getRegionName());
                    result.add(vo);
                }
                remove1.add(key);
            }
            index++;
        }

        remove1.forEach(System.out::println);

        //  지하철 - 버스 정류장 연결 csv만들기.
        CSVWriter writer = new CSVWriter(new FileWriter("d:/METRO-GGDB-NODE-INFO4.csv"));
        String[] cate = {"bus_route_id","bus_route_nm","ars_id","st_id","st_nm","gps_x","gps_y",
                "pos_x","pos_y","seq","dist_next_st","time_next_st","trn_st_id","city_nm"};
        writer.writeNext(cate);
        result.forEach(v -> {
            writer.writeNext(new String[]{v.getBus_route_id(),v.getBus_route_nm(),v.getArs_id(),v.getSt_id(),
                    v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),v.getPos_y(),v.getSeq()
                    ,v.getDist_next_st(),v.getTime_next_st(),v.getTrn_st_id(),v.getCity_nm()});
        });
        writer.close();
    }
}
