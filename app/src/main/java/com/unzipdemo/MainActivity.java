package com.unzipdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hzy.lib7z.ExtractCallback;
import com.hzy.lib7z.Z7Extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CompressorStreamFactory
//         开始解压
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Log.e("wbb", "开始： ");
//                    //解压zip并保存数据库
////                    UnZipUtils.upZipFile(Environment.getExternalStorageDirectory().getPath() + "/in_android.zip",
////                            Environment.getExternalStorageDirectory().getPath());
////                    String cmd = Command.getExtractCmd(Environment.getExternalStorageDirectory().getPath() + "/in_android.rar",
////                            Environment.getExternalStorageDirectory().getPath() + "/test-ext");
////                    P7ZipApi.executeCommand(cmd);
//
//                    UnZipUtils.SevenZFileExtract(Environment.getExternalStorageDirectory().getPath() + "/test.7z",
//                            Environment.getExternalStorageDirectory().getPath() + "/test-ext");
//                    Log.e("wbb", "结束： ");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("wbb", "Exception: " + e.getMessage());
//                }
//            }
//        }).start();
//        Log.e("wbb", "getLzmaVersion... " + Z7Extractor.getLzmaVersion());

        FileUtils.getInstance(this).copyAssetsToSD("","")
                .setFileOperateCallback(new FileUtils.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Z7Extractor.extractFile(Environment.getExternalStorageDirectory().getPath() + "/in_android.7z",
                                        Environment.getExternalStorageDirectory().getPath()+"/text_7z", new ExtractCallback() {
                                            @Override
                                            public void onProgress(String name, long size) {
                                                Log.e("wbb", "解压中... " + size);
                                            }

                                            @Override
                                            public void onError(int errorCode, String message) {
                                                Log.e("wbb", "解压失败： " + message);
                                                String s = null;
                                                s.length();
                                            }

                                            @Override
                                            public void onSucceed() {
                                                super.onSucceed();
                                                Log.e("wbb", "解压完成... " );
                                            }
                                        });
                            }
                        }).start();
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.e("wbb", "copyAssetsToSD失败： " + error);
                        String s = null;
                        s.length();
                    }
                });



    }

    /**
     * 解压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os)
            throws Exception {
        GZIPInputStream gis = new GZIPInputStream(is);
        int count;
        byte data[] = new byte[1024];
        while ((count = gis.read(data, 0, 1024)) != -1) {
            os.write(data, 0, count);
        }

        gis.close();
    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 解压缩

        decompress(bais, baos);

        data = baos.toByteArray();

        baos.flush();
        baos.close();

        bais.close();

        return data;
    }
}
