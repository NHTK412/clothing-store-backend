# Thông Số Thiết Kế API Sạch

## 📋 Mục Lục
1. [Phân Tích Trạng Thái Hiện Tại](#phân-tích-trạng-thái-hiện-tại)
2. [Nguyên Tắc Thiết Kế API](#nguyên-tắc-thiết-kế-api)
3. [Đặt Tên Điểm Cuối & Cấu Trúc](#đặt-tên-điểm-cuối--cấu-trúc)
4. [Định Dạng Yêu Cầu/Phản Hồi](#định-dạng-yêu-cầuphản-hồi)
5. [Xử Lý Lỗi & Mã Trạng Thái](#xử-lý-lỗi--mã-trạng-thái)
6. [Xác Thực & Phân Quyền](#xác-thực--phân-quyền)
7. [Phân Trang, Lọc & Sắp Xếp](#phân-trang-lọc--sắp-xếp)
8. [Phiên Bản API](#phiên-bản-api)
9. [Các Vấn Đề & Cải Thiện](#các-vấn-đề--cải-thiện)
10. [Lộ Trình Thực Hiện](#lộ-trình-thực-hiện)

---

## 1. Phân Tích Trạng Thái Hiện Tại

### 1.1 Tình Trạng Hiện Tại

**Cấu Trúc Điểm Cuối:**
```
✗ /product              (không rõ ràng, nên là /products)
✗ /product/{id}         (GET/PUT/DELETE lẫn lộn)
✓ /orders               (số nhiều, rõ ràng)
✓ /customer/cart        (có tiền tố, rõ ràng domain)
✗ /auth                 (có /login, /register, /login-admin lẫn lộn)
✗ /admin/promotion      (trộn vai trò admin với tài nguyên)
```

**Định Dạng Phản Hồi:**
```json
// Hiện tại
{
  "success": true,
  "message": null,
  "data": { ... }
}
```

**Các Vấn Đề:**
- ✗ Đặt tên điểm cuối không nhất quán (số ít vs số nhiều)
- ✗ Phiên bản tài nguyên không tồn tại
- ✗ Luồng xác thực không rõ (`/login` vs `/login-admin`)
- ✗ Định dạng phản hồi lỗi không nhất quán
- ✗ Mã trạng thái HTTP không chuẩn (tất cả 200 OK, thậm chí lỗi)
- ✗ Tham số phân trang không tiêu chuẩn (trang từ 1, không từ 0)
- ✗ Không có siêu dữ liệu tài liệu API rõ ràng

---

## 2. Nguyên Tắc Thiết Kế API

### 2.1 Thiết Kế RESTful

**Nguyên Tắc Cốt Lõi:**
- ✓ Tài nguyên là danh từ (không phải động từ): `/products` không `/getProducts`
- ✓ Động từ HTTP: GET (đọc), POST (tạo), PUT/PATCH (cập nhật), DELETE (xóa)
- ✓ Cấu trúc phân cấp: `/users/{id}/orders`, `/products/{id}/reviews`
- ✓ Đặt tên nhất quán: luôn số nhiều cho bộ sưu tập
- ✓ Không trạng thái: mỗi yêu cầu phải có đủ thông tin để xử lý

### 2.2 Quy Ước

**Đặt Tên Tài Nguyên:**
```
Dạng số nhiều: /products, /orders, /customers
Tài nguyên cụ thể: /products/{productId}
Tài nguyên phụ: /products/{productId}/reviews
Hành động: /products/{productId}/publish (nếu cần, nên tránh)
```

**Phương Thức HTTP:**
| Phương Thức | Mục Đích | Idempotent |
|------------|---------|-----------|
| GET | Lấy tài nguyên | Có |
| POST | Tạo tài nguyên mới | Không |
| PUT | Thay thế toàn bộ tài nguyên | Có |
| PATCH | Cập nhật một phần | Không (tùy implementation) |
| DELETE | Xóa tài nguyên | Có |

### 2.3 Triết Lý Thiết Kế

```
Thiết Kế API Sạch = 
  Điểm Cuối Có Thể Dự Đoán +
  Định Dạng Yêu Cầu Rõ Ràng +
  Định Dạng Phản Hồi Nhất Quán +
  Mã Trạng Thái Thích Hợp +
  Thông Báo Lỗi Tốt +
  Tài Liệu Hoàn Chỉnh
```

---

## 3. Đặt Tên Điểm Cuối & Cấu Trúc

### 3.1 Cấu Trúc Được Đề Xuất

```
Cơ Sở API: /api/v1

Tài Nguyên:
  GET    /api/v1/products          → Liệt kê tất cả sản phẩm
  POST   /api/v1/products          → Tạo sản phẩm
  GET    /api/v1/products/{id}     → Lấy sản phẩm cụ thể
  PUT    /api/v1/products/{id}     → Cập nhật sản phẩm
  DELETE /api/v1/products/{id}     → Xóa sản phẩm

Tài Nguyên Phụ:
  GET    /api/v1/products/{id}/reviews       → Đánh giá sản phẩm
  POST   /api/v1/products/{id}/reviews       → Thêm đánh giá
  DELETE /api/v1/products/{id}/reviews/{rid} → Xóa đánh giá

Dành Riêng Cho Người Dùng:
  GET    /api/v1/customers/me              → Lấy khách hàng hiện tại
  GET    /api/v1/customers/me/orders       → Lấy đơn hàng của tôi
  GET    /api/v1/customers/me/wishlist     → Lấy danh sách yêu thích
  POST   /api/v1/customers/me/addresses    → Thêm địa chỉ

Quản Trị Viên:
  GET    /api/v1/admin/orders              → Xem tất cả đơn hàng
  GET    /api/v1/admin/promotions          → Xem khuyến mãi
  POST   /api/v1/admin/promotions          → Tạo khuyến mãi
```

### 3.2 Quy Tắc Quy Ước Điểm Cuối

```
1. Luôn sử dụng số nhiều cho bộ sưu tập
   ✓ /products, /orders, /reviews
   ✗ /product, /order, /review

2. Tránh động từ trong URL (ngoại trừ tài nguyên phụ)
   ✓ /products (GET tạo ra hành động)
   ✗ /getProducts, /fetchProducts

3. Sử dụng cấu trúc phân cấp cho mối quan hệ
   ✓ /customers/{id}/orders
   ✗ /orders?customerId=1 (có thể chấp nhận nhưng kém rõ ràng)

4. Sử dụng động từ HTTP cho hành động
   ✓ POST /products         (tạo)
   ✓ PUT /products/{id}     (thay thế)
   ✓ PATCH /products/{id}   (cập nhật một phần)
   ✗ POST /products/create  (động từ trong URL)

5. Chữ thường và dấu gạch dưới cho tài nguyên nhiều từ
   ✓ /shipping-addresses
   ✓ /membership-tiers
   ✗ /shippingAddresses (camelCase không phải REST)
   ✗ /ShippingAddresses (PascalCase không phải REST)
```

### 3.3 Bộ Lọc vs Đường Dẫn URI

```
Sử dụng đường dẫn URI cho tài nguyên cụ thể:
  /api/v1/products/{id}

Sử dụng tham số truy vấn để lọc/tìm kiếm:
  /api/v1/products?categoryId=5&minPrice=100&maxPrice=500
  /api/v1/products?search=nike&sort=-price&page=1&limit=10

Sử dụng tài nguyên phụ cho dữ liệu liên quan:
  /api/v1/orders/{id}/items
  /api/v1/customers/{id}/addresses
```

---

## 4. Định Dạng Yêu Cầu/Phản Hồi

### 4.1 Bao Bọc Phản Hồi Tiêu Chuẩn

**Định Dạng Phản Hồi Hiện Tại:**
```json
{
  "success": true,
  "message": "thông báo tùy chọn",
  "data": { }
}
```

**Định Dạng Được Đề Xuất Nâng Cao:**
```json
{
  "success": true,
  "code": 200,
  "message": "Sản phẩm được lấy thành công",
  "data": {
    "id": 1,
    "name": "Áo Nike",
    ...
  },
  "timestamp": "2026-02-26T10:30:00Z",
  "path": "/api/v1/products/1"
}
```

**Định Dạng Phản Hồi Lỗi:**
```json
{
  "success": false,
  "code": 404,
  "message": "Không tìm thấy tài nguyên",
  "error": "ProductNotFoundException",
  "details": {
    "missingResource": "Sản phẩm có id 999"
  },
  "timestamp": "2026-02-26T10:30:00Z",
  "path": "/api/v1/products/999"
}
```

### 4.2 Định Dạng Phản Hồi Được Phân Trang

**Hiện Tại (Không Nhất Quán):**
```java
// Một số điểm cuối trả về List<DTO>
List<ProductSummaryDTO>

// AllOrderByCustomer sử dụng phân trang nhưng trả về List
List<OrderSummaryDTO> với PageRequest
```

**Được Đề Xuất (Tiêu Chuẩn):**
```json
{
  "success": true,
  "code": 200,
  "message": "Sản phẩm được lấy thành công",
  "data": {
    "content": [ ... ], // các mục thực tế
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalElements": 42,
      "totalPages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Hoặc phản hồi được phân trang đơn giản hơn:**
```json
{
  "success": true,
  "code": 200,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "size": 10,
    "total": 42,
    "pages": 5
  }
}
```

### 4.3 Các Thực Tiễn Tốt Nhất DTO Yêu Cầu

**DTO Tốt:**
```java
@Data
@Valid
public class ProductCreateRequestDTO {
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;
    
    @NotNull(message = "Giá là bắt buộc")
    @Min(value = 0, message = "Giá phải >= 0")
    private Double unitPrice;
    
    @Min(value = 0, message = "Chiết khấu phải >= 0")
    private Double discount;
    
    @NotNull(message = "Danh mục là bắt buộc")
    private Integer categoryId;
}
```

**Xác Thực Yêu Cầu:**
- Nên xác thực ở cấp DTO bằng chú thích `@Valid`
- Thông báo lỗi rõ ràng
- Trả về 400 Yêu Cầu Xấu với lỗi xác thực

---

## 5. Xử Lý Lỗi & Mã Trạng Thái

### 5.1 Mã Trạng Thái HTTP

| Mã | Tên | Cách Sử Dụng | Hiện Tại | Cần Làm |
|-----|-----|---------|---------|--------|
| 200 | OK | GET/PUT/PATCH thành công | ✓ | - |
| 201 | Được Tạo | POST thành công | ✗ | ✓ Thực hiện |
| 204 | Không Có Nội Dung | XÓA thành công (không có phần thân) | ✗ | ✓ Cân nhắc |
| 400 | Yêu Cầu Xấu | Yêu cầu không hợp lệ/lỗi xác thực | ✗ | ✓ Thực hiện |
| 401 | Không Được Phép | Thiếu/xác thực không hợp lệ | ✗ | ✓ Thực hiện |
| 403 | Cấm | Xác thực hợp lệ nhưng không có quyền | ✗ | ✓ Thực hiện |
| 404 | Không Tìm Thấy | Tài nguyên không tồn tại | ✗ | ✓ Thực hiện |
| 409 | Xung Đột | Vi phạm quy tắc kinh doanh | ✗ | ✓ Thực hiện |
| 422 | Không Thể Xử Lý | Lỗi ngữ nghĩa | ✗ | ✓ Cân nhắc |
| 500 | Lỗi Máy Chủ | Lỗi không mong muốn | ✗ | ✓ Thực hiện |

### 5.2 Tiêu Chuẩn Phản Hồi Lỗi

**Lỗi Xác Thực (400):**
```json
{
  "success": false,
  "code": 400,
  "message": "Xác thực thất bại",
  "error": "ValidationException",
  "details": {
    "errors": [
      {
        "field": "unitPrice",
        "message": "Giá phải >= 0"
      },
      {
        "field": "name",
        "message": "Tên sản phẩm là bắt buộc"
      }
    ]
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Lỗi Xác Thực (401):**
```json
{
  "success": false,
  "code": 401,
  "message": "Yêu cầu xác thực",
  "error": "UnauthorizedException",
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Lỗi Quyền (403):**
```json
{
  "success": false,
  "code": 403,
  "message": "Bị từ chối truy cập",
  "error": "ForbiddenException",
  "details": {
    "requiredRole": "ADMIN"
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Tài Nguyên Không Tìm Thấy (404):**
```json
{
  "success": false,
  "code": 404,
  "message": "Không tìm thấy sản phẩm",
  "error": "NotFoundException",
  "details": {
    "resourceId": 999
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Vi Phạm Quy Tắc Kinh Doanh (409):**
```json
{
  "success": false,
  "code": 409,
  "message": "Không thể hủy đơn hàng này",
  "error": "ConflictException",
  "details": {
    "reason": "Chỉ có thể hủy đơn hàng có trạng thái PLACED",
    "currentStatus": "DELIVERED"
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

### 5.3 Trình Xử Lý Ngoại Lệ Toàn Cục

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "NotFoundException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                "ConflictException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(
                error.getField(),
                error.getDefaultMessage()
            ));
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.ofValidation(
                "Xác thực thất bại",
                errors,
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.of(
                HttpStatus.FORBIDDEN,
                "Bị từ chối truy cập",
                "ForbiddenException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi máy chủ nội bộ",
                ex.getClass().getSimpleName(),
                request.getRequestURI()
            ));
    }
}
```

---

## 6. Xác Thực & Phân Quyền

### 6.1 Các Vấn Đề Trạng Thái Hiện Tại

```
✗ /auth/login + /auth/login-admin (logic trùng lặp)
✗ @RequestParam(defaultValue = "false") Boolean admin (tham số ẩn)
✗ @PreAuthorize("hasRole('ADMIN')") lẫn lộn với điểm cuối tài nguyên
✗ Không có đường dẫn API quản trị viên riêng biệt
✗ Định dạng mã thông báo JWT không nhất quán
```

### 6.2 Cấu Trúc Được Đề Xuất

**Điểm Cuối Xác Thực:**
```
POST   /api/v1/auth/login           → Đăng nhập (khách hàng)
POST   /api/v1/auth/register        → Đăng ký khách hàng mới
POST   /api/v1/auth/refresh         → Làm mới mã thông báo JWT
POST   /api/v1/auth/logout          → Đăng xuất

POST   /api/v1/admin/auth/login     → Đăng nhập quản trị viên (xác thực riêng biệt)
POST   /api/v1/admin/auth/logout    → Đăng xuất quản trị viên
```

**Điểm Cuối Đăng Nhập Được Cải Thiện:**
```java
// TRƯỚC - Gây nhầm lẫn
@PostMapping("/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
    @RequestParam(defaultValue = "false") Boolean admin,
    @Valid @RequestBody AuthRequestDTO authRequestDTO
) { ... }

// SAU - Phân tách rõ ràng
@PostMapping("/auth/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> customerLogin(
    @Valid @RequestBody LoginRequestDTO loginRequest
) { ... }

@PostMapping("/admin/auth/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> adminLogin(
    @Valid @RequestBody LoginRequestDTO loginRequest
) { ... }
```

### 6.3 Phản Hồi Mã Thông Báo JWT

**Phản Hồi JWT Tiêu Chuẩn:**
```json
{
  "success": true,
  "code": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "roles": ["CUSTOMER"]
    }
  }
}
```

### 6.4 Mẫu Phân Quyền

```
Đường dẫn URL xác định phạm vi tài nguyên:
  /api/v1/products          → Công khai (chỉ GET)
  /api/v1/customers/me      → CUSTOMER chỉ
  /api/v1/admin/orders      → ADMIN chỉ

Chú thích @PreAuthorize xác định các vai trò được phép cho mỗi điểm cuối:
  @PreAuthorize("hasRole('CUSTOMER')")
  @PreAuthorize("hasRole('ADMIN')")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
```

---

## 7. Phân Trang, Lọc & Sắp Xếp

### 7.1 Trạng Thái Hiện Tại

```java
// Phân trang không nhất quán
@RequestParam(defaultValue = "1") Integer page,
@RequestParam(defaultValue = "10") Integer size,

// Một số điểm cuối sử dụng PageRequest, một số trả về List thô
PageRequest.of(page - 1, size);
```

### 7.2 Tiêu Chuẩn Được Đề Xuất

**Tham Số Truy Vấn:**
```
GET /api/v1/products?page=1&size=10&sort=-price,name&search=nike

- page: được lập chỉ mục 1 (thân thiện với người dùng)
- size: các mục trên mỗi trang (mặc định: 10)
- sort: tên trường được phân tách bằng dấu phẩy, tiền tố "-" cho GIẢM
  Ví dụ: sort=-price,name (giá GIẢM, tên TĂNG)
- search: tìm kiếm toàn văn bản
- filter: các bộ lọc dành riêng cho kinh doanh
```

**Thực Hiện:**
```java
@GetMapping
public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProducts(
    @RequestParam(defaultValue = "1") @Min(1) Integer page,
    @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
    @RequestParam(required = false) String search,
    @RequestParam(required = false) Integer categoryId,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(defaultValue = "price,asc") String sort
) {
    Pageable pageable = PageRequest.of(
        page - 1,
        size,
        parseSortFromString(sort)
    );
    
    Page<Product> products = productService.searchProducts(
        search, categoryId, minPrice, maxPrice, pageable
    );
    
    return ResponseEntity.ok(new ApiResponse<>(
        true,
        "Sản phẩm được lấy",
        PagedResponse.of(products)
    ));
}
```

**Định Dạng Phản Hồi:**
```json
{
  "success": true,
  "code": 200,
  "data": {
    "content": [ ... ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 42,
      "pages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

---

## 8. Phiên Bản API

### 8.1 Chiến Lược Phiên Bản

**Phiên Bản Đường Dẫn URL (Được Đề Xuất Cho Dự Án Này):**
```
/api/v1/products
/api/v2/products
```

**Những Lợi Ích:**
- Phiên bản rõ ràng trong URL
- Dễ dàng định tuyến đến các trình xử lý khác nhau
- Có thể loại bỏ các phiên bản cũ dần dần
- Khách hàng luôn biết phiên bản nào họ đang sử dụng

### 8.2 Kế Hoạch Di Chuyển

**Giai Đoạn 1:** Thêm tiền tố `/api/v1` vào tất cả điểm cuối
```java
@RequestMapping("/api/v1/products")  // Thay vì @RequestMapping("product")
```

**Giai Đoạn 2:** Khi cần thay đổi đột phá, tạo v2
```java
@RequestMapping("/api/v2/products")
```

**Giai Đoạn 3:** Loại bỏ v1, chuyển khách hàng sang v2

---

## 9. Các Vấn Đề & Cải Thiện

### 9.1 Các Vấn Đề Hiện Tại Được Phân Loại

#### **Các Vấn Đề Quan Trọng**

| Vấn Đề | Hiện Tại | Vấn Đề | Sửa |
|--------|---------|--------|-----|
| Đặt tên điểm cuối | `/product` không `/products` | Không chuẩn, số ít | Đổi tên thành số nhiều |
| Mã trạng thái | Tất cả 200 OK | Không thể phân biệt loại lỗi | Trả về mã thích hợp (201, 400, 404, 409) |
| Điểm cuối xác thực | `/login` + `/login-admin` | Gây nhầm lẫn, logic trùng lặp | Đường dẫn riêng biệt: `/auth/login`, `/admin/auth/login` |
| Nhầm lẫn phương thức HTTP | `@PatchMapping` vs `@PutMapping` | Không nhất quán | Tiêu chuẩn hóa: PATCH cho một phần, PUT hiếm khi sử dụng |
| Đường dẫn cơ sở | Không có tiền tố `/api/v1` | Không được thực hiện chuyên nghiệp | Thêm tiền tố phiên bản |

#### **Các Vấn Đề Có Mức Độ Ưu Tiên Cao**

| Vấn Đề | Hiện Tại | Vấn Đề | Sửa |
|--------|---------|--------|-----|
| Phản hồi lỗi | Chuỗi chung | Không được cấu trúc | Sử dụng GlobalExceptionHandler với định dạng tiêu chuẩn |
| Trang phân trang | Bắt đầu từ 1 | Chuyển đổi thủ công `page - 1` | Giữ 1-indexed tại API, chuyển đổi nội bộ |
| Bao bọc phản hồi | Cơ bản (thành công, thông báo, dữ liệu) | Thiếu mã, dấu thời gian | Nâng cao với mã, dấu thời gian, đường dẫn |
| Lỗi xác thực | Thông báo đơn giản | Không có chi tiết cấp trường | Trả về lỗi xác thực cấp trường |
| Xác thực 401 | Trả về 200 + success:false | Gây nhầm lẫn cho khách hàng | Trả về mã trạng thái 401 |

#### **Các Vấn Đề Mức Độ Ưu Tiên Trung Bình**

| Vấn Đề | Hiện Tại | Vấn Đề | Sửa |
|--------|---------|--------|-----|
| Lọc | Không có tiêu chuẩn | Không nhất quán trên các điểm cuối | Thêm định dạng @RequestParam tiêu chuẩn |
| Sắp xếp | Không được triển khai rõ ràng | Khác nhau theo từng điểm cuối | Thêm tham số sắp xếp với định dạng tiêu chuẩn |
| Giới Hạn Tốc Độ | Không có chỉ báo | Có thể bị lạm dụng | Thêm tiêu đề giới hạn tốc độ (tương lai) |
| HATEOAS | Không có liên kết | Không thể khám phá tài nguyên | Thêm `_links` vào phản hồi (tùy chọn) |

### 9.2 Các Vấn Đề Điểm Cuối Cụ Thể

#### **ProductController**

```
VẤN ĐỀ:
✗ @RequestMapping("product")          → Nên là "/api/v1/products"
✗ Phản hồi POST 200 OK                → Nên là 201 Được Tạo
✗ Không có trạng thái phản hồi DELETE  → Nên là 204 hoặc 200 không có nội dung
✗ @PathVariable yêu cầu nhưng không xác thực
✗ Không lọc theo trạng thái/giá/v.v

CẢI THIỆN:
→ Đổi tên thành "/api/v1/products"
→ Trả về 201 cho POST
→ Thêm lọc: ?categoryId=5&minPrice=100
→ Thêm sắp xếp: ?sort=-price,name
→ Thêm tìm kiếm: ?search=nike
→ Xác thực input trên các biến đường dẫn
```

#### **AuthController**

```
VẤN ĐỀ:
✗ @RequestMapping("auth")             → Nên là "/api/v1/auth"
✗ /login với @RequestParam Boolean    → Gây nhầm lẫn, trùng lặp /login-admin
✗ Phản hồi POST 200 OK                → Nên là 200 (xác thực ổn với 200)
✗ Không có điểm cuối đăng xuất
✗ Không có điểm cuối làm mới mã thông báo

CẢI THIỆN:
→ Đổi tên thành "/api/v1/auth"
→ Xóa @RequestParam Boolean admin
→ Tạo đường dẫn riêng biệt "/api/v1/admin/auth/login"
→ Thêm điểm cuối /logout
→ Thêm điểm cuối /refresh
→ Trả về 200 với mã thông báo trong dữ liệu
→ Trả về 401 khi thông tin đăng nhập không hợp lệ
```

#### **CartController**

```
VẤN ĐỀ:
✗ @RequestMapping("customer/cart")    → Nên là "/api/v1/customers/me/cart"
✗ Sử dụng PATCH để cập nhật nhưng trả về 200 → OK cho PATCH
✗ Không có điểm cuối DELETE cho mục giỏ hàng  → Nên có
✗ Truyền tham số không nhất quán

CẢI THIỆN:
→ Đổi tên thành "/api/v1/customers/me/cart"
→ Thêm DELETE /api/v1/customers/me/cart/items/{id}
→ Giữ PATCH cho cập nhật một phần
→ Thông báo lỗi rõ ràng
```

#### **OrderController**

```
VẤN ĐỀ:
✗ @RequestMapping("/orders")          → Nên là "/api/v1/orders"
✗ /orders/me dư thừa                  → Nên là /customers/me/orders
✗ PATCH để cập nhật trạng thái (không rõ) → Sử dụng PUT hoặc điểm cuối riêng biệt
✗ Không có phản hồi POST 201 Được Tạo

CẢI THIỆN:
→ Giữ "/api/v1/orders" cho truy cập ADMIN (tất cả đơn hàng)
→ Di chuyển đơn hàng của khách hàng sang "/api/v1/customers/me/orders"
→ Sử dụng PUT để cập nhật trạng thái: PUT /orders/{id}/status
→ Trả về 201 cho POST
→ Điểm cuối cập nhật trạng thái rõ ràng
```

---

## 10. Lộ Trình Thực Hiện

### **Giai Đoạn 1: Nền Tảng (Tuần 1-2)**

**1. Tạo các lớp StandardizedResponseWrapper:**
```
util/response/
├── ApiResponse.java (nâng cao với mã, dấu thời gian, đường dẫn)
├── PagedResponse.java (bao bọc phân trang)
├── ErrorResponse.java (định dạng lỗi)
└── ValidationError.java (lỗi cấp trường)
```

**2. Thực hiện GlobalExceptionHandler:**
- Xử lý tất cả các loại ngoại lệ
- Trả về mã trạng thái thích hợp
- Định dạng lỗi nhất quán

**3. Cập nhật định dạng phản hồi cơ sở:**
- Thêm mã (trạng thái HTTP)
- Thêm dấu thời gian
- Thêm đường dẫn
- Tiêu chuẩn hóa thông báo

### **Giai Đoạn 2: Cấu Trúc Lại Bộ Điều Khiển (Tuần 3-4)**

**1. Thêm tiền tố phiên bản API `/api/v1` vào tất cả điểm cuối:**
```java
@RequestMapping("/api/v1/products")
@RequestMapping("/api/v1/orders")
@RequestMapping("/api/v1/customers")
// v.v.
```

**2. Sửa đặt tên điểm cuối (số ít → số nhiều):**
```
/product → /api/v1/products
/auth → /api/v1/auth
/customer/cart → /api/v1/customers/me/cart
```

**3. Thực hiện mã trạng thái thích hợp:**
- 201 Được Tạo cho POST
- 204 Không Có Nội Dung cho DELETE (tùy chọn)
- 400 Yêu Cầu Xấu cho xác thực
- 401/403 cho các vấn đề xác thực
- 404 cho không tìm thấy
- 409 cho xung đột

**4. Tiêu chuẩn hóa xác thực:**
```java
POST /api/v1/auth/login (khách hàng)
POST /api/v1/admin/auth/login (quản trị viên)
POST /api/v1/auth/logout
POST /api/v1/auth/refresh
POST /api/v1/auth/register
```

### **Giai Đoạn 3: Tính Năng Nâng Cao (Tuần 5-6)**

**1. Thêm lọc & sắp xếp:**
```
GET /api/v1/products?categoryId=5&minPrice=100&maxPrice=500&sort=-price,name
GET /api/v1/orders?status=PLACED&sort=-createdDate
```

**2. Thêm chức năng tìm kiếm:**
```
GET /api/v1/products?search=nike
GET /api/v1/orders?search=order123
```

**3. Nâng cao phân trang:**
- Tiêu chuẩn hóa tham số trang/kích thước
- Thêm siêu dữ liệu vào phản hồi
- Thêm giới hạn kích thước (tối đa 100)

### **Giai Đoạn 4: Tài Liệu & Tôi (Tuần 7-8)**

**1. Cập nhật tài liệu Swagger/OpenAPI:**
- Tất cả điểm cuối được ghi chép
- Ví dụ yêu cầu/phản hồi
- Mã trạng thái được giải thích

**2. Kiểm tra API:**
- Kiểm tra đơn vị cho bộ điều khiển
- Kiểm tra tích hợp cho điểm cuối
- Kiểm tra trường hợp lỗi

**3. Tài Liệu:**
- Hướng dẫn người dùng API
- Hướng dẫn Di Chuyển nếu thay đổi phiên bản
- Hướng dẫn Mã Trạng Thái

---

## 11. Các Mẫu Mã

### 11.1 Bao Bọc ApiResponse Nâng Cao

```java
// util/response/ApiResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "Thành công", data, 
            LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, 
            LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, "Được tạo", data, 
            LocalDateTime.now(), null);
    }
}
```

### 11.2 Định Dạng ErrorResponse

```java
// util/response/ErrorResponse.java
@Data
public class ErrorResponse {
    private Boolean success = false;
    private Integer code;
    private String message;
    private String error;
    private Object details;
    private LocalDateTime timestamp;
    private String path;
    
    public static ErrorResponse of(HttpStatus status, String message,
            String error, String path) {
        ErrorResponse response = new ErrorResponse();
        response.success = false;
        response.code = status.value();
        response.message = message;
        response.error = error;
        response.timestamp = LocalDateTime.now();
        response.path = path;
        return response;
    }
}
```

### 11.3 Mẫu Bộ Điều Khiển Thực Tiễn Tốt Nhất

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    
    // LẤY TẤT CẢ với phân trang và lọc
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProducts(
        @RequestParam(defaultValue = "1") @Min(1) Integer page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(defaultValue = "price,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductDTO> result = productService.search(search, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(
            "Sản phẩm được lấy",
            PagedResponse.of(result)
        ));
    }
    
    // LẤY MỘT
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
        @PathVariable @Positive Integer id
    ) {
        ProductDTO product = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    // TẠO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
        @Valid @RequestBody ProductCreateRequestDTO request
    ) {
        ProductDTO product = productService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(product));
    }
    
    // CẬP NHẬT (PUT - thay thế toàn bộ, hoặc PATCH - một phần)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
        @PathVariable @Positive Integer id,
        @Valid @RequestBody ProductUpdateDTO request
    ) {
        ProductDTO product = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    // XÓA
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @PathVariable @Positive Integer id
    ) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thành công"));
    }
}
```

---

## 12. Danh Sách Kiểm Tra Di Chuyển

### Trước Khi Thực Hiện

- [ ] Xem xét tất cả các điểm cuối hiện có
- [ ] Xác định những thay đổi đột phá cần thiết
- [ ] Lên kế hoạch cửa sổ tương thích ngược
- [ ] Phối hợp với nhóm front-end

### Thực Hiện

- [ ] Tạo các lớp bao bọc phản hồi mới
- [ ] Thực hiện GlobalExceptionHandler
- [ ] Thêm phiên bản API vào tất cả bộ điều khiển
- [ ] Đổi tên điểm cuối thành hình thức số nhiều/tiêu chuẩn
- [ ] Cập nhật mã trạng thái HTTP
- [ ] Thêm xác thực input
- [ ] Cấu Trúc Lại Các Điểm Cuối Xác Thực
- [ ] Thêm hỗ trợ lọc & sắp xếp
- [ ] Cập nhật phản hồi lỗi

### Kiểm Tra

- [ ] Kiểm tra đơn vị tất cả bộ điều khiển
- [ ] Kiểm tra tích hợp tất cả điểm cuối
- [ ] Kiểm tra các trường hợp lỗi
- [ ] Kiểm tra các quy trình xác thực
- [ ] Kiểm tra tải (tùy chọn)

### Tài Liệu

- [ ] Cập nhật Swagger/OpenAPI
- [ ] Viết hướng dẫn người dùng API
- [ ] Ghi chép đường dẫn Di Chuyển
- [ ] Cung cấp ví dụ mã
- [ ] Ghi chép giới hạn tốc độ

### Triển Khai

- [ ] Triển khai để sắp xếp
- [ ] Kiểm tra kỹ lưỡng
- [ ] Nhận xét từ nhóm
- [ ] Triển khai để sản xuất
- [ ] Giám sát tỷ lệ lỗi
- [ ] Thu thập phản hồi

---

## 13. Tóm Tắt Lợi Ích

| Khía Cạnh | Trước | Sau |
|-----------|------|-----|
| **Có Thể Dự Đoán** | Các Mẫu Không Nhất Quán | Quy Ước REST Tiêu Chuẩn |
| **Bảo Trì** | Khó Tìm Điểm Cuối | Cấu Trúc Đặt Tên Rõ Ràng |
| **Xử Lý Lỗi** | Tất Cả Mã 200 | Mã Thích Hợp Cho Tất Cả Trường Hợp |
| **Trải Nghiệm Khách Hàng** | Truyền Tham Số Gây Nhầm Lẫn | Định Dạng Yêu Cầu Rõ Ràng |
| **Gỡ Lỗi** | Khó Hiểu Vấn Đề | Thông Báo Lỗi Có Cấu Trúc |
| **Tài Liệu** | Chưa Hoàn Chỉnh | Hoàn Chỉnh Với Swagger |
| **Kiểm Tra** | Khó Kiểm Tra | Dễ Viết Kiểm Tra |
| **Khả Năng Mở Rộng** | Trở Nên Khó Xử Lý Khi Phát Triển | Hỗ Trợ Sự Phát Triển |

---

## 14. Tài Liệu Tham Khảo & Tiêu Chuẩn

### Tiêu Chuẩn Thiết Kế API REST
- [Các Thực Tiễn Tốt Nhất API REST](https://restfulapi.net/)
- [Mã Trạng Thái HTTP](https://httpwg.org/specs/rfc9110.html#status.codes)
- [Tiêu Chuẩn JSON API](https://jsonapi.org/)
- [Thông Số Kỹ Thuật OpenAPI](https://swagger.io/specification/)

### Các Thực Tiễn Tốt Nhất Spring Boot
- [API RESTful Spring Boot](https://spring.io/guides/gs/rest-service/)
- [Xác Thực & Phân Quyền Spring](https://spring.io/projects/spring-security)
- [Xử Lý Ngoại Lệ Toàn Cục](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)

### Tiêu Chuẩn Xác Thực
- [Xác Thực TiBean Jakarta](https://jakarta.ee/specifications/bean-validation/)
- [Các Thực Tiễn Tốt Nhất Xác Thực](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/)

---

## Kết Luận

Thiết kế API sạch không chỉ là tuân theo các quy ước—đó là về **rõ ràng, tính nhất quán và khả năng dự đoán**. Bằng cách thực hiện các tiêu chuẩn này:

✓ **Nhà Phát Triển** thấy việc sử dụng API của bạn dễ dàng hơn  
✓ **Nhóm Front-end** có thể làm việc hiệu quả hơn  
✓ **Bảo Trì** trở nên đơn giản hơn  
✓ **Mở Rộng** được thực hiện một cách thẳng thắn  
✓ **Gỡ Lỗi** nhanh hơn  

Bắt đầu với Giai Đoạn 1 (bao bọc phản hồi & xử lý lỗi), sau đó chuyển sang Giai Đoạn 2 (cấu trúc lại bộ điều khiển), đảm bảo mỗi giai đoạn được kiểm tra kỹ lưỡng trước khi tiếp tục.

---

## 2. Nguyên Tắc Thiết Kế API

### 2.1 Thiết Kế RESTful 

**Nguyên Tắc Cốt Lõi:**
- ✓ Tài Nguyên Dưới Dạng Danh Từ (Không Phải Động Từ): `/products` chứ không phải `/getProducts`
- ✓ Động Từ HTTP: GET (đọc), POST (tạo), PUT/PATCH (cập nhật), DELETE (xóa)
- ✓ Cấu Trúc Phân Cấp: `/users/{id}/orders`, `/products/{id}/reviews`
- ✓ Đặt Tên Nhất Quán: Luôn số nhiều cho các bộ sưu tập
- ✓ Không Trạng Thái: Mỗi yêu cầu phải có đủ thông tin để xử lý

### 2.2 Quy Ước

**Đặt Tên Tài Nguyên:**
```
Hình Thức Số Nhiều: /products, /orders, /customers
Tài Nguyên Cụ Thể: /products/{productId}
Tài Nguyên Con: /products/{productId}/reviews
Hành Động: /products/{productId}/publish (nếu cần hành động, nên tránh)
```

**Phương Thức HTTP:**
| Phương Thức | Mục Đích | Lũy Tích |
|--------|---------|-----------|
| GET | Truy Xuất Tài Nguyên | Có |
| POST | Tạo Tài Nguyên Mới | Không |
| PUT | Thay Thế Toàn Bộ Tài Nguyên | Có |
| PATCH | Cập Nhật Một Phần | Không (tùy triển khai) |
| DELETE | Xóa Tài Nguyên | Có |

### 2.3 Triết Lý Thiết Kế

```
Thiết Kế API Sạch = 
  Điểm Cuối Có Thể Dự Đoán Được +
  Định Dạng Yêu Cầu Rõ Ràng +
  Định Dạng Phản Hồi Nhất Quán +
  Mã Trạng Thái Thích Hợp +
  Thông Báo Lỗi Rõ Ràng +
  Tài Liệu Đầy Đủ
```

---

## 3. Đặt Tên Điểm Cuối & Cấu Trúc

### 3.1 Cấu Trúc Được Đề Xuất

```
Cơ Sở API: /api/v1

Tài Nguyên:
  GET    /api/v1/products          → Liệt Kê Tất Cả Sản Phẩm
  POST   /api/v1/products          → Tạo Sản Phẩm
  GET    /api/v1/products/{id}     → Lấy Sản Phẩm Cụ Thể
  PUT    /api/v1/products/{id}     → Cập Nhật Sản Phẩm
  DELETE /api/v1/products/{id}     → Xóa Sản Phẩm

Tài Nguyên Con:
  GET    /api/v1/products/{id}/reviews       → Đánh Giá Sản Phẩm
  POST   /api/v1/products/{id}/reviews       → Thêm Đánh Giá
  DELETE /api/v1/products/{id}/reviews/{rid} → Xóa Đánh Giá

Dành Riêng Cho Người Dùng:
  GET    /api/v1/customers/me              → Lấy Khách Hàng Hiện Tại
  GET    /api/v1/customers/me/orders       → Lấy Đơn Hàng Của Tôi
  GET    /api/v1/customers/me/wishlist     → Lấy Danh Sách Yêu Thích
  POST   /api/v1/customers/me/addresses    → Thêm Địa Chỉ

Quản Trị Viên:
  GET    /api/v1/admin/orders              → Xem Tất Cả Đơn Hàng
  GET    /api/v1/admin/promotions          → Xem Khuyến Mãi
  POST   /api/v1/admin/promotions          → Tạo Khuyến Mãi
```

### 3.2 Quy Tắc Quy Ước Điểm Cuối

```
1. Luôn Sử Dụng Số Nhiều Cho Bộ Sưu Tập
   ✓ /products, /orders, /reviews
   ✗ /product, /order, /review

2. Tránh Động Từ Trong URL (Ngoại Trừ Tài Nguyên Con)
   ✓ /products (GET tạo hành động)
   ✗ /getProducts, /fetchProducts

3. Sử Dụng Cấu Trúc Phân Cấp Cho Mối Quan Hệ
   ✓ /customers/{id}/orders
   ✗ /orders?customerId=1 (chấp nhận được nhưng kém rõ ràng)

4. Sử Dụng Động Từ HTTP Cho Hành Động
   ✓ POST /products         (tạo)
   ✓ PUT /products/{id}     (thay thế)
   ✓ PATCH /products/{id}   (cập nhật một phần)
   ✗ POST /products/create  (động từ trong URL)

5. Chữ Thường Với Dấu Gạch Dưới Cho Tài Nguyên Nhiều Từ
   ✓ /shipping-addresses
   ✓ /membership-tiers
   ✗ /shippingAddresses (camelCase không phải REST)
   ✗ /ShippingAddresses (PascalCase không phải REST)
```

### 3.3 Bộ Lọc vs Đường Dẫn URI

```
Sử Dụng Đường Dẫn URI Cho Tài Nguyên Cụ Thể:
  /api/v1/products/{id}

Sử Dụng Tham Số Truy Vấn Cho Lọc/Tìm Kiếm:
  /api/v1/products?categoryId=5&minPrice=100&maxPrice=500
  /api/v1/products?search=nike&sort=-price&page=1&limit=10

Sử Dụng Tài Nguyên Con Cho Dữ Liệu Liên Quan:
  /api/v1/orders/{id}/items
  /api/v1/customers/{id}/addresses
```

---

## 4. Định Dạng Yêu Cầu/Phản Hồi

### 4.1 Bao Bọc Phản Hồi Chuẩn

**Định Dạng Phản Hồi Hiện Tại:**
```json
{
  "success": true,
  "message": "thông báo tùy chọn",
  "data": { }
}
```

**Định Dạng Nâng Cao Được Đề Xuất:**
```json
{
  "success": true,
  "code": 200,
  "message": "Sản phẩm được lấy thành công",
  "data": {
    "id": 1,
    "name": "Áo T-Shirt Nike",
    ...
  },
  "timestamp": "2026-02-26T10:30:00Z",
  "path": "/api/v1/products/1"
}
```

**Định Dạng Phản Hồi Lỗi:**
```json
{
  "success": false,
  "code": 404,
  "message": "Không tìm thấy tài nguyên",
  "error": "ProductNotFoundException",
  "details": {
    "missingResource": "Sản phẩm có id 999"
  },
  "timestamp": "2026-02-26T10:30:00Z",
  "path": "/api/v1/products/999"
}
```

### 4.2 Định Dạng Phản Hồi Được Phân Trang

**Hiện Tại (Không Nhất Quán):**
```java
// Một Số Điểm Cuối Trả Về List<DTO>
List<ProductSummaryDTO>

// AllOrderByCustomer Sử Dụng Phân Trang Nhưng Trả Về List
List<OrderSummaryDTO> với PageRequest
```

**Được Đề Xuất (Tiêu Chuẩn):**
```json
{
  "success": true,
  "code": 200,
  "message": "Sản phẩm được lấy thành công",
  "data": {
    "content": [ ... ], // các mục thực tế
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalElements": 42,
      "totalPages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Hoặc Phản Hồi Được Phân Trang Đơn Giản Hơn:**
```json
{
  "success": true,
  "code": 200,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "size": 10,
    "total": 42,
    "pages": 5
  }
}
```

### 4.3 Các Thực Tiễn Tốt Nhất DTO Yêu Cầu

**DTO Tốt:**
```java
@Data
@Valid
public class ProductCreateRequestDTO {
    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    private String name;
    
    @NotNull(message = "Giá là bắt buộc")
    @Min(value = 0, message = "Giá phải >= 0")
    private Double unitPrice;
    
    @Min(value = 0, message = "Chiết khấu phải >= 0")
    private Double discount;
    
    @NotNull(message = "Danh mục là bắt buộc")
    private Integer categoryId;
}
```

**Xác Thực Yêu Cầu:**
- Nên xác thực ở cấp DTO bằng chú thích `@Valid`
- Thông báo lỗi rõ ràng
- Trả về 400 Yêu Cầu Xấu với lỗi xác thực

---

## 5. Xử Lý Lỗi & Mã Trạng Thái

### 5.1 Mã Trạng Thái HTTP

| Mã | Tên | Cách Sử Dụng | Hiện Tại | Cần Làm |
|------|------|-------|---------|-------|
| 200 | OK | GET/PUT/PATCH thành công | ✓ | - |
| 201 | Được Tạo | POST thành công | ✗ | ✓ Thực Hiện |
| 204 | Không Có Nội Dung | XÓA thành công (không có phần thân) | ✗ | ✓ Cân Nhắc |
| 400 | Yêu Cầu Xấu | Yêu cầu không hợp lệ/lỗi xác thực | ✗ | ✓ Thực Hiện |
| 401 | Không Được Phép | Xác thực thiếu/không hợp lệ | ✗ | ✓ Thực Hiện |
| 403 | Bị Cấm | Xác thực hợp lệ nhưng không có quyền | ✗ | ✓ Thực Hiện |
| 404 | Không Tìm Thấy | Tài nguyên không tồn tại | ✗ | ✓ Thực Hiện |
| 409 | Xung Đột | Vi Phạm Quy Tắc Kinh Doanh | ✗ | ✓ Thực Hiện |
| 422 | Thực Thể Không Xử Lý Được | Lỗi Ngữ Pháp | ✗ | ✓ Cân Nhắc |
| 500 | Lỗi Máy Chủ | Lỗi Không Dự Kiến | ✗ | ✓ Thực Hiện |

### 5.2 Tiêu Chuẩn Phản Hồi Lỗi

**Lỗi Xác Thực (400):**
```json
{
  "success": false,
  "code": 400,
  "message": "Xác thực thất bại",
  "error": "ValidationException",
  "details": {
    "errors": [
      {
        "field": "unitPrice",
        "message": "Giá phải >= 0"
      },
      {
        "field": "name",
        "message": "Tên sản phẩm là bắt buộc"
      }
    ]
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Lỗi Xác Thực (401):**
```json
{
  "success": false,
  "code": 401,
  "message": "Cần Xác Thực",
  "error": "UnauthorizedException",
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Lỗi Quyền (403):**
```json
{
  "success": false,
  "code": 403,
  "message": "Truy Cập Bị Từ Chối",
  "error": "ForbiddenException",
  "details": {
    "requiredRole": "ADMIN"
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Không Tìm Thấy Tài Nguyên (404):**
```json
{
  "success": false,
  "code": 404,
  "message": "Không Tìm Thấy Sản Phẩm",
  "error": "NotFoundException",
  "details": {
    "resourceId": 999
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

**Vi Phạm Quy Tắc Kinh Doanh (409):**
```json
{
  "success": false,
  "code": 409,
  "message": "Không Thể Hủy Đơn Hàng Này",
  "error": "ConflictException",
  "details": {
    "reason": "Chỉ Có Thể Hủy Các Đơn Hàng PLACED",
    "currentStatus": "DELIVERED"
  },
  "timestamp": "2026-02-26T10:30:00Z"
}
```

### 5.3 Bộ Điều Khiển Xử Lý Ngoại Lệ Toàn Cầu

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "NotFoundException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                "ConflictException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(
                error.getField(),
                error.getDefaultMessage()
            ));
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.ofValidation(
                "Xác thực thất bại",
                errors,
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.of(
                HttpStatus.FORBIDDEN,
                "Bị từ chối truy cập",
                "ForbiddenException",
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi máy chủ nội bộ",
                ex.getClass().getSimpleName(),
                request.getRequestURI()
            ));
    }
}
```

---

## 6. Xác Thực & Phân Quyền

### 6.1 Các Vấn Đề Trạng Thái Hiện Tại

```
✗ /auth/login + /auth/login-admin (logic trùng lặp)
✗ @RequestParam(defaultValue = "false") Boolean admin (tham số ẩn)
✗ @PreAuthorize("hasRole('ADMIN')") lẫn lộn với điểm cuối tài nguyên
✗ Không có đường dẫn API quản trị viên riêng biệt
✗ Định dạng mã thông báo JWT không nhất quán
```

### 6.2 Cấu Trúc Được Đề Xuất

**Điểm Cuối Xác Thực:**
```
POST   /api/v1/auth/login           → Đăng nhập (khách hàng)
POST   /api/v1/auth/register        → Đăng ký khách hàng mới
POST   /api/v1/auth/refresh         → Làm mới mã thông báo JWT
POST   /api/v1/auth/logout          → Đăng xuất

POST   /api/v1/admin/auth/login     → Đăng nhập quản trị viên (xác thực riêng biệt)
POST   /api/v1/admin/auth/logout    → Đăng xuất quản trị viên
```

**Điểm Cuối Đăng Nhập Được Cải Thiện:**
```java
// TRƯỚC - Gây nhầm lẫn
@PostMapping("/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
    @RequestParam(defaultValue = "false") Boolean admin,
    @Valid @RequestBody AuthRequestDTO authRequestDTO
) { ... }

// SAU - Phân tách rõ ràng
@PostMapping("/auth/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> customerLogin(
    @Valid @RequestBody LoginRequestDTO loginRequest
) { ... }

@PostMapping("/admin/auth/login")
public ResponseEntity<ApiResponse<AuthResponseDTO>> adminLogin(
    @Valid @RequestBody LoginRequestDTO loginRequest
) { ... }
```

### 6.3 Phản Hồi Mã Thông Báo JWT

**Phản Hồi JWT Tiêu Chuẩn:**
```json
{
  "success": true,
  "code": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "roles": ["CUSTOMER"]
    }
  }
}
```

### 6.4 Mẫu Phân Quyền

```
Đường dẫn URL xác định phạm vi tài nguyên:
  /api/v1/products          → Công khai (chỉ GET)
  /api/v1/customers/me      → CUSTOMER chỉ
  /api/v1/admin/orders      → ADMIN chỉ

Chú thích @PreAuthorize xác định các vai trò được phép cho mỗi điểm cuối:
  @PreAuthorize("hasRole('CUSTOMER')")
  @PreAuthorize("hasRole('ADMIN')")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
```

---

## 7. Phân Trang, Lọc & Sắp Xếp

### 7.1 Trạng Thái Hiện Tại

```java
// Phân trang không nhất quán
@RequestParam(defaultValue = "1") Integer page,
@RequestParam(defaultValue = "10") Integer size,

// Một số điểm cuối sử dụng PageRequest, một số trả về List thô
PageRequest.of(page - 1, size);
```

### 7.2 Tiêu Chuẩn Được Đề Xuất

**Tham Số Truy Vấn:**
```
GET /api/v1/products?page=1&size=10&sort=-price,name&search=nike

- page: được lập chỉ mục 1 (thân thiện với người dùng)
- size: các mục trên mỗi trang (mặc định: 10)
- sort: tên trường được phân tách bằng dấu phẩy, tiền tố "-" cho GIẢM
  Ví dụ: sort=-price,name (giá GIẢM, tên TĂNG)
- search: tìm kiếm toàn văn bản
- filter: các bộ lọc dành riêng cho kinh doanh
```

**Thực Hiện:**
```java
@GetMapping
public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProducts(
    @RequestParam(defaultValue = "1") @Min(1) Integer page,
    @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
    @RequestParam(required = false) String search,
    @RequestParam(required = false) Integer categoryId,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(defaultValue = "price,asc") String sort
) {
    Pageable pageable = PageRequest.of(
        page - 1,
        size,
        parseSortFromString(sort)
    );
    
    Page<Product> products = productService.searchProducts(
        search, categoryId, minPrice, maxPrice, pageable
    );
    
    return ResponseEntity.ok(new ApiResponse<>(
        true,
        "Sản phẩm được lấy",
        PagedResponse.of(products)
    ));
}
```

**Định Dạng Phản Hồi:**
```json
{
  "success": true,
  "code": 200,
  "data": {
    "content": [ ... ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total": 42,
      "pages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

---

## 8. Phiên Bản API

### 8.1 Chiến Lược Phiên Bản

**Phiên Bản Đường Dẫn URI (Khuyến Nghị Cho Dự Án Này):**
```
/api/v1/products
/api/v2/products
```

**Lợi Ích:**
- Phiên Bản Rõ Ràng Trong URL
- Dễ Định Tuyến Đến Các Xử Lý Khác Nhau
- Có Thể Phản Ánh Các Phiên Bản Cũ Dần Dần
- Máy Khách Luôn Biết Phiên Bản Nào Họ Đang Sử Dụng

### 8.2 Kế Hoạch Di Chuyển

**Giai Đoạn 1:** Thêm Tiền Tố `/api/v1` Vào Tất Cả Điểm Cuối
```java
@RequestMapping("/api/v1/products")  // Thay Vì @RequestMapping("product")
```

**Giai Đoạn 2:** Khi Cần Thay Đổi Phá Vỡ, Tạo v2
```java
@RequestMapping("/api/v2/products")
```

**Giai Đoạn 3:** Phân Ánh Phiên Bản v1, Di Chuyển Máy Khách Sang v2

---

## 9. Các Vấn Đề & Cải Thiện

### 9.1 Các Vấn Đề Hiện Tại Được Phân Loại

#### **Các Vấn Đề Quan Trọng**

| Vấn Đề | Hiện Tại | Vấn Đề | Khắc Phục |
|-------|---------|--------|--------|
| Đặt Tên Điểm Cuối | `/product` không phải `/products` | Không chuẩn, số ít | Đổi Tên Thành Số Nhiều |
| Mã Trạng Thái | Tất Cả 200 OK | Không Thể Phân Biệt Loại Lỗi | Trả Về Mã Thích Hợp (201, 400, 404, 409) |
| Điểm Cuối Xác Thực | `/login` + `/login-admin` | Gây Nhầm Lẫn, Logic Trùng Lặp | Đường Dẫn Riêng Biệt: `/auth/login`, `/admin/auth/login` |
| HTTP Lẫn Lộn Phương Thức | `@PatchMapping` vs `@PutMapping` | Không Nhất Quán | Chuẩn Hóa: PATCH Cho Một Phần, PUT Hiếm Khi Sử Dụng |
| Đường Dẫn Cơ Sở | Không Có Tiền Tố `/api/v1` | Không Chuyên Nghiệp | Thêm Tiền Tố Phiên Bản |

#### **Các Vấn Đề Ưu Tiên Cao**

| Vấn Đề | Hiện Tại | Vấn Đề | Khắc Phục |
|-------|---------|--------|--------|
| Phản Hồi Lỗi | Chuỗi Chung | Không Được Cấu Trúc | Sử Dụng GlobalExceptionHandler Với Định Dạng Tiêu Chuẩn |
| Trang Phân Trang | Bắt Đầu Từ 1 | Chuyển Đổi Thủ Công `page - 1` | Giữ 1 Được Lập Chỉ Mục Ở API, Chuyển Đổi Nội Bộ |
| Bao Bọc Phản Hồi | Cơ Bản (thành công, tin nhắn, dữ liệu) | Mã, Dấu Thời Gian Thiếu | Tăng Cường Với Mã, Dấu Thời Gian, Đường Dẫn |
| Lỗi Xác Thực | Tin Nhắn Đơn Giản | Không Có Chi Tiết Trường | Trả Về Lỗi Xác Thực Cấp Trường |
| Xác Thực 401 | Trả Về 200 + thành công: sai | Gây Nhầm Lẫn Cho Máy Khách | Trả Về Mã Trạng Thái 401 |

#### **Các Vấn Đề Ưu Tiên Trung Bình**

| Vấn Đề | Hiện Tại | Vấn Đề | Khắc Phục |
|-------|---------|--------|--------|
| Lọc | Không Có Tiêu Chuẩn | Không Nhất Quán Trên Các Điểm Cuối | Thêm Định Dạng @RequestParam Tiêu Chuẩn |
| Sắp Xếp | Không Triển Khai Rõ Ràng | Khác Nhau Cho Mỗi Điểm Cuối | Thêm Tham Số Sắp Xếp Với Định Dạng Tiêu Chuẩn |
| Giới Hạn Tỷ Lệ | Không Có Chỉ Báo | Có Thể Bị Lạm Dụng | Thêm Tiêu Đề Giới Hạn Tỷ Lệ (Tương Lai) |
| HATEOAS | Không Có Liên Kết | Không Thể Khám Phá Tài Nguyên | Thêm `_links` Trong Phản Hồi (Tùy Chọn) |

### 9.2 Các Vấn Đề Về Điểm Cuối Cụ Thể

#### **ProductController**

```
VẤN ĐỀ:
✗ @RequestMapping("product")          → Nên Là "/api/v1/products"
✗ Phản Hồi POST 200 OK                → Nên Là 201 Được Tạo
✗ Không Có Trạng Thái Phản Hồi DELETE → Nên Là 204 Hoặc 200 Không Có Nội Dung
✗ @PathVariable Bắt Buộc Nhưng Không Xác Thực
✗ Không Lọc Theo Trạng Thái/Giá/v.v.

CẢI THIỆN:
→ Đổi Tên Thành "/api/v1/products"
→ Trả Về 201 Cho POST
→ Thêm Lọc: ?categoryId=5&minPrice=100
→ Thêm Sắp Xếp: ?sort=-price,name
→ Thêm Tìm Kiếm: ?search=nike
→ Xác Thực Đầu Vào Trên Các Biến Đường Dẫn
```

#### **AuthController**

```
VẤN ĐỀ:
✗ @RequestMapping("auth")             → Nên Là "/api/v1/auth"
✗ /login Có @RequestParam Boolean     → Gây Nhầm Lẫn, Trùng Lặp /login-admin
✗ Phản Hồi POST 200 OK                → Nên Là 200 (xác thực ổn với 200)
✗ Không Có Điểm Cuối Đăng Xuất
✗ Không Có Điểm Cuối Làm Mới Mã Thông Báo

CẢI THIỆN:
→ Đổi Tên Thành "/api/v1/auth"
→ Loại Bỏ @RequestParam Boolean admin
→ Tạo "/api/v1/admin/auth/login" Riêng Biệt
→ Thêm Điểm Cuối /logout
→ Thêm Điểm Cuối /refresh
→ Trả Về 200 Với Mã Thông Báo Trong Dữ Liệu
→ Trả Về 401 Trên Thông Tin Xác Thực Không Hợp Lệ
```

#### **CartController**

```
VẤN ĐỀ:
✗ @RequestMapping("customer/cart")    → Nên Là "/api/v1/customers/me/cart"
✗ Sử Dụng PATCH Để Cập Nhật Nhưng Trả Về 200 → OK Cho PATCH
✗ Không Có Điểm Cuối DELETE Cho Mục Giỏ Hàng
✗ Chuyển Tham Số Không Nhất Quán

CẢI THIỆN:
→ Đổi Tên Thành "/api/v1/customers/me/cart"
→ Thêm DELETE /api/v1/customers/me/cart/items/{id}
→ Giữ PATCH Cho Cập Nhật Một Phần
→ Thông Báo Lỗi Rõ Ràng
```

#### **OrderController**

```
VẤN ĐỀ:
✗ @RequestMapping("/orders")          → Nên Là "/api/v1/orders"
✗ /orders/me Dư Thừa                  → Nên Là /customers/me/orders
✗ PATCH Để Cập Nhật Trạng Thái (Không Rõ Ràng) → Sử Dụng PUT Hoặc Điểm Cuối Riêng Biệt
✗ Không Có Phản Hồi POST 201 Được Tạo

CẢI THIỆN:
→ Giữ "/api/v1/orders" Cho Truy Cập ADMIN (tất cả đơn hàng)
→ Di Chuyển Đơn Hàng Của Khách Hàng Sang "/api/v1/customers/me/orders"
→ Sử Dụng PUT Để Cập Nhật Trạng Thái: PUT /orders/{id}/status
→ Trả Về 201 Cho POST
→ Điểm Cuối Cập Nhật Trạng Thái Rõ Ràng
```

---

## 10. Lộ Trình Thực Hiện

### **Giai Đoạn 1: Nền Tảng (Tuần 1-2)**

**1. Tạo Các Lớp Bao Bọc Phản Hồi Tiêu Chuẩn:**
```
util/response/
├── ApiResponse.java (tăng cường với mã, dấu thời gian, đường dẫn)
├── PagedResponse.java (bao bọc phân trang)
├── ErrorResponse.java (định dạng lỗi)
└── ValidationError.java (lỗi cấp trường)
```

**2. Triển Khai Bộ Điều Khiển Xử Lý Ngoại Lệ Toàn Cầu:**
- Xử Lý Tất Cả Loại Ngoại Lệ
- Trả Về Mã Trạng Thái Thích Hợp
- Định Dạng Lỗi Nhất Quán

**3. Cập Nhật Định Dạng Phản Hồi Cơ Sở:**
- Thêm Mã (Trạng Thái HTTP)
- Thêm Dấu Thời Gian
- Thêm Đường Dẫn
- Chuẩn Hóa Tin Nhắn

### **Giai Đoạn 2: Cấu Trúc Lại Bộ Điều Khiển (Tuần 3-4)**

**1. Thêm Tiền Tố Phiên Bản API `/api/v1` Vào Tất Cả Điểm Cuối:**
```java
@RequestMapping("/api/v1/products")
@RequestMapping("/api/v1/orders")
@RequestMapping("/api/v1/customers")
// v.v.
```

**2. Sửa Đặt Tên Điểm Cuối (số ít → số nhiều):**
```
/product → /api/v1/products
/auth → /api/v1/auth
/customer/cart → /api/v1/customers/me/cart
```

**3. Triển Khai Mã Trạng Thái Thích Hợp:**
- 201 Được Tạo Cho POST
- 204 Không Có Nội Dung Cho DELETE (Tùy Chọn)
- 400 Yêu Cầu Xấu Cho Xác Thực
- 401/403 Cho Các Vấn Đề Xác Thực
- 404 Cho Không Tìm Thấy
- 409 Cho Xung Đột

**4. Chuẩn Hóa Xác Thực:**
```java
POST /api/v1/auth/login (khách hàng)
POST /api/v1/admin/auth/login (quản trị viên)
POST /api/v1/auth/logout
POST /api/v1/auth/refresh
POST /api/v1/auth/register
```

### **Giai Đoạn 3: Tính Năng Nâng Cao (Tuần 5-6)**

**1. Thêm Lọc & Sắp Xếp:**
```
GET /api/v1/products?categoryId=5&minPrice=100&maxPrice=500&sort=-price,name
GET /api/v1/orders?status=PLACED&sort=-createdDate
```

**2. Thêm Chức Năng Tìm Kiếm:**
```
GET /api/v1/products?search=nike
GET /api/v1/orders?search=order123
```

**3. Nâng Cao Phân Trang:**
- Chuẩn Hóa Tham Số Trang/Kích Thước
- Thêm Siêu Dữ Liệu Vào Phản Hồi
- Thêm Giới Hạn Kích Thước (Tối Đa 100)

### **Giai Đoạn 4: Tài Liệu & Kiểm Tra (Tuần 7-8)**

**1. Cập Nhật Tài Liệu Swagger/OpenAPI:**
- Tất Cả Điểm Cuối Được Ghi Chép
- Ví Dụ Yêu Cầu/Phản Hồi
- Mã Trạng Thái Được Giải Thích

**2. Kiểm Tra API:**
- Kiểm Tra Đơn Vị Cho Bộ Điều Khiển
- Kiểm Tra Tích Hợp Cho Điểm Cuối
- Kiểm Tra Trường Hợp Lỗi

**3. Tài Liệu:**
- Hướng Dẫn Người Dùng API
- Hướng Dẫn Di Chuyển Nếu Thay Đổi Phiên Bản
- Hướng Dẫn Mã Trạng Thái

---

## 11. Các Mẫu Mã

### 11.1 Bao Bọc ApiResponse Nâng Cao

```java
// util/response/ApiResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "Success", data, 
            LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, 
            LocalDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, "Created", data, 
            LocalDateTime.now(), null);
    }
}
```

### 11.2 Định Dạng ErrorResponse

```java
// util/response/ErrorResponse.java
@Data
public class ErrorResponse {
    private Boolean success = false;
    private Integer code;
    private String message;
    private String error;
    private Object details;
    private LocalDateTime timestamp;
    private String path;
    
    public static ErrorResponse of(HttpStatus status, String message,
            String error, String path) {
        ErrorResponse response = new ErrorResponse();
        response.success = false;
        response.code = status.value();
        response.message = message;
        response.error = error;
        response.timestamp = LocalDateTime.now();
        response.path = path;
        return response;
    }
}
```

### 11.3 Mẫu Bộ Điều Khiển Thực Tiễn Tốt Nhất

```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    
    // GET ALL with pagination and filtering
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProducts(
        @RequestParam(defaultValue = "1") @Min(1) Integer page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(defaultValue = "price,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductDTO> result = productService.search(search, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(
            "Products retrieved",
            PagedResponse.of(result)
        ));
    }
    
    // GET ONE
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
        @PathVariable @Positive Integer id
    ) {
        ProductDTO product = productService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
        @Valid @RequestBody ProductCreateRequestDTO request
    ) {
        ProductDTO product = productService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(product));
    }
    
    // UPDATE (PUT - full replace, or PATCH - partial)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
        @PathVariable @Positive Integer id,
        @Valid @RequestBody ProductUpdateDTO request
    ) {
        ProductDTO product = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @PathVariable @Positive Integer id
    ) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully"));
    }
}
```

---

## 12. Danh Sách Kiểm Tra Di Chuyển

### Trước Khi Thực Hiện

- [ ] Review all existing endpoints
- [ ] Identify breaking changes needed
- [ ] Plan backward compatibility window
- [ ] Coordinate with frontend team

### Thực Hiện

- [ ] Create new response wrapper classes
- [ ] Implement GlobalExceptionHandler
- [ ] Add API versioning to all controllers
- [ ] Rename endpoints to plural/standardized form
- [ ] Update HTTP status codes
- [ ] Add input validation
- [ ] Refactor authentication endpoints
- [ ] Add filtering & sorting support
- [ ] Update error responses

### Kiểm Tra

- [ ] Unit test all controllers
- [ ] Integration test all endpoints
- [ ] Test error cases
- [ ] Test authentication flows
- [ ] Load testing (optional)

### Tài Liệu

- [ ] Update Swagger/OpenAPI
- [ ] Write API user guide
- [ ] Document migration path
- [ ] Provide code examples
- [ ] Document rate limits

### Triển Khai

- [ ] Deploy to staging
- [ ] Test thoroughly
- [ ] Get team review
- [ ] Deploy to production
- [ ] Monitor error rates
- [ ] Gather feedback

---

## 13. Tóm Tắt Lợi Ích

| Aspect | Before | After |
|--------|--------|-------|
| **Predictability** | Inconsistent patterns | Standard REST conventions |
| **Maintenance** | Hard to find endpoints | Clear naming structure |
| **Error Handling** | All 200 status codes | Proper status codes for all cases |
| **Client Experience** | Confusing parameter passing | Clear request format |
| **Debugging** | Hard to understand issues | Structured error messages |
| **Documentation** | Incomplete | Complete with Swagger |
| **Testing** | Difficult to test | Easy to write tests |
| **Scalability** | Becomes unwieldy | Supports growth |

---

## 14. Tài Liệu Tham Khảo & Tiêu Chuẩn

### Tiêu Chuẩn Thiết Kế API REST
- [REST API Best Practices](https://restfulapi.net/)
- [HTTP Status Codes](https://httpwg.org/specs/rfc9110.html#status.codes)
- [JSON API Standard](https://jsonapi.org/)
- [OpenAPI Specification](https://swagger.io/specification/)

### Các Thực Tiễn Tốt Nhất Spring Boot
- [Spring Boot RESTful API](https://spring.io/guides/gs/rest-service/)
- [Spring Security & Authentication](https://spring.io/projects/spring-security)
- [Global Exception Handling](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)

### Tiêu Chuẩn Xác Thực
- [Jakarta Bean Validation](https://jakarta.ee/specifications/bean-validation/)
- [Validation Best Practices](https://docs.jboss.org/hibernate/beanvalidation/spec/2.0/api/)

---

## Kết Luận

Thiết Kế API Sạch không chỉ là tuân theo các quy ước—đó là về **rõ ràng, tính nhất quán và khả năng dự đoán**. Bằng cách thực hiện các tiêu chuẩn này:

✓ **Nhà Phát Triển** thấy việc sử dụng API của bạn dễ dàng hơn  
✓ **Nhóm Phát Triển Giao Diện** có thể làm việc hiệu quả hơn  
✓ **Bảo Trì** trở nên đơn giản hơn  
✓ **Mở Rộng Quy Mô** được thực hiện một cách thẳng thắn  
✓ **Gỡ Lỗi** nhanh hơn  

Bắt đầu với Giai Đoạn 1 (bao bọc phản hồi & xử lý lỗi), sau đó chuyển sang Giai Đoạn 2 (cấu trúc lại bộ điều khiển), đảm bảo mỗi giai đoạn được kiểm tra kỹ lưỡng trước khi tiếp tục.
