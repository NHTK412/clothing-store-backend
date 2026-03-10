package com.example.clothingstore.config;

import org.springframework.context.annotation.Configuration;

/**
 * Class cấu hình các thông số kết nối ZaloPay
 * Chứa các thông tin nhạy cảm để tích hợp với ZaloPay API
 * LÀM VIỆC VỚI MÔI TRƯỜNG SANDBOX (thử nghiệm)
 */
@Configuration // Đánh dấu đây là class cấu hình Spring
public class ZaloPayConfig {

    /**
     * APP_ID: Mã định danh ứng dụng
     * Được ZaloPay cấp khi đăng ký merchant/ứng dụng
     * Dùng để xác định ứng dụng nào đang gọi API
     */
    public static final String APP_ID = "2553";

    /**
     * KEY1: Khóa bí mật thứ nhất
     * Dùng để tạo chữ ký MAC (Message Authentication Code) khi tạo đơn hàng
     * Đảm bảo dữ liệu gửi lên ZaloPay không bị giả mạo
     */
    public static final String KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";

    /**
     * KEY2: Khóa bí mật thứ hai
     * Dùng để verify callback từ ZaloPay về server khi thanh toán thành công
     * Đảm bảo callback thực sự từ ZaloPay gửi về, không phải từ nguồn khác
     */
    public static final String KEY2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";

    /**
     * CREATE_ORDER_ENDPOINT: Đường dẫn API tạo đơn hàng
     * Đây là endpoint sandbox (sb-openapi) dùng để test
     * Khi lên production sẽ đổi sang: https://openapi.zalopay.vn/v2/create
     */
    public static final String CREATE_ORDER_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";

    /**
     * CALLBACK_URL: URL mà ZaloPay sẽ gọi khi thanh toán thành công
     * Phải là URL public (có thể truy cập từ internet)
     * 
     * LƯU Ý:
     * - Khi dev local: dùng ngrok hoặc công cụ tương tự để expose localhost
     * Ví dụ chạy: ngrok http 8080
     * Sau đó lấy URL: https://abc123.ngrok.io/api/zalopay/callback
     * - Khi production: dùng domain thật của server
     * 
     * HƯỚNG DẪN DÙNG NGROK:
     * 1. Download ngrok: https://ngrok.com/download
     * 2. Chạy: ngrok http 8080
     * 3. Copy URL forwarding (https://xxx.ngrok.io) và thay thế bên dưới
     */
    // public static final String CALLBACK_URL =
    // "http://127.0.0.1:8080/api/zalopay/callback";

    public static final String CALLBACK_URL = "https://bilateral-misunderstandingly-veola.ngrok-free.dev/api/v1/payments/zalopay/callback";

    /**
     * REDIRECT_URL: URL để redirect người dùng sau khi thanh toán (thành công hoặc
     * thất bại)
     * Đây là trang web của bạn, người dùng sẽ quay lại trang này sau khi thanh toán
     * 
     * Ví dụ: "https://yourdomain.com/payment-result"
     */
    public static final String REDIRECT_URL = "http://localhost:5173/my-orders";
}