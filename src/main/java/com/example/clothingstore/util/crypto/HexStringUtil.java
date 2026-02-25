/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.example.clothingstore.util.crypto;

import java.util.Locale;

/**
 * Utility class để chuyển đổi giữa byte array và chuỗi Hexadecimal (hệ 16)
 * Hexadecimal sử dụng 16 ký tự: 0-9 và a-f
 * Ví dụ: byte 255 = "ff" trong hex, byte 10 = "0a" trong hex
 */
public class HexStringUtil {
	// @formatter:off
	/**
	 * Bảng tra cứu các ký tự hex (0-9, a-f)
	 * Mỗi byte (0-255) sẽ được biểu diễn bằng 2 ký tự hex
	 */
	static final byte[] HEX_CHAR_TABLE = {
        (byte) '0', (byte) '1', (byte) '2', (byte) '3',
        (byte) '4', (byte) '5', (byte) '6', (byte) '7',
        (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
        (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };
	// @formatter:on

	/**
	 * Chuyển đổi mảng byte thành chuỗi hexadecimal
	 * 
	 * Ví dụ:
	 * - Input: [15, 255, 0]
	 * - Output: "0fff00"
	 * 
	 * @param raw - Mảng byte cần chuyển đổi
	 * @return Chuỗi hexadecimal (chữ thường)
	 */
	public static String byteArrayToHexString(byte[] raw) {
		// Mỗi byte cần 2 ký tự hex, vì vậy mảng hex gấp đôi kích thước mảng byte
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		// Duyệt qua từng byte trong mảng
		for (byte b : raw) {
			// Chuyển byte thành số dương (0-255)
			int v = b & 0xFF;
			// Lấy 4 bit cao (chia cho 16) -> ký tự hex đầu tiên
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			// Lấy 4 bit thấp (lấy phần dư khi chia cho 16) -> ký tự hex thứ hai
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		// Chuyển mảng byte chứa ký tự ASCII thành String
		return new String(hex);
	}

	/**
	 * Chuyển đổi chuỗi hexadecimal thành mảng byte
	 * Đây là hàm ngược lại của byteArrayToHexString
	 * 
	 * Ví dụ:
	 * - Input: "0fff00"
	 * - Output: [15, 255, 0]
	 * 
	 * @param hex - Chuỗi hexadecimal (có thể viết hoa hoặc viết thường)
	 * @return Mảng byte tương ứng
	 */
	public static byte[] hexStringToByteArray(String hex) {
		// Chuyển toàn bộ string thành chữ thường
		String hexstandard = hex.toLowerCase(Locale.ENGLISH);
		// Mỗi 2 ký tự hex = 1 byte
		int sz = hexstandard.length() / 2;
		byte[] bytesResult = new byte[sz];

		int idx = 0;
		for (int i = 0; i < sz; i++) {
			// Lấy ký tự hex đầu tiên (4 bit cao)
			bytesResult[i] = (byte) (hexstandard.charAt(idx));
			++idx;
			// Lấy ký tự hex thứ hai (4 bit thấp)
			byte tmp = (byte) (hexstandard.charAt(idx));
			++idx;

			// Chuyển đổi ký tự '0'-'9' hoặc 'a'-'f' thành giá trị số
			if (bytesResult[i] > HEX_CHAR_TABLE[9]) {
				// Nếu là 'a'-'f', trừ đi ASCII của 'a' rồi cộng 10
				bytesResult[i] -= ((byte) ('a') - 10);
			} else {
				// Nếu là '0'-'9', trừ đi ASCII của '0'
				bytesResult[i] -= (byte) ('0');
			}
			if (tmp > HEX_CHAR_TABLE[9]) {
				tmp -= ((byte) ('a') - 10);
			} else {
				tmp -= (byte) ('0');
			}

			// Kết hợp 4 bit cao và 4 bit thấp thành 1 byte
			// Nhân 16 (shift left 4 bits) rồi cộng với 4 bit thấp
			bytesResult[i] = (byte) (bytesResult[i] * 16 + tmp);
		}
		return bytesResult;
	}
}