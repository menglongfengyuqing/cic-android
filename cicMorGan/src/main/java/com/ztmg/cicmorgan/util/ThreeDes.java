/**
 * gigold Inc.  吉高支付 湖南宝伦天地信息科技有限公司
 * Copyright (c) 2015-2016 All Rights Reserved.
 */
package com.ztmg.cicmorgan.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 3DES辅助类
 *
 * @author chenbo
 * @version $Id: ThreeDes.java,v 0.1 2016年1月29日 上午10:21:50 chenbo$Exp
 */

public class ThreeDes {

    /**
     * DES,DESede,Blowfish 定义加密算法,可用
     */
    private static final String Algorithm = "DESede";

    /**
     * 填充方式
     */
    private static final String deAlgorithm = "DESede/ECB/NoPadding";

    /**
     * @param keybyte
     * @param src
     * @return
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            src = buildBodyBytes(src);
            // 填充Key
            keybyte = build3DesKey(keybyte);
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, 0, 24, Algorithm);

            // 加密
            Cipher c1 = Cipher.getInstance(deAlgorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);// 在单一方面的加密或解密
        } catch (Exception e3) {
            e3.printStackTrace();
        }

        return null;
    }

    /**
     * @param keybyte
     * @param src
     * @return
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        byte[] busDt = null;
        try {
            // 填充Key
            keybyte = build3DesKey(keybyte);
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, 0, 24, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(deAlgorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            busDt = c1.doFinal(src);
        } catch (Exception e3) {
            e3.printStackTrace();
            //			throw new AbortException(MsgcdConstant.POS_DECODE_ERROR,
            //					"POS数据解密失败", e3);
        }
        return busDt;
    }

    public static byte[] build3DesKey(byte[] temp)

            throws Exception {

        byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0

        System.arraycopy(temp, 0, key, 0, temp.length);

        // 补充的8字节就是16字节密钥的前8位

        for (int i = 0; i < 8; i++) {

            key[16 + i] = temp[i];

        }
        return key;
    }

    public static byte[] buildBodyBytes(byte[] body) {
        int len = body.length % 8 == 0 ? 0 : 8 - (body.length % 8);
        if (len > 0) {
            byte[] addByte = new byte[len];
            try {
                for (int i = 0; i < addByte.length; i++) {
                    addByte[i] = 0x00;
                }
                body = ByteUtil.contactArray(body, addByte);
            } catch (Exception ex) {
                ex.printStackTrace();
                //				throw new AbortException(MsgcdConstant.POS_DECODE_ERROR,
                //						"POS数据补充", ex);
            }
        }
        return body;
    }

    public static void main(String[] args) {
        String key = "4C359AE7BDF7B96E2A1F91458D414949";
        //String content = "0000000000063069";
        //byte[] password = encryptMode(key.getBytes(), content.getBytes());
        byte[] password = encryptMode(ByteUtil.hexStringToBytes(key), ByteUtil.hexStringToBytes("0000000000063069"));
        //byte[] pass = decryptMode(key.getBytes(), password);
        //System.out.println(pass.length);
        System.out.println(ByteUtil.toHexString(password));
    }

}
