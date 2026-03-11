package com.example.clothingstore.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// import com.example.clothingstore.dto.zalopay.CreateOrderRequest;
import com.example.clothingstore.dto.zalopay.ZaloPayResponseDTO;
import com.example.clothingstore.service.ZaloPayService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller xử lý các API liên quan đến thanh toán ZaloPay
 * Đây là lớp điều khiển REST API để tương tác với hệ thống thanh toán ZaloPay
 */
@RestController // Đánh dấu đây là REST Controller, tự động chuyển đổi response thành JSON
@RequestMapping("v1/payments/zalopay") // Định nghĩa base URL cho tất cả API trong controller này
@RequiredArgsConstructor

public class ZaloPayController {

    // @Autowired // Tự động inject ZaloPayService vào controller
    // private ZaloPayService zaloPayService;

    private final ZaloPayService zaloPayService; // Sử dụng Lombok để tự động tạo constructor và inject service

    /**
     * API tạo đơn hàng thanh toán ZaloPay
     * Endpoint: POST /api/zalopay/create-order
     * 
     * @param req - Đối tượng chứa thông tin đơn hàng (số tiền, mô tả, người dùng)
     * @return JSON string chứa kết quả tạo đơn hàng từ ZaloPay (bao gồm order_url
     *         để redirect người dùng)
     * @throws Exception nếu có lỗi trong quá trình tạo đơn hàng
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create/{orderId}") // Mapping cho HTTP POST request
    public ResponseEntity<ApiResponse<ZaloPayResponseDTO>> createOrder(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            // @RequestBody CreateOrderRequest req,
            @PathVariable Integer orderId,
            HttpServletRequest request)
            throws Exception {
        // Gọi service để tạo đơn hàng trên ZaloPay
        // JSONObject res = zaloPayService.createOrder(req);
        // Trả về kết quả dạng JSON string
        // return res.toString();

        Integer userId = userDetails.getUserId();

        ZaloPayResponseDTO res = zaloPayService.createOrder(userId, orderId);

        // return ResponseEntity.ok(new ApiResponse<>(true, "Create ZaloPay order
        // successfully", res));

        return ResponseEntity.ok(
                ApiResponse.created("Successfully created ZaloPay order", res, request.getRequestURI()));
    }

    // @PostMapping("/create-order") // Mapping cho HTTP POST request
    // public String createOrder(@RequestBody CreateOrderRequest req) throws
    // Exception {
    // // Gọi service để tạo đơn hàng trên ZaloPay
    // JSONObject res = zaloPayService.createOrder(req);
    // // Trả về kết quả dạng JSON string
    // return res.toString();
    // }

    /**
     * API nhận callback từ ZaloPay khi thanh toán thành công
     * Endpoint: POST /api/zalopay/callback
     * 
     * QUAN TRỌNG:
     * - URL này phải PUBLIC (truy cập được từ internet)
     * - ZaloPay sẽ POST dữ liệu về URL này khi user thanh toán thành công
     * - Phải verify MAC trước khi tin tưởng dữ liệu
     * - Phải trả về return_code=1 để ZaloPay biết đã xử lý thành công
     * 
     * @param cbdata - Map chứa data và mac từ ZaloPay
     * @return JSON string chứa return_code và return_message
     * @throws Exception nếu có lỗi xử lý callback
     */
    @PostMapping("/callback")
    public String callback(@RequestBody java.util.Map<String, Object> cbdata) throws Exception {
        // Log để biết callback có được gọi không
        System.out.println("=== ZALOPAY CALLBACK RECEIVED ===");
        System.out.println("Callback data: " + cbdata);

        // Lấy data và mac từ callback
        String dataStr = (String) cbdata.get("data");
        String reqMac = (String) cbdata.get("mac");

        // Gọi service xử lý callback (verify MAC, cập nhật DB,...)
        JSONObject result = zaloPayService.handleCallback(dataStr, reqMac);

        // Trả về kết quả cho ZaloPay
        return result.toString();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payment-detail/{appTransId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentDetail(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            HttpServletRequest request,
            @PathVariable String appTransId) throws Exception {

        Map<String, Object> res = zaloPayService.getPaymentDetail(appTransId);

        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved payment detail", res, request.getRequestURI()));
    }

}
