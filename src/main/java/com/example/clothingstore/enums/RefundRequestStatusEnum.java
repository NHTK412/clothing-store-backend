package com.example.clothingstore.enums;

public enum RefundRequestStatusEnum {
    PENDING, // Mới tạo, chưa được xử lý
    APPROVED, // Đã được duyệt, đang chờ hoàn tiền
    REJECTED, // Đã bị từ chối
    COMPLETED, // Hoàn tiền đã được thực hiện thành công
    CANCEL // Yêu cầu hoàn tiền đã bị hủy bởi khách hàng hoặc hệ thống

}
