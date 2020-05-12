
package com.ztmg.cicmorgan.pay.util;

public class EnvConstants {
    private EnvConstants() {
    }

    /**
     * TODO 商户号，商户MD5 key 配置。本测试Demo里的“PARTNER”；强烈建议将私钥配置到服务器上，以免泄露。“MD5_KEY”字段均为测试字段。正式接入需要填写商户自己的字段
     */
    public static final String PARTNER_PREAUTH = "201504071000272504"; // 短信

    public static final String MD5_KEY_PREAUTH = "201504071000272504_test_20150417";
    
//    public static final String PARTNER = "201408071000001546";
//    public static final String PARTNER = "201408071000001543";
//    public static final String PARTNER = "201506191000378505";
    public static final String PARTNER = "201506191000378505";

//    public static final String MD5_KEY = "201408071000001546_test_20140815";
//    public static final String MD5_KEY = "201408071000001543test_20140812";
    public static final String MD5_KEY = "md5key_201506191000378505_20150707";

    // 商户（RSA）私钥 TODO 强烈建议将私钥配置到服务器上，否则有安全隐患
    // public static final String RSA_PRIVATE =
    // "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOilN4tR7HpNYvSBra/DzebemoAiGtGeaxa+qebx/O2YAdUFPI+xTKTX2ETyqSzGfbxXpmSax7tXOdoa3uyaFnhKRGRvLdq1kTSTu7q5s6gTryxVH2m62Py8Pw0sKcuuV0CxtxkrxUzGQN+QSxf+TyNAv5rYi/ayvsDgWdB3cRqbAgMBAAECgYEAj02d/jqTcO6UQspSY484GLsL7luTq4Vqr5L4cyKiSvQ0RLQ6DsUG0g+Gz0muPb9ymf5fp17UIyjioN+ma5WquncHGm6ElIuRv2jYbGOnl9q2cMyNsAZCiSWfR++op+6UZbzpoNDiYzeKbNUz6L1fJjzCt52w/RbkDncJd2mVDRkCQQD/Uz3QnrWfCeWmBbsAZVoM57n01k7hyLWmDMYoKh8vnzKjrWScDkaQ6qGTbPVL3x0EBoxgb/smnT6/A5XyB9bvAkEA6UKhP1KLi/ImaLFUgLvEvmbUrpzY2I1+jgdsoj9Bm4a8K+KROsnNAIvRsKNgJPWd64uuQntUFPKkcyfBV1MXFQJBAJGs3Mf6xYVIEE75VgiTyx0x2VdoLvmDmqBzCVxBLCnvmuToOU8QlhJ4zFdhA1OWqOdzFQSw34rYjMRPN24wKuECQEqpYhVzpWkA9BxUjli6QUo0feT6HUqLV7O8WqBAIQ7X/IkLdzLa/vwqxM6GLLMHzylixz9OXGZsGAkn83GxDdUCQA9+pQOitY0WranUHeZFKWAHZszSjtbe6wDAdiKdXCfig0/rOdxAODCbQrQs7PYy1ed8DuVQlHPwRGtokVGHATU=";
//    public static final String RSA_PRIVATE =
//            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJJL8OLB0J/9pmzHFxpwOeigamHd3Yk6PkZdaL6reDOdlq5mOQ0/xIqXcnaWI/Q7qtT9j/b34hR74ZMyEw4Um5mbWG0C0qK7l6RbQaUExbF/gU+RiVCQ8TQW1qgw/eBh+H47Aj58hGulbfJKfeZJydzpnvTSdT9VitGR9xIJtKdHAgMBAAECgYBMmbzATnE5RGu+qyP6sOZxWoU5Rx03PCrdVw2AQHIIvKvoFxgqSshTNOc3Fngu6osRSM73pmVXCmJbWy3FAp9Rqg2FZfQoX+ds4cnj3QVpeILw6b2Sr0rI2OBkbXGFre/crM+JcjYBAkV7pnwcWRH3EyOvzLUqKs5qEkOycxTi8QJBAOUFVS8ipCnp7Qaynig6PcfJC0JP4GxpFmQu0w1OrmlzP/zezUfRwihTx1NPssJm9HD7KNiBDlgFj0PQJkGbB18CQQCjh90kBAoloAsCxe/qD4w7lbre75P16Kicb+K0FCeJsZrdXpApFhlDo60zPNUJEPph9HFptZfNBE8I8dIesHEZAkEAxe4V8Oa/ennxoBg/GAU936yhTm46R3eLIopVXOrjUb+JTcJBKBDg/Hlri1UV6W2RVRO7+WGQRAKKDtGWPpz9gQJAImZAFIVtBQEnj8vHbfsbSqVyi9blzwLEBTRcAfmDX6mmpA5yUNI/OkVB99dCEQgrQ1PCT7RNXGkdnwoPYzlGcQJBAJQQrWM8SxovyqcN7Md2wRvIjA1Ny7OJGSR8y+0eu/D0GydQbUj1rNdPX5CLNFVwvcgMwkLNUD+u+JSol5+PQHk=";
    public static final String RSA_PRIVATE ="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK7BZn69Ro3Yn4rryCX2G2MahhlXIYANVpzqU+4JwpCQEnkfwWlzIql9+Hx4BertmfuFPTtC5EBYuO6FzrUlQ7eRnh3u1QUK+8sTfyeHEwUW+I5PoZRyIeLTybrAjBYtD0K2qs+wTElw+gbrmzKi1T9s8mVm0ej+jjOWU3MgQ12fAgMBAAECgYAMxGxsjf99lXnyeE9jFpvhnap2Z3s3E8JkLGp9VZE1ZpEy8DL9NUAvCdkBnvMvurQpQA69KaHrYZABPR1g9pv/ylu65bruj1/MHmNJxPd+D1w12cp0OZARwjE+uQVlBf1WdRPsJeHAI23pUAZruKwweClZM/5C212h3b2sRLqcYQJBAN+KozrPl+WHTqRKafz7wR1d3JvxwiD+wpqj0kdFdnJm8VZsrE7xhQ3+0jcFHf9IJI/dEQUEf2aauore/oOAFG0CQQDIIVBHfghKisFlnRl9MPtSPPSJHFvVSrpNF1DvHGH0Ro5pvVL59G4if1Yt3RkttvZF/szujmIFbOJNIxQHivq7AkBMiaRf04o3jX9wowXtF6nES0nonvsP3wp0dhjeTDtE6lhBdg39LZaX9yK0sezWZjYWh+N261ZPpDSwra9JCQZ1AkEAiD5lsMYUTur3jn3NBHV8IxhsQYuU2TfubN0atC3WYb5G5aWF/7Rv5fxaZw7GPWD5d91nFmDISnk99tWSpS/wUQJBAIpHlxAib9BpNr/TmujTPbV2EAjN3NrRtujeWrKGz6XXe6xToCFSHA4StdZ4I/IMlqW7aCF1JQr8Z+Rs41zLXuU=";
    
    // 银通支付（RSA）公钥
//    public static final String RSA_YT_PUBLIC =
//            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";
    public static final String RSA_YT_PUBLIC =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";
    
}
