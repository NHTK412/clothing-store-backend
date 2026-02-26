# 📊 Bảng Chuyển Đổi Endpoint API - Dự Án Clothingstore

**Tài Liệu này hướng dẫn chuyển đổi tất cả endpoint từ cấu trúc hiện tại sang cấu trúc API RESTful chuẩn với tiền tố `/api/v1`**

---

## 📌 Huyền Thoại Cột

| Cột | Mô Tả |
|-----|-------|
| **URL Gốc** | Đường dẫn endpoint hiện tại |
| **URL Chuyển Đổi** | Đường dẫn endpoint mới (tiêu chuẩn REST + `/api/v1`) |
| **Tham Số Query String** | Tham số yêu cầu (page, size, sort, filter, v.v.) |
| **Response Body** | Cấu trúc phản hồi chuẩn |
| **Phân Quyền** | Vai trò được yêu cầu (Public, CUSTOMER, ADMIN) |
| **File Cần Sửa** | Đường dẫn file Java cần cập nhật |

---

## 1️⃣ AUTHENTICATION ENDPOINTS

### 1.1 Đăng Nhập (Khách Hàng)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /auth/login?admin=false` |
| **URL Chuyển Đổi** | `POST /api/v1/auth/login` |
| **Tham Số Query String** | Không có (đã loại bỏ tham số `admin`) |
| **Response Body** | `{ success, code: 200, message, data: { accessToken, refreshToken, expiresIn, user: { id, username, email, roles } }, timestamp, path }` |
| **Phân Quyền** | Public (Không yêu cầu xác thực) |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/AuthController.java` |

### 1.2 Đăng Nhập (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /auth/login-admin` |
| **URL Chuyển Đổi** | `POST /api/v1/admin/auth/login` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { accessToken, refreshToken, expiresIn, user: { id, username, email, roles } }, timestamp, path }` |
| **Phân Quyền** | Public (Không yêu cầu xác thực) |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/AuthController.java` |

### 1.3 Đăng Ký

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /auth/register` |
| **URL Chuyển Đổi** | `POST /api/v1/auth/register` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 201, message, data: { id, username, email, roles }, timestamp, path }` |
| **Phân Quyền** | Public (Không yêu cầu xác thực) |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/AuthController.java` |

### 1.4 Đăng Xuất (Khách Hàng)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `POST /api/v1/auth/logout` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Đã đăng xuất thành công" }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/AuthController.java` |

### 1.5 Làm Mới Mã Thông Báo

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `POST /api/v1/auth/refresh` |
| **Tham Số Query String** | `refreshToken` (trong body) |
| **Response Body** | `{ success, code: 200, message, data: { accessToken, expiresIn }, timestamp, path }` |
| **Phân Quyền** | Public (Không yêu cầu xác thực) |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/AuthController.java` |

---

## 2️⃣ PRODUCT ENDPOINTS

### 2.1 Liệt Kê Tất Cả Sản Phẩm

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /product?page=1&size=10&categoryId=5` |
| **URL Chuyển Đổi** | `GET /api/v1/products?page=1&size=10&categoryId=5&sort=name,asc&search=nike` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `categoryId` (optional), `sort` (optional: name,asc / price,desc), `search` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: { page, size, total, pages, hasNext, hasPrevious } }, timestamp, path }` |
| **Phân Quyền** | Public |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.2 Chi Tiết Sản Phẩm

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /product/{productId}` |
| **URL Chuyển Đổi** | `GET /api/v1/products/{productId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, name, price, description, categoryId, ... }, timestamp, path }` |
| **Phân Quyền** | Public |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.3 Tạo Sản Phẩm

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /product` |
| **URL Chuyển Đổi** | `POST /api/v1/products` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Sản phẩm được tạo thành công", data: { id, name, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.4 Cập Nhật Sản Phẩm

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /product/{productId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/products/{productId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Sản phẩm được cập nhật thành công", data: { id, name, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.5 Xóa Sản Phẩm

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /product/{productId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/products/{productId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Sản phẩm được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.6 Quản Lý Màu Sắc Sản Phẩm - Tạo

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /product/product-color` |
| **URL Chuyển Đổi** | `POST /api/v1/products/colors` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message, data: { id, productId, colorName, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.7 Quản Lý Màu Sắc Sản Phẩm - Cập Nhật

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /product/product-color/{productColorId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/products/colors/{productColorId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message, data: { id, colorName, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.8 Quản Lý Màu Sắc Sản Phẩm - Xóa

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /product/product-color/{productColorId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/products/colors/{productColorId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Màu sắc được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.9 Quản Lý Chi Tiết Sản Phẩm - Tạo

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /product/product-color/{productColorId}/product-detail` |
| **URL Chuyển Đổi** | `POST /api/v1/products/colors/{productColorId}/details` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message, data: { id, size, quantity, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.10 Quản Lý Chi Tiết Sản Phẩm - Cập Nhật

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /product/product-color/{productColorId}/product-detail/{productDetailId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/products/colors/{productColorId}/details/{productDetailId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message, data: { id, size, quantity, ... }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

### 2.11 Quản Lý Chi Tiết Sản Phẩm - Xóa

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /product/product-color/{productColorId}/product-detail/{productDetailId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/products/colors/{productColorId}/details/{productDetailId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Chi tiết sản phẩm được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ProductController.java` |

---

## 3️⃣ ORDER ENDPOINTS

### 3.1 Tạo Đơn Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /orders` |
| **URL Chuyển Đổi** | `POST /api/v1/orders` |
| **Tham Số Query String** | Không có (customerId từ JWT token) |
| **Response Body** | `{ success: true, code: 201, message: "Đơn hàng được tạo", data: { id, customerId, totalPrice, status, items: [] }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

### 3.2 Chi Tiết Đơn Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /orders/{orderId}` |
| **URL Chuyển Đổi** | `GET /api/v1/orders/{orderId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, customerId, totalPrice, status, items: [], createdDate }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER, ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

### 3.3 Đơn Hàng Của Khách Hàng (Cá Nhân)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /orders/me?page=1&size=10` |
| **URL Chuyển Đổi** | `GET /api/v1/customers/me/orders?page=1&size=10&sort=-createdDate&search=` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `sort` (optional), `search` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

### 3.4 Tất Cả Đơn Hàng (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /orders?page=1&size=10` |
| **URL Chuyển Đổi** | `GET /api/v1/admin/orders?page=1&size=10&status=PLACED&sort=-createdDate&search=` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `status` (optional: PLACED/PROCESSING/SHIPPED/DELIVERED), `sort` (optional), `search` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

### 3.5 Cập Nhật Trạng Thái Đơn Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PATCH /orders/{orderId}` (với status trong body) |
| **URL Chuyển Đổi** | `PUT /api/v1/orders/{orderId}/status` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Trạng thái đơn hàng được cập nhật", data: { id, status }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

### 3.6 Hủy Đơn Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có (có thể xử lý bằng cập nhật status) |
| **URL Chuyển Đổi** | `DELETE /api/v1/orders/{orderId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Đơn hàng được hủy thành công" }` |
| **Phân Quyền** | CUSTOMER (chỉ hủy đơn hàng của mình), ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/OrderController.java` |

---

## 4️⃣ CART ENDPOINTS

### 4.1 Xem Giỏ Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /customer/cart` |
| **URL Chuyển Đổi** | `GET /api/v1/customers/me/cart` |
| **Tham Số Query String** | Không có (customerId từ JWT token) |
| **Response Body** | `{ success, code: 200, message, data: { id, customerId, items: [], totalPrice }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CartController.java` |

### 4.2 Thêm Vào Giỏ Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /customer/cart/items` |
| **URL Chuyển Đổi** | `POST /api/v1/customers/me/cart/items` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Thêm vào giỏ hàng thành công", data: { id, productId, quantity, price }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CartController.java` |

### 4.3 Cập Nhật Giỏ Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PATCH /customer/cart/items/{cartDetailId}?quantity=5` |
| **URL Chuyển Đổi** | `PATCH /api/v1/customers/me/cart/items/{cartDetailId}` |
| **Tham Số Query String** | `quantity` (trong request body) |
| **Response Body** | `{ success: true, code: 200, message: "Giỏ hàng được cập nhật", data: { id, quantity, price }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CartController.java` |

### 4.4 Xóa Khỏi Giỏ Hàng

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /customer/cart/items/{cartDetailId}` (cần kiểm tra code) |
| **URL Chuyển Đổi** | `DELETE /api/v1/customers/me/cart/items/{cartDetailId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Xóa khỏi giỏ hàng thành công" }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CartController.java` |

---

## 5️⃣ CATEGORY ENDPOINTS

### 5.1 Liệt Kê Danh Mục

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /category?page=1&size=10` |
| **URL Chuyển Đổi** | `GET /api/v1/categories?page=1&size=10&sort=name,asc&search=` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `sort` (optional), `search` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | Public |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CategoryController.java` |

### 5.2 Chi Tiết Danh Mục

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /category/{categoryId}` |
| **URL Chuyển Đổi** | `GET /api/v1/categories/{categoryId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, name, description, status }, timestamp, path }` |
| **Phân Quyền** | Public |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CategoryController.java` |

### 5.3 Tạo Danh Mục

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /category` |
| **URL Chuyển Đổi** | `POST /api/v1/categories` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Danh mục được tạo", data: { id, name, description }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CategoryController.java` |

### 5.4 Cập Nhật Danh Mục

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /category/{categoryId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/categories/{categoryId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Danh mục được cập nhật", data: { id, name, description }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CategoryController.java` |

### 5.5 Xóa Danh Mục

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /category/{categoryId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/categories/{categoryId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Danh mục được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CategoryController.java` |

---

## 6️⃣ REVIEW ENDPOINTS

### 6.1 Liệt Kê Đánh Giá (Theo Sản Phẩm)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /product/{productId}/reviews?page=1&size=5` |
| **URL Chuyển Đổi** | `GET /api/v1/products/{productId}/reviews?page=1&size=10&sort=-createdDate&rating=4` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `sort` (optional), `rating` (optional: 1-5) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | Public |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ReviewController.java` |

### 6.2 Tạo Đánh Giá

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /product/{productId}/reviews` |
| **URL Chuyển Đổi** | `POST /api/v1/products/{productId}/reviews` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Đánh giá được tạo", data: { id, rating, content, createdDate }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ReviewController.java` |

### 6.3 Cập Nhật Đánh Giá

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /product/{productId}/reviews/{reviewId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/products/{productId}/reviews/{reviewId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Đánh giá được cập nhật", data: { id, rating, content }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER (chỉ cập nhật đánh giá của mình), ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ReviewController.java` |

### 6.4 Xóa Đánh Giá

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /product/{productId}/reviews/{reviewId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/products/{productId}/reviews/{reviewId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Đánh giá được xóa thành công" }` |
| **Phân Quyền** | CUSTOMER (chỉ xóa đánh giá của mình), ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ReviewController.java` |

---

## 7️⃣ CUSTOMER ENDPOINTS

### 7.1 Thông Tin Khách Hàng (Cá Nhân)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /customer` (hoặc `PUT /customer/me`) |
| **URL Chuyển Đổi** | `GET /api/v1/customers/me` |
| **Tham Số Query String** | Không có (customerId từ JWT token) |
| **Response Body** | `{ success, code: 200, message, data: { id, username, email, fullName, phone, address }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CustomerController.java` |

### 7.2 Cập Nhật Thông Tin Khách Hàng (Cá Nhân)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /customer/me` |
| **URL Chuyển Đổi** | `PUT /api/v1/customers/me` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Thông tin được cập nhật", data: { id, fullName, phone, address }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CustomerController.java` |

### 7.3 Liệt Kê Khách Hàng (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /customer?page=1&size=10` |
| **URL Chuyển Đổi** | `GET /api/v1/admin/customers?page=1&size=10&sort=createdDate,desc&search=` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `sort` (optional), `search` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CustomerController.java` |

### 7.4 Chi Tiết Khách Hàng (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /customer/{customerId}` |
| **URL Chuyển Đổi** | `GET /api/v1/admin/customers/{customerId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, username, email, fullName, phone, createdDate }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CustomerController.java` |

### 7.5 Xóa Khách Hàng (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /customer/{customerId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/admin/customers/{customerId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Khách hàng được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/CustomerController.java` |

---

## 8️⃣ PROMOTION ENDPOINTS

### 8.1 Tạo Khuyến Mãi

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /admin/promotion` |
| **URL Chuyển Đổi** | `POST /api/v1/admin/promotions` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Khuyến mãi được tạo", data: { id, name, description, discountValue, startDate, endDate }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionController.java` |

### 8.2 Liệt Kê Khuyến Mãi (Quản Trị Viên)

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `GET /api/v1/admin/promotions?page=1&size=10&sort=-createdDate&status=ACTIVE` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `status` (optional: ACTIVE/INACTIVE/EXPIRED), `sort` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionController.java` |

### 8.3 Chi Tiết Khuyến Mãi

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `GET /api/v1/admin/promotions/{promotionId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, name, discountValue, startDate, endDate, actions: [], conditions: [] }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionController.java` |

### 8.4 Cập Nhật Khuyến Mãi

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `PUT /api/v1/admin/promotions/{promotionId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Khuyến mãi được cập nhật", data: { id, name, discountValue }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionController.java` |

### 8.5 Xóa Khuyến Mãi

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❌ Chưa có |
| **URL Chuyển Đổi** | `DELETE /api/v1/admin/promotions/{promotionId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Khuyến mãi được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionController.java` |

---

## 9️⃣ MEMBERSHIP TIER ENDPOINTS

### 9.1 Liệt Kê Hạng Thành Viên

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /membership-tier?page=1&size=10` |
| **URL Chuyển Đổi** | `GET /api/v1/admin/membership-tiers?page=1&size=10&sort=minPoints,asc` |
| **Tham Số Query String** | `page` (default: 1), `size` (default: 10), `sort` (optional) |
| **Response Body** | `{ success, code: 200, message, data: { content: [], pagination: {} }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/MembershipTierController.java` |

### 9.2 Chi Tiết Hạng Thành Viên

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `GET /membership-tier/{tierId}` |
| **URL Chuyển Đổi** | `GET /api/v1/admin/membership-tiers/{tierId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 200, message, data: { id, name, minPoints, discountPercentage }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/MembershipTierController.java` |

### 9.3 Tạo Hạng Thành Viên

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `POST /membership-tier` |
| **URL Chuyển Đổi** | `POST /api/v1/admin/membership-tiers` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 201, message: "Hạng thành viên được tạo", data: { id, name, minPoints }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/MembershipTierController.java` |

### 9.4 Cập Nhật Hạng Thành Viên

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `PUT /membership-tier/{tierId}` |
| **URL Chuyển Đổi** | `PUT /api/v1/admin/membership-tiers/{tierId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Hạng thành viên được cập nhật", data: { id, name, minPoints }, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/MembershipTierController.java` |

### 9.5 Xóa Hạng Thành Viên

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | `DELETE /membership-tier/{tierId}` |
| **URL Chuyển Đổi** | `DELETE /api/v1/admin/membership-tiers/{tierId}` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success: true, code: 200, message: "Hạng thành viên được xóa thành công" }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/MembershipTierController.java` |

---

## 🔟 OTHER ENDPOINTS

### 10.1 FileUploadController

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❓ Cần kiểm tra file |
| **URL Chuyển Đổi** | `POST /api/v1/files/upload` |
| **Tham Số Query String** | Không có (multipart form data) |
| **Response Body** | `{ success: true, code: 201, message, data: { filename, url, size } }` |
| **Phân Quyền** | CUSTOMER, ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/FileUploadController.java` |

### 10.2 PromotionGroupController

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❓ Cần kiểm tra file |
| **URL Chuyển Đổi** | `GET/POST/PUT/DELETE /api/v1/admin/promotion-groups` |
| **Tham Số Query String** | `page`, `size`, `sort` (cho GET) |
| **Response Body** | `{ success, code: 200/201/204, message, data, timestamp, path }` |
| **Phân Quyền** | ADMIN |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/PromotionGroupController.java` |

### 10.3 VoucherWalletController

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❓ Cần kiểm tra file |
| **URL Chuyển Đổi** | `GET/POST /api/v1/customers/me/vouchers` |
| **Tham Số Query String** | `page`, `size` (cho GET) |
| **Response Body** | `{ success, code: 200/201, message, data, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/VoucherWalletController.java` |

### 10.4 ZaloPayController

| Cột | Nội Dung |
|-----|---------|
| **URL Gốc** | ❓ Cần kiểm tra file |
| **URL Chuyển Đổi** | `POST /api/v1/payments/zalopay/create` |
| **Tham Số Query String** | Không có |
| **Response Body** | `{ success, code: 201, message, data: { paymentUrl, transactionId }, timestamp, path }` |
| **Phân Quyền** | CUSTOMER |
| **File Cần Sửa** | `src/main/java/com/example/clothingstore/controller/ZaloPayController.java` |

---

## 📋 TÓM TẮT THAY ĐỔI

| Số Thứ Tự | Tên | Hiện Tại | Chuyển Đổi | Thay Đổi |
|-----------|-----|---------|-----------|---------|
| 1 | AuthController | `auth` | `/api/v1/auth` | Thêm tiền tố + POST /logout + POST /refresh |
| 2 | ProductController | `product` | `/api/v1/products` | Thêm tiền tố + số nhiều + Cấu trúc con resources |
| 3 | OrderController | `/orders` | `/api/v1/orders` | Cấu trúc lại GET /me → /api/v1/customers/me/orders |
| 4 | CartController | `customer/cart` | `/api/v1/customers/me/cart` | Cấu trúc lại theo REST tiêu chuẩn |
| 5 | CategoryController | `/category` | `/api/v1/categories` | Thêm tiền tố + số nhiều |
| 6 | ReviewController | `/product/{id}/reviews` | `/api/v1/products/{id}/reviews` | Thêm tiền tố + số nhiều |
| 7 | CustomerController | `/customer` | `/api/v1/customers/me` và `/api/v1/admin/customers` | Phân tách customer/admin |
| 8 | PromotionController | `/admin/promotion` | `/api/v1/admin/promotions` | Thêm tiền tố + số nhiều + CRUD endpoints |
| 9 | MembershipTierController | `/membership-tier` | `/api/v1/admin/membership-tiers` | Thêm tiền tố + admin path |
| 10 | FileUploadController | ❓ | `/api/v1/files/upload` | Cần kiểm tra |
| 11 | PromotionGroupController | ❓ | `/api/v1/admin/promotion-groups` | Cần kiểm tra |
| 12 | VoucherWalletController | ❓ | `/api/v1/customers/me/vouchers` | Cần kiểm tra |
| 13 | ZaloPayController | ❓ | `/api/v1/payments/zalopay` | Cần kiểm tra |

---

## ✅ DANH SÁCH KIỂM TRA TRIỂN KHAI

- [ ] Cập nhật AuthController 
- [ ] Cập nhật ProductController
- [ ] Cập nhật OrderController
- [ ] Cập nhật CartController
- [ ] Cập nhật CategoryController
- [ ] Cập nhật ReviewController
- [ ] Cập nhật CustomerController
- [ ] Cập nhật PromotionController
- [ ] Cập nhật MembershipTierController
- [ ] Kiểm tra và cập nhật các controller khác
- [ ] Tạo Response/ErrorResponse wrapper mới
- [ ] Triển khai GlobalExceptionHandler
- [ ] Cập nhật tất cả trả về HTTP status codes chính xác
- [ ] Cập nhật Swagger/OpenAPI documentation
- [ ] Kiểm tra tất cả endpoints
- [ ] Triển khai production

---

## 📝 GHI CHÚ

1. **Tất cả endpoints yêu cầu xác thực (trừ public)** phải được bào vệ bằng JWT token trong header: `Authorization: Bearer {token}`

2. **Response format tiêu chuẩn** cho tất cả endpoints:
   ```json
   {
     "success": boolean,
     "code": number (HTTP status),
     "message": string,
     "data": any (tùy endpoint),
     "timestamp": ISO datetime,
     "path": request path
   }
   ```

3. **Pagination tiêu chuẩn**:
   - `page`: 1-indexed (user-friendly)
   - `size`: items per page
   - Response chứa: `{ content: [], pagination: { page, size, total, pages, hasNext, hasPrevious } }`

4. **HTTP Status Codes**:
   - `200`: GET/PUT/PATCH thành công
   - `201`: POST thành công (tạo resource)
   - `400`: Bad Request (validation error)
   - `401`: Unauthorized (missing/invalid auth)
   - `403`: Forbidden (valid auth but no permission)
   - `404`: Not Found
   - `409`: Conflict (business rule violation)

5. **Tham số query string chung cho GET (list)**:
   - `page`: số trang (mặc định: 1)
   - `size`: kích thước trang (mặc định: 10, tối đa: 100)
   - `sort`: cột sắp xếp (format: `-price,name` = price DESC, name ASC)
   - `search`: từ khóa tìm kiếm (tùy chọn)

---

**Cập nhật lần cuối: 2026-02-26**
