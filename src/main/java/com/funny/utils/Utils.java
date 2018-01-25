package com.funny.utils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Formatter;

public class Utils {

    public static boolean isEmpty(Object obj) {
        return obj == null || "".equals(obj) || "null".equals(obj);
    }


    public static String getSha1(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));

            // byteToHex
            byte[] hash = crypt.digest();
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String result = formatter.toString();
            formatter.close();

            sha1 = result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    /**
     * 先将接口返回的字符串编码成ISO-8859-1的字节流，然后再以UTF-8解码成字符串
     *
     * @param result 微信接口接口返回字符串
     * @return 正常显示中文的字符串
     */
    public static String invalidWeixinReturn(String result) {
        try {
            return new String(result.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
        }
        return null;
    }

    public static String unInvalidWeixinReturn(String result) {
        try {
            return new String(result.getBytes("UTF-8"), "ISO-8859-1");
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 根据域名计算图片大小
     *
     * @param imagePath 图片url
     * @return
     */
    public static int imageSize(String imagePath) {
        try {
            URL url = new URL(imagePath);
            URLConnection uc = url.openConnection();
            int fileSize = uc.getContentLength() / 1024;

            return fileSize;
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 获取图片后缀名
     *
     * @param fileName 文件名
     * @return 后缀名， png， jpg
     */
    public static String getSuffix(String fileName) {
        try {
            if (fileName == null) {
                return null;
            }
            int start1 = fileName.lastIndexOf("/");
            int start2 = fileName.lastIndexOf("\\");
            int start3 = fileName.lastIndexOf("\\\\");
            start1 = start1 > start2 ? start1 > start3 ? start1 : start3 : start2 > start3 ? start2 : start3;

            fileName = fileName.substring(start1, fileName.length());
            if (fileName.lastIndexOf(".") > 0) {
                return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static File saveUrlAs(String fileUrl, String savePath)/* fileUrl网络资源地址 */ {
        try {
            // 如果不存在目录则创建
            if (!new File(savePath).exists()) {
                new File(savePath).mkdir();
            }

            /* 将网络资源地址传给,即赋值给url */
            URL url = new URL(fileUrl);

			/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            DataInputStream in = new DataInputStream(connection.getInputStream());


            savePath += "/" + System.currentTimeMillis() + "." + getSuffix(fileUrl);

			/* 此处也可用BufferedInputStream与BufferedOutputStream  需要保存的路径*/
            DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));

			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
            byte[] buffer = new byte[4096];
            int count = 0;
            while ((count = in.read(buffer)) > 0)/* 将输入流以字节的形式读取并写入buffer中 */ {
                out.write(buffer, 0, count);
            }

            out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
            in.close();
            connection.disconnect();

            return new File(savePath);/* 网络资源截取并存储本地成功返回true */
        } catch (Exception e) {
            System.out.println(e + fileUrl + savePath);
            return null;
        }
    }


    public static String subStr(String str, int maxLen) {
        if (str == null) {
            return str;
        }

        if (str.length() > maxLen) {
            return str.substring(0, maxLen - 3) + "...";
        } else {
            return str;
        }
    }


    public static void main(String[] args) {

        File f = new File("http://s1.dwstatic.com/group1/M00/94/F9/2fc141dc28ce97c3329564841c283bf8.png");
        System.out.println(f.length());
        saveUrlAs("http://s1.dwstatic.com/group1/M00/94/F9/2fc141dc28ce97c3329564841c283bf8.png", "temp/2fc141dc28ce97c3329564841c283bf8.png");
    }
}
