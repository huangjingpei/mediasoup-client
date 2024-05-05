package org.mediasoup.droid.demo.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by liwenfei on 09/08/2017.
 */

public class FilePath {
    private static FilePath _instance = new FilePath();
    public static FilePath Instance(){
        return _instance;
    }
    FilePath(){}
    String rootPath = "";
    String tempPathKey = "temp";
    String jsonPathKey = "json";
    String keyPathKey = "key";
    //    String videoKey = "video";
//    String musicKey = "music";
    String gamePathKey = "game";
    String mediaPathKey = "media";
    String imgPathKey = "image";
    String modelPathKey = "model";
    String resPathKey = "res";
    String crashPathKey = "crash";
    String scriptsPathKey = "scripts";

    public static final String md5Key = "md5";
    public static final String checkFileKey = "available.check";

    String arKanKanId="";

    public void initRootPath(String path){
        rootPath = path;
        createDirFilePath(path);
        createDirFilePath(getGamePath());
        createDirFilePath(getImgPath());
        createDirFilePath(getMediaPath());
        createDirFilePath(getJsonPath());
        createDirFilePath(getCrashInfoPath());
        createDirFilePath(getKeyPath());

    }


    public static void createDirFilePath(String dir) {
        FileUtil.createDirFile(dir);
    }


    public String getRootPath(){
        return rootPath;
    }

    public String getCrashInfoPath(){return rootPath + File.separator + crashPathKey + File.separator;}
    public String getJsonPath(){
        return rootPath + File.separator + jsonPathKey+File.separator;
    }

//    public String getVideoPath(){
//        return rootPath+ File.separator + videoKey+File.separator;
//    }
//
//    public String getMusicPath(){return rootPath+ File.separator+musicKey+File.separator;}

    public String getMediaPath(){
        return getGamePath()+mediaPathKey+File.separator;
    }
    public String getKeyPath(){
        return rootPath + File.separator + keyPathKey+File.separator;
    }

    public String getGamePath(){return rootPath+File.separator+gamePathKey+File.separator;}

    public String getImgPath(){return getGameRootPath()+imgPathKey+File.separator;}

    public String getModelPath(){
        return getGameRootPath()+modelPathKey+File.separator;
    }
    public String getResPath(){
        return getGameRootPath()+resPathKey+File.separator;
    }
    public String getRootResPath(){
        return getGamePath()+resPathKey+File.separator;
    }
    public String getTempPath(){
        return rootPath+File.separator+tempPathKey+File.separator;
    }



    public  void createGameDirById(String id) {
        if (TextUtils.isEmpty(id)){
//            createGameDir();
            throw new NullPointerException(" id is null");
        }
//        arKanKanId=id;
        createDirFilePath(FilePath.Instance().getGamePath()+id);
        createDirFilePath(getImgPath());
        createDirFilePath(getModelPath());
        createDirFilePath(getResPath());
        createDirFilePath(getTempPath());
    }

    public void setKanKanID(String id){
        arKanKanId = id;
        Log.d("tracked", "arKanKanId=" + arKanKanId);
    }

    private String getGamePathById() {
        return getGamePath()+arKanKanId+File.separator;
    }

    public String getGameRootPath(){
        if (TextUtils.isEmpty(arKanKanId))return getGamePath();
        return getGamePathById();
    }
    public void createGameDir() {
        createDirFilePath(getImgPath());
        createDirFilePath(getModelPath());
        createDirFilePath(getResPath());
    }

    public void release() {
        arKanKanId="";
    }
}
