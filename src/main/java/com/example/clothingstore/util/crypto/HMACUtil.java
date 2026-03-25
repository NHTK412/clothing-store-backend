/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.clothingstore.util.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class để tạo HMAC (Hash-based Message Authentication Code)
 * HMAC là một kỹ thuật mã hóa kết hợp giữa hàm băm (hash) và khóa bí mật
 * Mục đích: Đảm bảo tính toàn vẹn và xác thực dữ liệu
 */
public class HMACUtil {

    // @formatter:off
    /**
     * Các thuật toán HMAC được hỗ trợ
     */
    public final static String HMACMD5 = "HmacMD5";        // HMAC với MD5 (không khuyến khích)
    public final static String HMACSHA1 = "HmacSHA1";      // HMAC với SHA-1
    public final static String HMACSHA256 = "HmacSHA256";  // HMAC với SHA-256 (được ZaloPay sử dụng)
    public final static String HMACSHA512 = "HmacSHA512";  // HMAC với SHA-512
    public final static Charset UTF8CHARSET = Charset.forName("UTF-8");

    public final static LinkedList<String> HMACS = new LinkedList<String>(Arrays.asList("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512"));
    // @formatter:on

    /**
     * Mã hóa dữ liệu bằng HMAC và trả về dạng byte array
     * 
     * @param algorithm - Thuật toán HMAC (ví dụ: HmacSHA256)
     * @param key       - Khóa bí mật để mã hóa
     * @param data      - Dữ liệu cần mã hóa
     * @return Mảng byte chứa kết quả HMAC
     */
    private static byte[] HMacEncode(final String algorithm, final String key, final String data) {
        Mac macGenerator = null;
        try {
            // Khởi tạo Mac generator với thuật toán được chỉ định
            macGenerator = Mac.getInstance(algorithm);
            // Tạo khóa bí mật từ string key
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
            // Khởi tạo Mac với khóa bí mật
            macGenerator.init(signingKey);
        } catch (Exception ex) {
        }

        if (macGenerator == null) {
            return null;
        }

        byte[] dataByte = null;
        try {
            // Chuyển đổi dữ liệu thành byte array
            dataByte = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        // Tính toán HMAC và trả về kết quả dạng byte array
        return macGenerator.doFinal(dataByte);
    }

    /**
     * Tính HMAC và trả về kết quả dạng Base64
     * 
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented base64-encoded string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     *
     * @param key       A secret cryptographic key
     *
     * @param data      The message to be authenticated
     *
     * @return Base64-encoded HMAC String
     */
    public static String HMacBase64Encode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        // Encode kết quả HMAC thành Base64 string
        return Base64.getEncoder().encodeToString(hmacEncodeBytes);
    }

    /**
     * Tính HMAC và trả về kết quả dạng Hex string
     * ĐÂY LÀ PHƯƠNG THỨC ĐƯỢC ZALOPAY SỬ DỤNG
     * 
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented hex string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     *
     * @param key       A secret cryptographic key
     *
     * @param data      The message to be authenticated
     *
     * @return Hex HMAC String (ví dụ: "a3f5c8d2e1...")
     */
    public static String HMacHexStringEncode(final String algorithm, final String key, final String data) {
        // Tính HMAC dạng byte array
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        // Chuyển đổi byte array thành chuỗi Hex (hexadecimal)
        // ZaloPay yêu cầu MAC ở định dạng hex
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes);
    }
}
