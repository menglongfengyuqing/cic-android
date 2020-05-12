package com.ztmg.cicmorgan.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;

import com.loopj.android.http.MySSLSocketFactory;
import com.ztmg.cicmorgan.R;

public class SSLSocketFactory {
	
	public  static  MySSLSocketFactory getSocketFactory(Context context) {  
        // TODO Auto-generated method stub  
        MySSLSocketFactory sslFactory = null;  
        try {  
            KeyStore keyStore = KeyStore.getInstance("PKCS12");  
            InputStream instream = context.getResources().openRawResource(  
                    R.raw.cicmorgan);//后台拿到的.p12证书  
            keyStore.load(instream, "cicmorgan@123".toCharArray());  
            sslFactory = new MySSLSocketFactory(keyStore);  
        } catch (KeyStoreException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (NoSuchAlgorithmException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (CertificateException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (IOException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (UnrecoverableKeyException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (KeyManagementException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return sslFactory;  
    }  
}
