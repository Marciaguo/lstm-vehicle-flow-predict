package com.udgrp.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-spring-flow-predict
 * @Description: TODO
 * @date 2018/1/29
 */
public class FileUtil {
    public static File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.deleteOnExit();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
