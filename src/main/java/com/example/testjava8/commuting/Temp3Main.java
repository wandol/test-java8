package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.BusNodeVO;
import com.example.testjava8.commuting.bus.BusStationVO;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Temp3Main {
    /**
     *  네이버 API로 각 정류장의 거리,걸릴시간 구함.
     * @param args
     */
    public static void main(String[] args) throws IOException {

        CommUtils utils = new CommUtils();

        //  경기도 버스 좌표로 naver api를 이용하여 각 정류소간의 소요시간 산정.
        //  https://map.naver.com/v5/api/transit/directions/point-to-point?start=126.9328667,37.3727833,name=곡란초등학교&goal=126.9316167,37.3751167,name=개나리후문&mode=TIME&lang=ko
//        List<BusNodeVO> sBusNodeList = utils.readCsv(BusNodeVO.class, "csvs/METRO-SEB-NODE-INFO-old.csv");
//        List<BusNodeVO> sBusNodeList = utils.readCsv(BusNodeVO.class, "bus/METRO-ICNB-NODE-INFO.csv");
        List<BusNodeVO> sBusNodeList = utils.readCsv(BusNodeVO.class,"csvs/METRO-SEB-NODE-INFO.csv");

        Map<String,List<BusNodeVO>> map = sBusNodeList.stream().collect(Collectors.groupingBy(BusNodeVO::getBus_route_id));
        AtomicInteger index = new AtomicInteger();
        List<BusNodeVO> resultList = new LinkedList<>();
        map.forEach((s, busNodeVOS) -> {
            IntStream.range(0, busNodeVOS.size()).forEach( i -> {
                StringBuffer url = new StringBuffer();
                try {
                    if(i != (busNodeVOS.size() - 1)){
                        url.append("https://map.naver.com/v5/api/transit/directions/point-to-point?");
                        url.append("start="+ URLEncoder.encode(busNodeVOS.get(i).getGps_x()+","+busNodeVOS.get(i).getGps_y()+",name=" + busNodeVOS.get(i).getSt_nm(),"utf-8"));
                        url.append("&goal="+ URLEncoder.encode(busNodeVOS.get(i+1).getGps_x()+","+busNodeVOS.get(i+1).getGps_y()+",name=" + busNodeVOS.get(i+1).getSt_nm(),"utf-8"));
                        url.append("&mode=TIME&lang=ko");
                        System.out.println(url.toString());
                        Map<String,String> returnMap = utils.getTakeMinData(url.toString());
                        if(returnMap.size() > 0){
                            busNodeVOS.get(i).setDist_next_st(returnMap.get("distance"));
                            busNodeVOS.get(i).setTime_next_st(returnMap.get("duration"));
                            resultList.add(busNodeVOS.get(i));
                        }else{
                            busNodeVOS.get(i).setDist_next_st("500");
                            busNodeVOS.get(i).setTime_next_st("2");
                            resultList.add(busNodeVOS.get(i));
                        }
                    }else{
                        resultList.add(busNodeVOS.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                index.getAndIncrement();
            });
            if(index.get()%1000 == 0) System.out.println(index.get() +"/"+sBusNodeList.size());
        });


        //  지하철 - 버스 정류장 연결 csv만들기.
        CSVWriter writer = new CSVWriter(new FileWriter("D:/METRO-SEB-NODE-INFO-TOBE.csv"));
        String[] cate = {"bus_route_id","bus_route_nm","ars_id","st_id","st_nm","gps_x","gps_y",
                "pos_x","pos_y","seq","dist_next_st","time_next_st","trn_st_id","city_nm"};
        writer.writeNext(cate);
        resultList.forEach(v -> {
            writer.writeNext(new String[]{v.getBus_route_id(),v.getBus_route_nm(),v.getArs_id(),v.getSt_id(),
                    v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),v.getPos_y(),v.getSeq()
                    ,v.getDist_next_st(),v.getTime_next_st(),v.getTrn_st_id(),v.getCity_nm()});
        });
        writer.close();
        //  중복 정류장 제거를 위해 서울 정류장 리스트에 있는 정류장을 경기도에서 제거
//        List<BusStationVO> seoul = utils.readCsv(BusStationVO.class,"csvs/METRO-SEB-ST-INFO.csv");
//        List<BusStationVO> gdd = utils.readCsv(BusStationVO.class,"csvs/METRO-GGDB-ST-INFO.csv");
//
//        List<BusStationVO> real = gdd.stream()
//                .filter(v -> seoul.stream().noneMatch(x -> v.getSt_id().equals(x.getSt_id()))).collect(Collectors.toList());
//
//        CSVWriter writer = new CSVWriter(new FileWriter("d:/METRO-GGDB-ST-INFO.csv"));
//        String[] cate = {"ars_id","st_id","st_nm","gps_x","gps_y","pos_x","pos_y","trans_yn","city_nm"};
//        writer.writeNext(cate);
//        real.forEach(v -> {
//            writer.writeNext(new String[]{v.getArs_id(),v.getSt_id(),v.getSt_nm(),v.getGps_x(),v.getGps_y(),v.getPos_x(),
//                    v.getPos_y(), v.getTransYn(), v.getCity_nm()});
//        });
//        writer.close();
    }
}
