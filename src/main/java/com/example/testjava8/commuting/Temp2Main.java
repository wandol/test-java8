package com.example.testjava8.commuting;

import com.example.testjava8.commuting.bus.NearByVO;
import com.example.testjava8.commuting.bus.Temp2VO;
import com.example.testjava8.commuting.subway.SubwayVO;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Temp2Main {
    /**
     *  지하철 좌표로 근처 버스정류장 구하기.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        CommUtils utils = new CommUtils();

        //  좌표로 근처 정류장 구하기 ( 근처 거리 1000m )
        //  http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?serviceKey=%2F1T27kausQ5i6QmZz2F97BDjYCtKt%2B2JxO7achdmwnishpW%2BbbacYh69htyNR%2FaHNbvZRO8m70YINbyaOMk8ww%3D%3D&tmX=126.7852322&tmY=37.6456384&radius=500
        //  수도권 지하철역 데이터 가져오기
        List<SubwayVO> subwayAllData = utils.readCsv(SubwayVO.class, Constants.METRO_SUBWAY_ALL_INFO_CSV_PATH);

        List<Temp2VO> result = new LinkedList<>();
        List<String> ss = new LinkedList<>();
        subwayAllData.forEach(vo -> {
            StringBuffer url = new StringBuffer();
            url.append("http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?serviceKey="+Constants.SEOUL_BUS_API_KEY);
            url.append("&tmX=").append(vo.getXCon());
            url.append("&tmY=").append(vo.getYCon());
            url.append("&radius=").append("500");
            System.out.println(url.toString());
            try {
                LinkedList<NearByVO> temp = utils.getListVO(NearByVO.class, url.toString(), "itemList");
                System.out.println(temp.toString());
                if(temp.size() > 0){
                    temp.forEach(v -> {
                        Temp2VO voo = new Temp2VO();
                        voo.setArsId(v.getArsId());
                        voo.setStationId(v.getStationId());
                        voo.setDist(v.getDist());
                        voo.setTakeMin(utils.makeBusTakeMinBYDistinct(v.getDist()));
                        voo.setSubwayLine(vo.getSubwayLine());
                        voo.setSubwayNm(vo.getSubwayNm());
                        result.add(voo);
                    });
                }else{
                    ss.add(vo.getSubwayNm());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //  지하철 - 버스 정류장 연결 csv만들기.
        CSVWriter writer = new CSVWriter(new FileWriter("d:/subwayBus.csv"));
        String[] cate = {"arsId","dist","takeMin","stationId","subwayLine","subwayNm"};
        writer.writeNext(cate);
        result.forEach(v -> {
            writer.writeNext(new String[]{v.getArsId(),v.getDist(),v.getTakeMin(),v.getStationId(),v.getSubwayLine(),
                    v.getSubwayNm()});
        });
        writer.close();
        ss.forEach(System.out::println);
    }
}
