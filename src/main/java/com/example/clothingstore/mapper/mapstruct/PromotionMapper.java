// package com.example.clothingstore.mapper.mapstruct;

// import org.mapstruct.Mapper;

// @Mapper(componentModel = "spring", uses = {
// DiscountMapper.class, GiftMapper.class
// })
// public interface PromotionMapper {

// }

// // Case này cần xem lại logic vì Promotion có thể là Discount hoặc Gift nên sẽ
// // có nhiều trường hợp khác nhau, nếu dùng MapStruct thì sẽ phải viết nhiều
// // method để xử lý từng trường hợp, điều này sẽ làm code trở nên phức tạp và khó
// // bảo trì. Thay vào đó, có thể sử dụng một interface chung cho Promotion và sau
// // đó implement các class cụ thể cho Discount và Gift, như vậy sẽ dễ dàng hơn
// // trong việc quản lý và mở rộng sau này.