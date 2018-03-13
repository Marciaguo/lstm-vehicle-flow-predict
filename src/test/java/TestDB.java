import com.udgrp.utils.DBUtil;

import java.util.List;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class TestDB {

    public static void main(String[] args) {
        //List<String[]> list = DBUtil.readData("", "", "4412-64-19");
        List<String[]> l = DBUtil.getStationIds();
        for (String[] arr : l) {
            System.out.println(arr[0]);
        }
    }
}