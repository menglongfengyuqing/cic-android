package com.ztmg.cicmorgan.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AndroidUtil {


    /**
     * 获得程序的版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            return 0;
        }

        return versioncode;
    }

    /**
     * 获得程序的版本名
     *
     * @param context
     * @return
     * @author Mark
     */
    public static String getAppVersionName(Context context) {
        String versionName = "0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "0";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }

        return versionName;
    }

    public static void writeBiying(String s) {
        FileWriter fw;
        try {
            String SDPATH = Environment.getExternalStorageDirectory() + "/";
            fw = new FileWriter(SDPATH + "biying.txt", true);
            fw.write(s, 0, s.length());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createBiying() {
        try {
            //String SDPATH = Environment.getExternalStorageDirectory() + "/";
            String SDPATH = Environment.getRootDirectory() + "/";

            File file = new File(SDPATH + "biying.txt");

            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readBiying() {
        File f;
        String line = "";
        try {
            String SDPATH = Environment.getExternalStorageDirectory() + "/";
            f = new File(SDPATH + "biying.txt");
            if (f.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                line = reader.readLine();       // ��ȡ��һ��
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }


    //�ֻ��ͺ�
    public static String getModel() {
        return Build.MODEL;
    }

    //�����绰����
    public static String getNumber(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return phoneMgr.getLine1Number();
    }

    //SDK�汾��
    public static String getSdkVersion() {
        return Build.VERSION.SDK;
    }

    //OS �汾��
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    //ID
    public static String getID() {
        return Build.ID;
    }

    //��ȡIMEI��
    public static String getIEMI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iemi = telephonyManager.getDeviceId();
        return iemi;
    }


    //	System.out.println("DEVICE====="+Build.DEVICE);
    //	System.out.println("DISPLAY====="+Build.DISPLAY);
    //	System.out.println("FINGERPRINT====="+Build.FINGERPRINT);
    //	System.out.println("MANUFACTURER====="+Build.MANUFACTURER);
    //	System.out.println("MODEL====="+Build.MODEL);
    //	System.out.println("PRODUCT====="+Build.PRODUCT);
    //	System.out.println("CPU_ABI====="+Build.CPU_ABI);
    //	System.out.println("BRAND====="+Build.BRAND);
    //	System.out.println("BOARD====="+Build.BOARD);


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 50;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     * ���㾭γ����������
     **/
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    public static double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;

        // �Ƕ�ת��Ϊ����
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;

        // ���Ȳ�
        dew = ew1 - ew2;

        // ���綫��������180 �ȣ����е���
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // �������򳤶�(��γ��Ȧ�ϵ�ͶӰ����)
        dy = DEF_R * (ns1 - ns2); // �ϱ����򳤶�(�ھ���Ȧ�ϵ�ͶӰ����)

        // ���ɶ�����б�߳�
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    public static double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;

        // �Ƕ�ת��Ϊ����
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;

        // ���Բ�ӻ����������еĽ�(����)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);

        // ������[-1..1]��Χ�ڣ��������
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;

        // ���Բ�ӻ�����
        distance = DEF_R * Math.acos(distance);
        return distance;
    }

    //	double mLat1 = 39.90923; // point1γ��
    //	double mLon1 = 116.357428; // point1����
    //	double mLat2 = 39.90923;// point2γ��
    //	double mLon2 = 116.397428;// point2����
    //	double distance = GetShortDistance(mLon1, mLat1, mLon2, mLat2);


    public static void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();

            System.out.println("pubKey:" + pubKey);
            System.out.println("signNumber:" + signNumber);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }


    /**
     * ����״̬��
     *
     * @param act
     * @author Mark
     */
    public static void hideStatusBar(Activity act) {
        //���ر���
        act.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //����ȫ������
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //��ô��ڶ���
        Window window = act.getWindow();
        //����Flag��ʶ
        window.setFlags(flag, flag);

    }

    //流转换成字符串
    public static String stream2String(InputStream is) throws IOException {
        if (is != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int temp = -1;
            while ((temp = is.read(buffer)) != -1) {
                baos.write(buffer, 0, temp);
            }
            is.close();
            baos.close();
            return baos.toString();
        }
        return null;
    }

    //防止按钮连续点击
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
