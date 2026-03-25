### Tạo đơn hàng dựa trên cái preview lần này --> nên gửi cái data của preview lên và check lại nếu khớp thì ok nếu sai thì báo lỗi để khách hàng không bị hớ

### NOTE: Chỉnh sửa logic của endpoint mua ngay --> từ không thông qua giỏ hàng

#### Đơn hàng khi được tạo sẽ được gửi kèm giá hiện tại + ds các khuyến mãi đã áp dụng nếu giá hiện tại bị chênh lệch hoặc ds khuyến mãi hết hạn thì báo lỗi liền

# Trường hợp nếu mà thanh toán k được bằng zalo pay thì cho đổi phương thức thanh toán

---

## TEST TRƯỜNG HỢP GIẢM GIÁ CHO SẢN PHẨM

## Chưa chỉnh logic khi hủy đơn thì trả lại voucher --> done

## Tạo logic update đơn hàng - update trả hàng

// ĐANG LM LOGIC TẠO PHIẾU TRẢ HÀNG

// Ngày 19.3.2026:
// Thêm type vào jwt để phân biệt refresh token và access token
// Điều chỉnh fundResonseDTO --> done
// Thêm endpoint Update


// Ngày 25/3/2026: Test logic của tạo đơn khi có discount và lưu cái promotion của discount 
