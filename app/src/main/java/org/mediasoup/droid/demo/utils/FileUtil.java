package org.mediasoup.droid.demo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by li on 2016/9/3.
 */

public class FileUtil {
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.getAbsolutePath();
	}

	public static void copyBigData2SD(Context context, String strOutFileName,
			String strAssetFileName) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(strOutFileName);
		myInput = context.getAssets().open(strAssetFileName);
		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}
		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

    public static void clearDir(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return;
                }
                for (File f : childFile) {
                    clearDir(f);
                }
            }
        }
    }


    public static void copyAssetsFileToSDCard(Context context, String sourceFile, String destinationFile, boolean needRefresh) {
        if (!needRefresh) {
            File destination = new File(destinationFile);
            if (destination.exists()) return;
        }
        try {
            InputStream inputStream = context.getAssets().open(sourceFile);
            OutputStream outputStream = new FileOutputStream(destinationFile);
            // Transfer bytes from in to out
            byte[] buf = new byte[2048];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean CopyAssets2SDcard(Context context, String assetDir, String dir, boolean needRefresh) {
        String[] files;

        try {
            files = context.getResources().getAssets().list(assetDir);
            File mWorkingPath = new File(dir);

            if (!mWorkingPath.exists()) {
                mWorkingPath.mkdir();
            }
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i];
//                if ("lua".equals(fileName)&&!isScriptDebug(context))continue;
                // we make sure file name not contains '.' to be a folder.
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets2SDcard(context, fileName, dir + fileName + "/", needRefresh);
                    } else {
                        CopyAssets2SDcard(context, assetDir + "/" + fileName, dir + fileName + "/", needRefresh);
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    if (needRefresh) {
                        outFile.delete();
                    } else {
                        continue;
                    }
                }
                InputStream in = null;
                if (0 != assetDir.length()) {
                    in = context.getAssets().open(assetDir + "/" + fileName);
                } else {
                    in = context.getAssets().open(fileName);
                }
                OutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[2048];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            }
        } catch (IOException e1) {
            return false;
        }
        return true;
    }

    public static boolean isScriptDebug(Context context) {
        String json= FileUtil.getHiARDebugFromAssets(context, "HiARDebugAssets/HiARDebug.config");
        if (TextUtils.isEmpty(json))return false;
        try {
            String scriptDebug = new JSONObject(json).getString("ScriptDebug");
            if ("1".equals(scriptDebug))return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String basePath(Context context){
        if (!isScriptDebug(context)){
            return "baseAssets/game";
        }else{
            return "HiARDebugAssets/game";
        }

    }

    public static void copyDirsFiles(String dir,String destination){
        if (!destination.endsWith(File.separator))
            destination+=File.separator;
            File[] files;
        files=new File(dir).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                createDirFile(destination + file.getName());
                copyDirsFiles(file.getAbsolutePath(), destination + file.getName());
            } else if (file.isFile()) {
                copyFile(file.toString(),
                        destination + file.toString()
                                .substring(file.toString().lastIndexOf(File.separator)+1,
                                        file.toString().length()));
            }
        }
    }

    public static void copyFile(String filePath,String destination) {
        try{
            if (FileUtil.exists(destination))return;
            InputStream inputStream = new FileInputStream(new File(filePath));
            OutputStream outputStream = new FileOutputStream(new File(destination));
            byte[] buf = new byte[4096];
            int len;
            while((len = inputStream.read(buf))>0){
                outputStream.write(buf,0,len);
            }
            inputStream.close();
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void saveByte(byte[] data, String file) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(String filename, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    final static int BUFFER_SIZE = 4096;
    /**
     * 将InputStream转换成某种字符编码的String
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in,String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),encoding);
    }

    public static void saveStringToSDCard(String path, String content) {
        Log.d("jucf","path="+path);
        FileOutputStream fos = null;
        try{
            File file = new File(path);
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePathAndName
     *            String 如 c:\\1.txt 绝对路径
     * @return boolean
     */
    public static String readFile(String filePathAndName) {
        StringBuilder fileContent = new StringBuilder();
        try {
            File f = new File(filePathAndName);
            if (f.isFile() && f.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                read.close();
            }
        } catch (Exception e) {
            System.out.println("读取文件内容操作出错");
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    private final static int BUFF_SIZE = 2048;
    /**
     * 解压文件
     *
     * @param destination 解压后的目录
     * @param filePath   压缩文件目录
     * @return 成功返回 true，否则 false
     */
    public static boolean unZipFile(String destination, String filePath) {
        destination = createSeparator(destination);
        BufferedOutputStream bos = null;
        ZipInputStream zis = null;

        boolean result = false;

        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            ZipEntry ze;
            byte[] buffer = new byte[BUFF_SIZE];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                createSubFolders(filename, destination);
                if (ze.isDirectory()) {
                    File fmd = new File(destination + filename);
                    fmd.mkdirs();
                    continue;
                }

                bos = new BufferedOutputStream(new FileOutputStream(destination + filename));
                while ((count = zis.read(buffer)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
                bos.close();
            }
            result = true;
        } catch (IOException e) {
            Log.e("upzip","error: "+e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.closeEntry();
                    zis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static void createSubFolders(String filename, String path) {
        String[] subFolders = filename.split("/");
        if (subFolders.length <= 1) {
            return;
        }

        String pathNow = path;
        for (int i = 0; i < subFolders.length - 1; ++i) {
            pathNow = pathNow + subFolders[i] + "/";
            File fmd = new File(pathNow);
            if (fmd.exists()) {
                continue;
            }
            fmd.mkdirs();
        }
    }

    private static String createSeparator(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (path.endsWith("/")) {
            return path;
        }
        return path + '/';
    }

    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(FilePath.Instance().getImgPath(), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        // 最后通知图库更新
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return file.getAbsolutePath();
    }

    public static String saveImageBytes2imageFile(Context context,byte[] imageBytes){
        //将字节数组
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return saveImageToGallery(context,bitmap);
    }

    public static void deleteFiles(String path){
        File file = new File(path);
        if(!file.exists())return;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f: files){
                deleteFiles(f.getAbsolutePath());
            }
            file.delete();
        }else{
            file.delete();
        }
    }

    public static void transForm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse("2018-03-01");
            long longDate = date.getTime();
            long time = System.currentTimeMillis();
            //LogUtil.Logi(""+longDate+"time "+time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存截图
     * @param path 保存路径（不包含名称）
     * @param context
     * @param bmp
     * @return
     */
    public static String saveScreenShot(String path,Context context, Bitmap bmp){
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(path, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        Uri uri = Uri.fromFile(file);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        return file.getAbsolutePath();
    }

    /**
     * 删除文件并且从图库中删除
     * @param context
     * @param filePath
     */
    public static void deleteFileFromGallry(Context context,String filePath){
        deleteFile(filePath);
        Uri uri = Uri.fromFile(new File(filePath));
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    /**
     * 删除文件
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        Log.d("jucf","deleteFile(String filePath)"+filePath);
        File file = new File(filePath);
        if(!file.isDirectory() && file.exists()){
            file.delete();

        }
    }

    public static void createNewFile(String newFile) {
        try {
            File file = new File(newFile);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirFile(String dir){
        Assert.assertNotNull("dir is null or \"\"!",dir);
        Assert.assertNotSame("dir is null or \"\"!","",dir);

        File dirFile = new File(dir);
        if(!dirFile.exists()){
            boolean result=dirFile.mkdirs();
            if (!result)
                throw new RuntimeException("#############创建目录失败 dir="+dir);
        }
    }

    public static boolean exists(String filePath) {
        return new File(filePath).exists();

    }

    public static String getHiARDebugFromAssets(Context context,String sourceFile) {
        try {
            InputStream is = context.getAssets().open(sourceFile);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 删除文件夹内部文件
     * @param file
     */
    public static void deleteSubFile(File file) {
        if(file.isDirectory()){
            File[] subFiles = file.listFiles();
            for(int i=0;i<subFiles.length;i++){
                File subFile = subFiles[i];
                subFile.delete();
            }
        }
    }
}
