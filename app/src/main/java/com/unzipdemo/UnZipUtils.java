package com.unzipdemo;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by 王兵兵 on 2018/8/15.
 */

public class UnZipUtils {


//    /**
//     * 解压缩一个文件
//     *
//     * @param zipFile    压缩文件
//     * @param folderPath 解压缩的目标目录
//     * @throws IOException 当解压缩过程出错时抛出
//     */
//
//    public static void upZipFile(File zipFile, String folderPath) throws Exception {
//        File desDir = new File(folderPath);
//        if (!desDir.exists()) {
//            desDir.mkdirs();
//        }
//        ZipFile zf = new ZipFile(zipFile);
//        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
//            ZipEntry entry = ((ZipEntry) entries.nextElement());
//            InputStream in = zf.getInputStream(entry);
//            String str = folderPath;
//            // str = new String(str.getBytes("8859_1"), "GB2312");
//            File desFile = new File(str, java.net.URLEncoder.encode(
//                    "offlinedata.sqlite", "UTF-8"));
//
//            if (!desFile.exists()) {
//                File fileParentDir = desFile.getParentFile();
//                if (!fileParentDir.exists()) {
//                    fileParentDir.mkdirs();
//                }
//            }
//
//            OutputStream out = new FileOutputStream(desFile);
//            byte buffer[] = new byte[1024 * 1024];
//            int realLength = in.read(buffer);
//            while (realLength != -1) {
//                out.write(buffer, 0, realLength);
//                realLength = in.read(buffer);
//            }
//            out.close();
//            in.close();
//        }
//    }


    /**
     * 含子目录的文件压缩
     *
     * @throws Exception
     */
    // 第一个参数就是需要解压的文件，第二个就是解压的目录
    public static boolean upZipFile(String zipFile, String folderPath) throws Exception {
        ZipFile zfile = null;
        // 转码为GBK格式，支持中文
        zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            // 列举的压缩文件里面的各个文件，判断是否为目录
            if (ze.isDirectory()) {
                String dirstr = folderPath + ze.getName();
                Log.e("wbb", "dirstr=" + dirstr);
                dirstr.trim();
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = null;
            FileOutputStream fos = null;
            // ze.getName()会返回 script/start.script这样的，是为了返回实体的File
            File realFile = getRealFileName(folderPath, ze.getName());
            try {
                fos = new FileOutputStream(realFile);
            } catch (FileNotFoundException e) {
                Log.e("wbb", e.getMessage());
                return false;
            }
            os = new BufferedOutputStream(fos);
            InputStream is = null;
            is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            // 进行一些内容复制操作
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        return true;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        Log.e("wbb", "baseDir=" + baseDir + "------absFileName="
                + absFileName);
        absFileName = absFileName.replace("\\", "/");
        Log.e("wbb", "absFileName=" + absFileName);
        String[] dirs = absFileName.split("/");
        Log.e("wbb", "dirs=" + dirs);
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                ret = new File(ret, substr);
            }

            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            ret = new File(ret, substr);
            return ret;
        } else {
            ret = new File(ret, absFileName);
        }
        return ret;
    }

    /**
     *tar.gz解压缩
     */
//    public static void doUnTarGz(File srcfile, String destpath)
//            throws IOException {
//        byte[] buf = new byte[1024];
//        FileInputStream fis = new FileInputStream(srcfile);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        GzipCompressorInputStream cis = new GzipCompressorInputStream(bis);
//        TarArchiveInputStream tais = new TarArchiveInputStream(cis);
//        TarArchiveEntry tae = null;
//        int pro = 0;
//        while ((tae = tais.getNextTarEntry()) != null) {
//            File f = new File(destpath + "/" + tae.getName());
//            if (tae.isDirectory()) {
//                f.mkdirs();
//            } else {
//                /*
//                 * 父目录不存在则创建
//                 */
//                File parent = f.getParentFile();
//                if (!parent.exists()) {
//                    parent.mkdirs();
//                }
//
//                FileOutputStream fos = new FileOutputStream(f);
//                BufferedOutputStream bos = new BufferedOutputStream(fos);
//                int len;
//                while ((len = tais.read(buf)) != -1) {
//                    bos.write(buf, 0, len);
//                }
//                bos.flush();
//                bos.close();
//            }
//        }
//        tais.close();
//    }


    /**
     * 将zip压缩包解压成文件到指定文件夹
     *
     * @param zipFilePath
     * @param targetDirPath
     * @return
     */
    public boolean decompressZip2Files(String zipFilePath, String targetDirPath) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        //zip文件输入流
        ZipArchiveInputStream zipArchiveInputStream = null;
        ArchiveEntry archiveEntry = null;
        try {
            File zipFile = new File(zipFilePath);
            inputStream = new FileInputStream(zipFile);
            zipArchiveInputStream = new ZipArchiveInputStream(inputStream, "UTF-8");

            while (null != (archiveEntry = zipArchiveInputStream.getNextEntry())) {
                //获取文件名
                String archiveEntryFileName = archiveEntry.getName();
                //构造解压后文件的存放路径
                String archiveEntryPath = targetDirPath + archiveEntryFileName;
                //把解压出来的文件写到指定路径
                File entryFile = new File(archiveEntryPath);
                if (!entryFile.exists()) {
                    entryFile.getParentFile().mkdirs();
                }
                byte[] buffer = new byte[1024 * 5];
                outputStream = new FileOutputStream(entryFile);
                int len = -1;
                while ((len = zipArchiveInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }
        } catch (FileNotFoundException e) {
//			e.printStackTrace();
            return false;
        } catch (IOException e) {
//			e.printStackTrace();
            return false;
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
//					e.printStackTrace();
                }
            }
            if (null != zipArchiveInputStream) {
                try {
                    zipArchiveInputStream.close();
                } catch (IOException e) {
//					e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
//					e.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * 7z文件解压解压
     *
     * @param filepath    解压文件路径
     * @param outfilepath 解压后的存放路径
     * @throws Exception
     */
    public static void SevenZFileExtract(String filepath, String outfilepath) throws Exception {
        File tempFile = new File(filepath);//需要解压的文件
        //目标目录
        File targetDir = new File(outfilepath);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        String outFilepath = outfilepath + File.separator + tempFile.getName();
        FileOutputStream outputStream = new FileOutputStream(outFilepath);
        SevenZFile sevenZFile = new SevenZFile(tempFile);
        int offset = 0;
        while (true) {
            //获取下一个文件
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            if (entry == null) {
                break;
            }
            System.out.println("###########name:" + entry.getName());
            byte[] content = new byte[(int) entry.getSize()];
//                sevenZFile.read(content, 0, (int) entry.getSize());
//                            System.out.println("count:"+count);
            int count = 0;
            while ((count = sevenZFile.read(content, 0, (int) entry.getSize())) > 0) {
//                            System.out.println("count:"+count);
                outputStream.write(content, 0, count);
                Log.e("wbb", "count: " + count);
            }
//                outputStream.write(content, 0, sevenZFile.read(content, 0, (int) entry.getSize()));
            //TODO  根据字符串做相应处理
            content = null;

        }
        sevenZFile.close();
    }
}