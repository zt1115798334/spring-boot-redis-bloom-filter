package com.example;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;


/**
 * 非对称加密算法RSA算法组件
 * 非对称算法一般是用来传送对称加密算法的密钥来使用的，相对于DH算法，RSA算法只需要一方构造密钥，不需要
 * 大费周章的构造各自本地的密钥对了。DH算法只能算法非对称算法的底层实现。而RSA算法算法实现起来较为简单
 *
 * @author kongqz
 */
public class RSAUtils {
    //非对称密钥算法
    public static final String KEY_ALGORITHM = "RSA";


    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */

    //公钥
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQgEoj3z9JrdPNI23DbMQkl3gkGuDke7iBr5yrYyqolkTyxuBLWFwHNuGv4VKOj9fXg61QxpaJ/fxDBvMvmkBSRowHBloGFceVTx8wV/8u0DcjvTCu0IZ1zp6wjG6xBn5j66Sg/q+9hvaY2p7fkKmsvcW6VoNPgQHU1Cf01DLZmQIDAQAB+oXcINOiE3AsuZ4VJmwNZg9Y/7fY+OFRS2JAh5YMsrv2qyoGP+Z9ksre26NYR+Lt91B2lhdwJHLpQpziaANZm/ONb31fj/lwIDAQAB";

    //私钥
    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANCASiPfP0mt080jbcNsxCSXeCQa4OR7uIGvnKtjKqiWRPLG4EtYXAc24a/hUo6P19eDrVDGlon9/EMG8y+aQFJGjAcGWgYVx5VPHzBX/y7QNyO9MK7QhnXOnrCMbrEGfmPrpKD+r72G9pjant+Qqay9xbpWg0+BAdTUJ/TUMtmZAgMBAAECgYBSozY/Z4FW+31h5fPgK+DFu/8TGFAgXuTvCaJnz2Md9IkZTDejxT6cYWUr53toI5zhvz/XLw6FXNQ54KxMJq/s9PiZYUgq/PMrnyU4gBSTm5BmiWjdaGicVEZ1lofHjpkAchPNW/CzwxD8AeKI7QaObE+EkWbLAi6sa+nRdHKgrQJBAOwYLD2DncU15XCKS0RNzTrNohdBQcisOPHdtQO0CGZlxx3xjuU4WL6/EpdmbjTeYbOSDKCmY5vyVbYZdOWfEs8CQQDiFIwWpvW2WLxLVw3i2P55WmMMXuecwEzg++ae3Ht7nW0zNcWSsyvHh40sM8XqEzmWOzMY6JOePbkuVfWTc4cXAkBRzf5mQhiEoKwjVofF3v9hhKbJT/8vPR1uENgLtHHEqTdZFL3ihqeZUDNs6jz9bKCFy/E8KOsSueEg+6kZdwjZAkEAj2RW4fstd2VasDJb5ViaNqAEmJENOBej60L6KCJR07qqy0M8t+oaR2iLOtDvo6Jj8QxFQXQqRMCDVodAxjANKwJAL3KuaqA6kdy9RxdV3uP8nRXLY7C/1ZIK6U0pyZqKXEwpD+7Ar3hwwhPz9TeuoqjB/cCknZjw70BQFQ0/VUHW2g==";


    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;


    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 获取公钥
     *
     * @return 公钥密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey() throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(PUBLIC_KEY);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }


    /**
     * 获取私钥
     *
     * @return 私钥密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(PRIVATE_KEY);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //获取公钥
        PublicKey publicKey = getPublicKey();

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //获取公钥
        PrivateKey privateKey = getPrivateKey();

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @return byte[] 解密数据
     */
    public static String decryptByPrivateKey(byte[] data) throws Exception {
        //取得私钥
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = getPrivateKey();
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();

        return new String(decryptedData, "utf-8");
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @return byte[] 解密数据
     */
    public static String decryptByPublicKey(byte[] data) throws Exception {
        //取得私钥
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = getPublicKey();
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();

        return new String(decryptedData, "utf-8");
    }

    public static <T> T objToClass(Object object, Class<T> clazz) {
        T t = clazz.cast(object);
        return t;
    }

    public static void main(String[] args) throws Exception {

        String s = "{\n" +
                "  \"question\" : {\n" +
                "    \"qaPid\" : \"ff80808171775bac0171775f3f620015\",\n" +
                "    \"content\" : \"测试yansw9\", \n" +
                "    \"robotAnswer\" : 0, \n" +
                "    \"avaliableStatus\" : 1, \n" +
                "    \"effectDateStr\" : \"2021-03-04 14:57:57\", \n" +
                "    \"expireDateStr\" : \"2021-03-04 14:57:57\"  \n" +
                "  },\n" +
                "  \"extQuestions\" : [ {\n" +
                "    \"content\" : \"测试追问相似问题1\"  \n" +
                "  }, {\n" +
                "    \"content\" : \"测试追问相似问题2\"  \n" +
                "  } ],\n" +
                "  \"recommendQuestions\" : [ {\n" +
                "    \"reContent\" : \"测试推荐1\" \n" +
                "  }, {\n" +
                "    \"reContent\" : \"测试推荐2\"\n" +
                "  } ],\n" +
                "  \"followAsks\" : [ {\n" +
                "    \"questions\" : \"追问问题1\", \n" +
                "    \"answer\" : \"追问文本答案\",  \n" +
                "    \"targetType\" : 0,   \n" +
                "    \"similarQuestions\" : [ {\n" +
                "     \"content\" : \"测试追问相似问题1\"  \n" +
                "    }, {\n" +
                "    \"content\" : \"测试追问相似问题1\"\n" +
                "    } ]\n" +
                "  }, {\n" +
                "    \"targetQuestionId\" : \"0000000\", \n" +
                "    \"questions\" : \"追问问题1\",  \n" +
                "    \"targetType\" : 1,\n" +
                "    \"similarQuestions\" : [ {\n" +
                "     \"content\" : \"测试追问相似问题1\"  \n" +
                "    }, {\n" +
                "    \"content\" : \"测试追问相似问题1\"\n" +
                "    } ]\n" +
                "  }],\n" +
                "  \"answers\" : [ {\n" +
                "    \"content\" : \"通用答案\",   \n" +
                "    \"channelId\" : 0  \n" +
                "  }, {\n" +
                "    \"content\" : \"web答案\",\n" +
                "    \"channelId\" : 1\n" +
                "  }, {\n" +
                "    \"content\" : \"wap答案\",\n" +
                "    \"channelId\" : 2\n" +
                "  } ]\n" +
                "}";
        String ss = "GWSvrDiHYzBOgbvDOy16rXeK5+5lCo5+t6UX4qkywmwsCEEPkvSEs9N2wNQQ619G+ExgQGr/p0Z6\n" +
                "isb8FME07/PUigcgpIfR7Lq1bcv35TEUrNu1OVIYzIg5VGoFFHEOOWLnNmYcHilX2ydGfaV86StZ\n" +
                "WEy+AZHexP4lX1J4OgwWRWCxC9wyQeyGZByfod7E7/pY2+bwtFk3pkvW5KegXCvHrYDnhSBvYstg\n" +
                "wYhgZoIs3ZgUJVuAvPyebHtm9/VZ4to0kk/IE6s3RGmkle6YPRwlx1L0+25ALlZzj81N8EasA2b0\n" +
                "b0JqoZTwti5nXw9qMsz/6ywipzf3StdAvg30HJUQdiGjHz4zst+fDV/PPVZmUZm421awA7F60/ly\n" +
                "Elg9MbNRAbKKh6EpDeGU4BGzCsnvE7DaFqd8Na7j1uAnGij9DCV4EtSRZpPkcxjUJGPISNe3DQBh\n" +
                "9OHgJ+2xuaz2WvqQ5GeoFn2qX4BWgdExQtETdyzGLMrvqgZmrruiffqVzOJOif2rMlz2i084iPv1\n" +
                "JCRSJkKPa4PEMoJyQoj9/nKVss+AkEN9mEsCMVFo4NtbvNX1NvOM6Q7qjNn4f7BrqLOH/m08IxQT\n" +
                "MkYAoH4Y52pZxWnjQKkqNO2h9uVfV9QKtrwwSATiMtnQBGqMi7KE4C+8v8k5ZH/DduoeTSfbEki7\n" +
                "Cep7trVG/CnQLWU7Onot/gY/0pZet/8+6w11o9hYSFOrLOXKzQcUyWPCloP+/4bki99kzm01pLkk\n" +
                "qSbNIcMbgbP5iUHsdN5kOPG6M2fPZPjDNy5WfJMQzGJRRehSVfPsvlT+7T9rxOETqR7sWnD2mqsB\n" +
                "0jTdoEeXHCu2yf6FzsamClMA4O9K5VTap457Ath0ItuTd9c7fWvJY1PIsntZ8iy0QFCqr/RKZZNg\n" +
                "R/Svv3GYLs8ZpUmpAxdYkMKoZ7IaQQnJeVHqPd6FBEDbgzSXOSJYie7D4QVtGjJlNGf7UYiwdFtz\n" +
                "VL1l5gcxNT5NzsiyhMfaU6xhhS1d12iHGJ1FAk3bcU94fNpjmEY91gvjRa1uz5XB1T1oJ/emKQL9\n" +
                "hCFAXcFt5nYlfXuMC+w/ylmYQ4BLdM5XfgmYAD5BpcXiWhqgr9EVcTzaahOu3mzgpYxdJb+SZIzb\n" +
                "WPLCBBmINCqUxC6i320ozFXbvOFWkikD1isPAV1dm8MknZQsFF+D2j4=";
        String s5 = "DcZv0lQ4t8z8uoGaY7WwuhvDZQ5X8f5FCOyO6moAYH4Ofuwgl9PMeMp63oRQVsb3-ARn76qRSUXyPQrhCD-eoiralfpGSqdqH9cDYZtaBhXXit_qe4MtcunGaM0ms7gvV_E74qY5vNU9gO-8k_5qXAeGUFHIPKLL5OjQ5jFWHgFCGwzRMGEtlmhBikLgHoriTONPyqz0rHybvA4Y-8i90Njq4YK-KAMWAIltj8ZgMhT0QGlD-nPl-n9D3ZiASnp0BKs-NPVAzN0jQUsgpaJHM0r5Qb3oT8k8UY1ueS4XVqRGLxoMv1yz2OrgxsO1OdQQTucFoN87AFap5Jw5Zl3goc9SP2ozvkKBo1yiBZFuSaCmaX0_u4rSk9Fi57e4OSAy50aLrjuczFK3E0HQX-gfDFZK38IoImpE9MuI7jYJBqbZT8zb08pp8wuwE1InvY3kUZvAZ-8UhWfN8L7H5zZMRqP4kAjRL0jaO3srtgBA8Lyv4ygIQR_fR5py_8otcfuDPlEMweDkurZBfyHoxS3Q0BJhCo9tHbRci1u4-Oc-H-e1PV2ws7er5Li8TFp2m1mr6c55GTHPz9kP4lHODf8zOLeubY9Dpmme7oIcFqX0eNCw66BggMxQXuoKCmR1B7OMP_DfChYrnepHUJtuvxye3dwbfMDjmeVe9yR6Ahk1SMDNR2IM3p2-QNBGWI1cGgtAOfiLjVoQ49iaar0Mf7_1c4jQkIXfitvBjhey5Tzmsv_RMkNH7rwaunPCUgtiFhjPEtwBJC6JViGvrNc_jrQG8wpaPmv_oDbPAjisAMWuXX7LXDWNmY655iiAtvfU4_1MhPONbM6uCQawhJbs-ViRHBMp6YxrZk0_fyJ2YGuOnnR6gZFB3nLgw7TRrOqJOizssEMxrPmYsO26pasbrRuPiJF20Xdnt4lzfMWHkNnAMBugy958QrDjgswhV4Qg73chBzJnDSWstkTaRl992-KMRFacB5U4xJRqMVqKBxmywJmEhH_KZ1mO0Ke_k3U3niyxM2jJqWKqEzHIWvtFzW7m43hoyneB1z8HXG5dEW7F06Uf55iuKMVE2YCU6bztat17pKPQRldgmYLtaKqaT8lBRcZ2mQKC_HoTwkmOQ5KlkQc0SYWSQAQRw6Q9LQpTJ5BRV3J0nRXh-LHikFd0mnGLdvYrJD065sg1aJuulLjMhOxRc21Kjv7zZmy6Cl8YRSa396VeWayYwNW2xwspnm7QPU0Aqis5IFWFtR3IGaXdZePd8ue28wcEI1Zti3gSKGgPP9t4dztaEg1YwXyW3adhz_-BWlZ2Y2OTLvTBG3Hqo4u0MdH193p75MZwf-7l-FEnYRTZm8ymHEufXefJKCpuDLaEZ9hKlzeVCaijVzp_5z5ZzfO80Y1C7pkD-5JY40Eqk9ZAhVqO7vkLcxvA6Qg53e9JV9qD-x4ynYXwv1U1cQW1LXGXn05EnzC0qasZTjQKaL6KWTTXM52UBJakOr5IDsCGvJQJpUnH4Mww2plDj5yJSIUtKFj5ASu-QfZYplMCmxODbeCuXhW75agXoL5NuC_bxZpHjQj1suJ24Vs9rS3FHXNZW7OWdSXst8Q_wa2HcmggvcpgycVsg0I-yLkp389FatCYAvWVhMl8DiQjYhNtW8-BDVnou_58_O3eUsGeU4yZRMO2y_fer6IiPiFDd35EJwRSztvVSHL-N1o_8IIxlQavQ4VRKv4J2eXRDsCYRCRUAMVnHDzLMn0TozmzWVjAA9U9GzxCtTkpKM0n_BR6qtFTn5842IMU-gYMid2J_X0OW6mTIPi51dkRRXTKBOFq02a9o6Ez8_jVKwv4qi82lsdS7UAhPZ_QlapF3dosQuKRzq7b0WZnMyOmVae51w==";
//        s = "zkhl_aicc";
        byte[] bytes = encryptByPublicKey(s.getBytes());
//        String s2 = new String(Base64.getUrlEncoder().encode(bytes)) ;
//        System.out.println(s2);
//        byte[] bytes2 = Base64.getUrlDecoder().decode(s2);
//        String s3 = RSAUtils.decryptByPrivateKey(bytes2);
//        System.out.println(s3);

//        byte[] bytes2 = Base64.getDecoder().decode(s5);
//        String s3 = decryptByPrivateKey(bytes2);
//        System.out.println(s3);

//        System.out.println(decryptByPrivateKey(bytes1));
//        String encode = "{\"orgi\":\"ukewo\",\"rankNum\":\"20\",\"keyword\":\"中国\",\"knowledgePointId\":\"54ffd874-df56-44a7-8380-cf8e9bc1a196\"}";
        String encode = "{\"userName\":\"aikm\",\"timeStamp\":\"" + new Date().getTime() + "\",\"orgi\":\"ukewo\"}";
//        String encode = "{\"username\":\"aikm\",\"uname\":\"aikm测试\",\"orgi\":\"ukewo\",\"mobile\":\"19999999999\",\"email\":\"aikm@zkhl.com\",\"roleId\":\"ff808081743317c70174332672080015\"}";
//        String encode = "admin" + "#" +  new Date().getTime() + "#" +"ukewo";
        byte[] bytes1 = encryptByPublicKey(encode.getBytes());
        String s1 = Base64.getEncoder().encodeToString(bytes1);
        String s2 = Base64.getUrlEncoder().encodeToString(bytes1);
        System.out.println(s1);
        System.out.println(s2);
        String dd = "kRl91h986CWONMY-OUBqeF6L-5zZEuun0CIW_iVg85zcxU-ur8E-BQzL3YX3cXVT5G6LdVjTnOLLbXtAmRWdx5MvNUS9ipuCtf-nlBvsa-1VmsvvOHnujrR4FV90ZzeMZ5wsWY3vt9qPqZJyCyz-Ln9ce3ddM-6aN2j_8z8-DKfPq3e0MdP-Qy1vh0ExuHaFbihXF5cy4pcvhvMfdVkp7b-gTedvhTUymfeEUwAgLzSqEl8I7gQSyFj8pdpl7dtGZGa_-ilZWrd7CXw6TQVL1wU0OtGDY34XnR3vqpltzEXO033XFZFYsgX2mSYGmXOJfMj3TY4OjXz5GrMQbYxZZA==";
        String s3 = decryptByPrivateKey(Base64.getUrlDecoder().decode(dd));
        System.out.println("s3 = " + s3);
//        System.out.println(decryptByPublicKey(Base64.getDecoder().decode(s1)));

    }

}