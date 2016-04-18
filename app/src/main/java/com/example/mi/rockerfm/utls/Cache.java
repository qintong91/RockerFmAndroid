package com.example.mi.rockerfm.utls;

import android.content.Context;

import com.example.mi.rockerfm.JsonBeans.Articals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by qintong on 16-4-18.
 */
public class Cache {

    private static File mBuffDir;
    private static final String MAIN_ARTICAL_LIST = "Main_Artical_List";
    public static void initialize(Context context) {
        mBuffDir = context.getCacheDir() ;
    }
    public static void putArticalList(Articals articals) {
        ObjectOutputStream oos = null;
        try {
            // 打开文件
            File file = new File(mBuffDir, MAIN_ARTICAL_LIST);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            // 将数据写入文件
            oos = new ObjectOutputStream(fos);
            oos.writeObject(articals);

            // 释放资源
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    public static Articals getArticalList(){
        ObjectInputStream in = null;
        try {
            File file = new File(mBuffDir, MAIN_ARTICAL_LIST);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fileIn = new FileInputStream(file);
            in = new ObjectInputStream(fileIn);
            return (Articals) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }

    }
}
