package com.example.clothingstore.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// import com.example.clothingstore.config.ZaloPayConfig;
// import com.example.clothingstore.dto.zalopay.CreateOrderRequest;
import com.example.clothingstore.dto.zalopay.ZaloPayResponseDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.PaymentMethodEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.util.crypto.HMACUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
// import java.util.logging.Logger;

/**
 * Service xử lý logic nghiệp vụ tích hợp thanh toán ZaloPay
 * Class này chịu trách nhiệm giao tiếp với ZaloPay API để tạo đơn hàng thanh
 * toán
 */
@Service // Đánh dấu đây là một Spring Service bean
@RequiredArgsConstructor
public class ZaloPayService {

    // @Autowired
    // private OrderRepository orderRepository;

    private final static Logger logger = LoggerFactory.getLogger(ZaloPayService.class);

    private final OrderRepository orderRepository;

    @Value("${zalopay.app-id}")
    private String APP_ID;

    @Value("${zalopay.key1}")
    private String KEY1;

    @Value("${zalopay.key2}")
    private String KEY2;

    @Value("${zalopay.create-order-url}")
    private String CREATE_ORDER_URL;

    @Value("${zalopay.callback-url}")
    private String CALLBACK_URL;

    @Value("${zalopay.return-url}")
    private String REDIRECT_URL;

    /**
     * Tạo mã giao dịch duy nhất (app_trans_id)
     * Format: YYMMDD_XXXXXX (Ví dụ: 251202_123456)
     * 
     * @return Mã giao dịch duy nhất theo định dạng ngày + số ngẫu nhiên
     */
    private String getAppTransId(Integer orderId) {
        // Lấy ngày hiện tại định dạng YYMMDD (ví dụ: 251202 cho ngày 02/12/2025)
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        // Tạo số ngẫu nhiên từ 0-999999 để đảm bảo tính duy nhất
        int rand = new Random().nextInt(1000000);
        // Ghép ngày và số ngẫu nhiên với dấu gạch dưới
        return date + "_" + rand + "_" + orderId;
    }

    /**
     * Tạo đơn hàng thanh toán trên hệ thống ZaloPay
     * 
     * Luồng xử lý:
     * 1. Chuẩn bị dữ liệu đơn hàng (app_id, amount, description,...)
     * 2. Tạo chữ ký MAC để bảo mật dữ liệu
     * 3. Gửi request tới ZaloPay API
     * 4. Nhận và trả về kết quả
     * 
     * @param req - Thông tin đơn hàng từ client
     * @return JSONObject chứa kết quả từ ZaloPay (return_code, return_message,
     *         order_url,...)
     * @throws Exception nếu có lỗi khi gọi API ZaloPay
     */
    // public JSONObject createOrder(CreateOrderRequest req) throws Exception {
    @Transactional
    public ZaloPayResponseDTO createOrder(
            Integer userId,
            // CreateOrderRequest req
            Integer orderId) throws Exception {

        // Order o = orderRepository.findById(req.getOrderId())
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (o.getCustomer().getUserId() != userId) {
            throw new ConflictException("You can only create ZaloPay order for your own orders");
        }

        if (o.getPaymentStatus() != OrderPaymentStatusEnum.UNPAID
                || o.getPaymentMethod() != PaymentMethodEnum.ZALOPAY) {
            throw new ConflictException("Only unpaid orders with payment method ZALOPAY can be paid via ZaloPay");
        }

        if (o.getStatus() != OrderStatusEnum.PLACED) {
            throw new ConflictException("Only orders with status PLACED can be paid via ZaloPay");
        }

        // Tạo Map chứa các tham số đơn hàng theo yêu cầu của ZaloPay
        Map<String, Object> order = new HashMap<>();
        // Tạo mã giao dịch duy nhất
        String appTransId = getAppTransId(o.getOrderId());
        // String appTransId = o.getOrderId().toString();

        // APP_ID: ID ứng dụng được cấp bởi ZaloPay khi đăng ký (giống như API key)
        // order.put("app_id", ZaloPayConfig.APP_ID);
        order.put("app_id", APP_ID);
        // app_trans_id: Mã giao dịch duy nhất do merchant tạo ra
        order.put("app_trans_id", appTransId);
        // app_user: Username/ID của người dùng đang thanh toán
        // order.put("app_user", req.getAppUser());
        order.put("app_user", userId.toString());

        // amount: Số tiền thanh toán (VNĐ)
        // order.put("amount", req.getAmount());
        order.put("amount", o.getFinalAmount().longValue());

        // app_time: Timestamp tạo đơn hàng (milliseconds)
        order.put("app_time", System.currentTimeMillis());
        // bank_code: Phương thức thanh toán ("zalopayapp" = ví ZaloPay)
        order.put("bank_code", "zalopayapp");
        // description: Mô tả đơn hàng
        order.put("description", "Payment for order #" + o.getOrderId());
        // item: Danh sách sản phẩm (JSON array) - hiện tại để trống
        order.put("item", "[]");
        // embed_data: Dữ liệu bổ sung (JSON object) - có thể chứa redirect_url
        // redirect_url: URL để redirect user sau khi thanh toán (không bắt buộc)
        JSONObject embedData = new JSONObject();
        // embedData.put("redirecturl", ZaloPayConfig.REDIRECT_URL);
        embedData.put("redirecturl", REDIRECT_URL);
        order.put("embed_data", embedData.toString());
        // callback_url: URL mà ZaloPay sẽ gọi khi thanh toán thành công
        // ZaloPay sẽ POST dữ liệu callback về URL này (BẮT BUỘC phải là URL public)
        // order.put("callback_url", ZaloPayConfig.CALLBACK_URL);
        order.put("callback_url", CALLBACK_URL);

        // Tạo chuỗi data để tính MAC (Message Authentication Code)
        // Các trường phải được nối theo đúng thứ tự và cách thức quy định của ZaloPay
        // Mục đích: Đảm bảo tính toàn vẹn dữ liệu, ZaloPay sẽ verify MAC này
        String data = order.get("app_id") + "|" +
                order.get("app_trans_id") + "|" +
                order.get("app_user") + "|" +
                order.get("amount") + "|" +
                order.get("app_time") + "|" +
                order.get("embed_data") + "|" +
                order.get("item");

        // Tính MAC sử dụng thuật toán HMAC-SHA256
        // KEY1: Khóa bí mật được ZaloPay cấp để mã hóa
        // Kết quả: Chuỗi hex đại diện cho chữ ký điện tử của dữ liệu
        String mac = HMACUtil.HMacHexStringEncode(
                HMACUtil.HMACSHA256,
                // ZaloPayConfig.KEY1,
                KEY1,
                data);

        // Thêm MAC vào dữ liệu đơn hàng
        order.put("mac", mac);

        System.err.println(mac);

        // Tạo HTTP client để gửi request
        CloseableHttpClient client = HttpClients.createDefault();
        // Tạo POST request tới endpoint tạo đơn hàng của ZaloPay
        // HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_ENDPOINT);
        HttpPost post = new HttpPost(CREATE_ORDER_URL);

        // Chuyển đổi Map thành danh sách NameValuePair (form data)
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : order.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }

        // Set entity cho request dưới dạng URL-encoded form data
        post.setEntity(new UrlEncodedFormEntity(params));

        // Thực thi request và nhận response từ ZaloPay
        CloseableHttpResponse response = client.execute(post);
        // Đọc nội dung response
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        // Đọc từng dòng response và ghép lại thành chuỗi JSON
        StringBuilder resultJsonStr = new StringBuilder();

        // ZaloPayResponseDTO responseDTO = new ZaloPayResponseDTO();

        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();

        ZaloPayResponseDTO zaloPayResponse = mapper.readValue(resultJsonStr.toString(), ZaloPayResponseDTO.class);

        return zaloPayResponse;

        // Chuyển chuỗi JSON thành JSONObject và trả về
        // Response thường chứa: return_code (1=thành công), return_message, order_url
        // (link thanh toán)
        // return new JSONObject(resultJsonStr.toString());

        // =================================================

        // // Tạo Map chứa các tham số đơn hàng theo yêu cầu của ZaloPay
        // Map<String, Object> order = new HashMap<>();
        // // Tạo mã giao dịch duy nhất
        // String appTransId = getAppTransId();

        // // APP_ID: ID ứng dụng được cấp bởi ZaloPay khi đăng ký (giống như API key)
        // order.put("app_id", ZaloPayConfig.APP_ID);
        // // app_trans_id: Mã giao dịch duy nhất do merchant tạo ra
        // order.put("app_trans_id", appTransId);
        // // app_user: Username/ID của người dùng đang thanh toán
        // order.put("app_user", req.getAppUser());
        // // amount: Số tiền thanh toán (VNĐ)
        // order.put("amount", req.getAmount());
        // // app_time: Timestamp tạo đơn hàng (milliseconds)
        // order.put("app_time", System.currentTimeMillis());
        // // bank_code: Phương thức thanh toán ("zalopayapp" = ví ZaloPay)
        // order.put("bank_code", "zalopayapp");
        // // description: Mô tả đơn hàng
        // order.put("description", req.getDescription());
        // // item: Danh sách sản phẩm (JSON array) - hiện tại để trống
        // order.put("item", "[]");
        // // embed_data: Dữ liệu bổ sung (JSON object) - có thể chứa redirect_url
        // // redirect_url: URL để redirect user sau khi thanh toán (không bắt buộc)
        // JSONObject embedData = new JSONObject();
        // embedData.put("redirecturl", ZaloPayConfig.REDIRECT_URL);
        // order.put("embed_data", embedData.toString());
        // // callback_url: URL mà ZaloPay sẽ gọi khi thanh toán thành công
        // // ZaloPay sẽ POST dữ liệu callback về URL này (BẮT BUỘC phải là URL public)
        // order.put("callback_url", ZaloPayConfig.CALLBACK_URL);

        // // Tạo chuỗi data để tính MAC (Message Authentication Code)
        // // Các trường phải được nối theo đúng thứ tự và cách thức quy định của
        // ZaloPay
        // // Mục đích: Đảm bảo tính toàn vẹn dữ liệu, ZaloPay sẽ verify MAC này
        // String data = order.get("app_id") + "|" +
        // order.get("app_trans_id") + "|" +
        // order.get("app_user") + "|" +
        // order.get("amount") + "|" +
        // order.get("app_time") + "|" +
        // order.get("embed_data") + "|" +
        // order.get("item");

        // // Tính MAC sử dụng thuật toán HMAC-SHA256
        // // KEY1: Khóa bí mật được ZaloPay cấp để mã hóa
        // // Kết quả: Chuỗi hex đại diện cho chữ ký điện tử của dữ liệu
        // String mac = HMACUtil.HMacHexStringEncode(
        // HMACUtil.HMACSHA256,
        // ZaloPayConfig.KEY1,
        // data);

        // // Thêm MAC vào dữ liệu đơn hàng
        // order.put("mac", mac);

        // // Tạo HTTP client để gửi request
        // CloseableHttpClient client = HttpClients.createDefault();
        // // Tạo POST request tới endpoint tạo đơn hàng của ZaloPay
        // HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_ENDPOINT);

        // // Chuyển đổi Map thành danh sách NameValuePair (form data)
        // List<NameValuePair> params = new ArrayList<>();
        // for (Map.Entry<String, Object> entry : order.entrySet()) {
        // params.add(new BasicNameValuePair(entry.getKey(),
        // entry.getValue().toString()));
        // }

        // // Set entity cho request dưới dạng URL-encoded form data
        // post.setEntity(new UrlEncodedFormEntity(params));

        // // Thực thi request và nhận response từ ZaloPay
        // CloseableHttpResponse response = client.execute(post);
        // // Đọc nội dung response
        // BufferedReader rd = new BufferedReader(new
        // InputStreamReader(response.getEntity().getContent()));

        // // Đọc từng dòng response và ghép lại thành chuỗi JSON
        // StringBuilder resultJsonStr = new StringBuilder();
        // String line;

        // while ((line = rd.readLine()) != null) {
        // resultJsonStr.append(line);
        // }

        // // Chuyển chuỗi JSON thành JSONObject và trả về
        // // Response thường chứa: return_code (1=thành công), return_message,
        // order_url
        // // (link thanh toán)
        // return new JSONObject(resultJsonStr.toString());

    }

    /**
     * Xử lý callback từ ZaloPay khi thanh toán thành công
     * 
     * Luồng xử lý:
     * 1. Nhận data và mac từ ZaloPay
     * 2. Verify MAC để đảm bảo callback thực sự từ ZaloPay
     * 3. Parse data để lấy thông tin giao dịch
     * 4. Cập nhật trạng thái đơn hàng trong database
     * 5. Trả về kết quả cho ZaloPay
     * 
     * @param callbackData - Dữ liệu callback từ ZaloPay
     * @return JSONObject chứa return_code và return_message
     * @throws Exception nếu có lỗi xử lý
     */
    public JSONObject handleCallback(String dataStr, String reqMac) throws Exception {
        JSONObject result = new JSONObject();

        try {
            // Bước 1: Tính MAC từ data nhận được
            // Sử dụng KEY2 để verify (khác với KEY1 dùng khi tạo đơn)
            String mac = HMACUtil.HMacHexStringEncode(
                    HMACUtil.HMACSHA256,
                    // ZaloPayConfig.KEY2,
                    KEY2,
                    dataStr);

            // Bước 2: So sánh MAC tính được với MAC từ ZaloPay gửi về
            if (!mac.equals(reqMac)) {
                // MAC không khớp -> callback giả mạo hoặc dữ liệu bị sửa đổi
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
                return result;
            }

            // Bước 3: Parse data để lấy thông tin giao dịch
            JSONObject data = new JSONObject(dataStr);

            // Lấy các thông tin quan trọng từ callback
            String appTransId = data.getString("app_trans_id"); // Mã giao dịch
            Long amount = data.getLong("amount"); // Số tiền
            String appUser = data.getString("app_user"); // User ID
            // Long appTime = data.getLong("app_time"); // Thời gian tạo đơn
            Long zapTransId = data.getLong("zp_trans_id"); // Mã giao dịch ZaloPay

            Integer orderId = Integer.parseInt(appTransId.split("_")[2]);

            Order o = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found"));

            if (o.getStatus() != OrderStatusEnum.PLACED) {
                throw new ConflictException("Only orders with status PLACED can be paid via ZaloPay");
            }

            // o.setVnpayCode(zapTransId.toString());
            // o.setZaloAppTransId(appTransId);

            // Map<String, Object> paymentData = new HashMap<>();
            // paymentData.put("appId", data.getInt("app_id"));
            // paymentData.put("appTransId", data.getString("app_trans_id"));
            // paymentData.put("mac", mac);

            // o.setPaymentData(paymentData);

            o.setPaymentId(data.getString("app_trans_id"));

            o.setPaymentStatus(OrderPaymentStatusEnum.PAID);

            orderRepository.save(o);

            // TODO: Bước 4: Cập nhật trạng thái đơn hàng trong database
            // Ví dụ:
            // - Tìm đơn hàng theo app_trans_id
            // - Cập nhật trạng thái = "PAID" hoặc "SUCCESS"
            // - Lưu zp_trans_id để đối soát sau này
            // - Gửi email/notification cho khách hàng

            logger.info("Payment successful!");
            logger.info("- Transaction ID: " + appTransId);
            logger.info("- Số tiền: " + amount + " VNĐ");
            logger.info("- User: " + appUser);
            logger.info("- ZaloPay Trans ID: " + zapTransId);

            // Bước 5: Trả về thành công cho ZaloPay
            // return_code = 1 -> ZaloPay biết callback đã được xử lý thành công
            // Nếu trả về -1 -> ZaloPay sẽ retry callback nhiều lần
            result.put("return_code", 1);
            result.put("return_message", "success");

        } catch (Exception e) {
            // Có lỗi xảy ra -> trả về -1 để ZaloPay retry
            // e.printStackTrace();
            // logger.error("Error occurred while processing ZaloPay callback", e);
            logger.error("Error occurred while processing ZaloPay callback: " + e);

            result.put("return_code", -1);
            result.put("return_message", e.getMessage());
        }

        return result;
    }

    public Map<String, Object> getPaymentDetail(String appTransId) throws Exception {

        // String data = ZaloPayConfig.APP_ID + "|" + appTransId + "|" +
        // ZaloPayConfig.KEY1;
        // String mac = HMACUtil.HMacHexStringEncode(
        // HMACUtil.HMACSHA256,
        // ZaloPayConfig.KEY1,
        // data);

        String data = APP_ID + "|" + appTransId + "|" + KEY1;
        String mac = HMACUtil.HMacHexStringEncode(
                HMACUtil.HMACSHA256,
                // ZaloPayConfig.KEY1,
                KEY1,
                data);

        String url = "https://sb-openapi.zalopay.vn/v2/query";

        // logger.warning("app_id: " + APP_ID);
        // logger.warning("app_trans_id: " + appTransId);
        // logger.warning("mac: " + mac);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // body.add("app_id", String.valueOf(ZaloPayConfig.APP_ID));
        // body.add("app_trans_id", String.valueOf(appTransId));
        // body.add("mac", String.valueOf(mac));

        body.add("app_id", String.valueOf(APP_ID));
        body.add("app_trans_id", String.valueOf(appTransId));
        body.add("mac", String.valueOf(mac));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        // return Map.of(
        // "body", response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> res = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
        });

        return res;

    }
}
