# Đề Xuất Cải Thiện Cấu Trúc Code - Code Cleanup & Refactoring

## 1. TYPO VÀ LỖI ĐẶT TÊN (Critical Issues)

### 1.1 Sai Chính Tả File
| Vị Trí | Tên Hiện Tại | Tên Đề Xuất | Ưu Điểm |
|--------|-----------|-----------|---------|
| `exception/` | `GolbalExceptionHandler.java` | `GlobalExceptionHandler.java` | Sửa typo "Golbal" → "Global" |

### 1.2 Naming Convention Interface
| Vị Trí | Tên Hiện Tại | Tên Đề Xuất | Ưu Điểm |
|--------|-----------|-----------|---------|
| `scope/` | `IPromotionScopeStrategy.java` | `PromotionScopeStrategy.java` | Tuân theo Java convention (không sử dụng tiền tố "I") |

---

## 2. CẤU TRÚC PACKAGE - DTO PACKAGE QUỐC NỘI

### 2.1 Vấn Đề Hiện Tại
**Cấu trúc DTO hiện tại quá phức tạp với 21 subfolder:**
```
dto/
├── account/
├── auth/
├── cart/
├── cartdetail/
├── category/
├── customer/
├── discount/
├── fileupload/
├── gift/
├── membershiptier/
├── order/
├── orderdetail/
├── ordergift/
├── product/
├── productcolor/
├── productdetail/
├── productvariant/
├── promotion/
├── review/
├── shippingaddress/
└── zalopay/
```

**Các vấn đề:**
- ✗ Quá nhiều subfolder (21 package con) → Khó bảo trì
- ✗ Dấu hiệu của code smell "Folder Per DTO"
- ✗ Khó khoanh vùng các DTO liên quan (VD: order, orderdetail, ordergift nên một nhóm)
- ✗ Dẫn tới thiếu tính cohesive trong team

### 2.2 Đề Xuất Cấu Trúc Mới
Nhóm các DTO theo domain business logic thay vì 1 file = 1 folder:

```
dto/
├── account/          # Account + Admin DTOs
│   ├── AccountDTO.java
│   ├── AdminDTO.java
│   └── ...
├── auth/             # Giữ nguyên, vì liên quan đến security
│   ├── AuthRequestDTO.java
│   └── AuthResponseDTO.java
├── product/          # Gom: product, productcolor, productdetail, productvariant
│   ├── ProductDTO.java
│   ├── ProductColorDTO.java
│   ├── ProductDetailDTO.java
│   ├── ProductVariantDTO.java
│   └── ...
├── order/            # Gom: order, orderdetail, ordergift
│   ├── OrderDTO.java
│   ├── OrderRequestDTO.java
│   ├── OrderResponseDTO.java
│   ├── OrderDetailDTO.java
│   ├── OrderDetailRequestDTO.java
│   ├── OrderDetailResponseDTO.java
│   ├── OrderGiftDTO.java
│   └── OrderSummaryDTO.java
├── cart/             # Gom: cart, cartdetail
│   ├── CartDTO.java
│   ├── CartRequestDTO.java
│   ├── CartItemDTO.java
│   └── CartDetailDTO.java
├── customer/         # Giữ nguyên
│   ├── CustomerDTO.java
│   └── ...
├── category/         # Giữ nguyên
│   ├── CategoryDTO.java
│   └── ...
├── promotion/        # Giữ nguyên
│   ├── PromotionDTO.java
│   ├── PromotionGroupDTO.java
│   └── ...
├── review/           # Giữ nguyên
│   ├── ReviewDTO.java
│   └── ...
├── address/          # Đặt lại tên từ shippingaddress (ngắn gọn)
│   ├── ShippingAddressDTO.java
│   └── ...
├── membership/       # Đặt lại tên từ membershiptier
│   ├── MembershipTierDTO.java
│   └── ...
├── discount/         # Giữ nguyên
│   ├── DiscountDTO.java
│   └── ...
├── gift/             # Giữ nguyên
│   ├── GiftDTO.java
│   └── ...
├── wallet/           # Đặt lại tên từ voucherwallet
│   ├── VoucherWalletDTO.java
│   └── ...
├── payment/          # Tạo folder mới cho payment-related DTOs
│   ├── ZaloPayDTO.java
│   ├── PaymentResponseDTO.java
│   └── ...
└── fileupload/       # Giữ nguyên
    ├── FileUploadDTO.java
    └── ...
```

**Lợi ích của cấu trúc mới:**
- ✓ Giảm từ 21 package xuống ~15 package (ngắn hơn, rõ ràng hơn)
- ✓ Dễ tìm các DTO liên quan đến một feature
- ✓ Dễ thêm DTO mới mà không tạo thêm subfolder
- ✓ Dễ nhìn và dễ navigate cho team members

---

## 3. CẤU TRÚC PACKAGE - MAPPER PACKAGE

### 3.1 Vấn đề Hiện Tại
```
mapper/
├── CartDetailMapper.java
├── CartMapper.java
├── CategoryMapper.java
├── CustomerMapper.java
├── DiscountMapper.java
├── GiftMapper.java
├── mapstruct/            # ← Subfolder này không rõ mục đích
├── MembershipTierMapper.java
├── ProductDetailMapper.java
├── ProductMapper.java
├── PromotionGroupMapper.java
├── PromotionMapper.java
├── ReviewMapper.java
└── ShippingAddressMapper.java
```

**Các vấn đề:**
- ✗ Folder `mapstruct/` không rõ mục đích
- ✗ Mapper classes lẻ tẻ, không có công thức tổ chức

### 3.2 Đề Xuất Cấu Trúc Mới
```
mapper/
├── impl/                 # Mapper implementations
│   ├── CartMapperImpl.java
│   ├── OrderMapperImpl.java
│   ├── ProductMapperImpl.java
│   └── ...
├── CartMapper.java       # Interfaces (nếu dùng MapStruct)
├── OrderMapper.java
├── ProductMapper.java
├── CustomerMapper.java
├── CategoryMapper.java
├── ReviewMapper.java
├── PromotionMapper.java
├── PromotionGroupMapper.java
├── PromotionTargetUserMapper.java
├── PromotionMemberTierMapper.java
├── MembershipTierMapper.java
├── ShippingAddressMapper.java
├── DiscountMapper.java
└── GiftMapper.java
```

**Hoặc nếu muốn gom theo domain (giống DTO):**
```
mapper/
├── product/
│   ├── ProductMapper.java
│   ├── ProductDetailMapper.java
│   └── ProductColorMapper.java
├── order/
│   ├── OrderMapper.java
│   ├── OrderDetailMapper.java
│   └── OrderGiftMapper.java
├── cart/
│   ├── CartMapper.java
│   └── CartDetailMapper.java
├── customer/
│   └── CustomerMapper.java
├── review/
│   └── ReviewMapper.java
├── promotion/
│   ├── PromotionMapper.java
│   ├── PromotionGroupMapper.java
│   └── PromotionMemberTierMapper.java
└── ...
```

---

## 4. CẤU TRÚC PACKAGE - EXCEPTION PACKAGE

### 4.1 Vấn đề Hiện Tại
```
exception/
├── customer/
│   ├── ConflictException.java
│   ├── NotFoundException.java
│   └── ...
└── GolbalExceptionHandler.java
```

**Các vấn đề:**
- ✗ Exception classes được đặt trong `customer/` folder nhưng được sử dụng ở nhiều nơi
- ✗ Tên folder `customer/` không phản ánh đúng mục đích của exceptions
- ✗ GlobalExceptionHandler nên có naming rõ ràng

### 4.2 Đề Xuất Cấu Trúc Mới
```
exception/
├── GlobalExceptionHandler.java    # Sửa typo: Golbal → Global
├── ApiException.java              # Base exception class (nếu chưa có)
├── business/                      # Business logic exceptions
│   ├── ConflictException.java
│   ├── NotFoundException.java
│   ├── ValidationException.java
│   ├── BusinessRuleException.java
│   └── ...
├── security/                      # Security exceptions
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── ...
├── validation/                    # Validation exceptions (nếu cần tách)
│   └── InvalidInputException.java
└── handler/                       # Exception handlers/advisors
    └── GlobalExceptionAdvice.java
```

---

## 5. PROMOTION STRATEGY PATTERN - ARCHITECTURE (NEW - CRITICAL INSIGHT)

### 5.1 Nhận Xét Quan Trọng
Các package `action/`, `condition/`, `scope/` đều tuân theo cùng một pattern:
- **Interface cơ bản** → múltiple implementations → **Factory class**

```
action/
├── PromotionActionStrategy (interface)
├── PromotionActionFactory
└── FreeProductStrategy, FreeShipStrategy, PercentDiscountStrategy (implementations)

condition/
├── PromotionConditionStrategy (interface)
├── PromotionConditionFactory
└── MinOrderAmountStrategy, MinQuantityConditionStrategy (implementations)

scope/
├── IPromotionScopeStrategy (interface)  ← Naming issue
├── PromotionScopeFactory
└── AllUserScopeStrategy, MembershipRankScopeStrategy, SpecificUserScopeStrategy (implementations)
```

**Pattern:** Strategy Pattern + Factory Pattern

### 5.2 Vấn Đề Hiện Tại
1. ✗ Ba packages riêng lẻ nhưng logic rất liên kết → Khó maintain
2. ✗ `IPromotionScopeStrategy.java` không tuân theo Java convention (tiền tố "I")
3. ✗ Impementations nằm giữa thư mục cùng với factory → Không rõ ràng
4. ✗ Không có cấu trúc chung cho Strategy Pattern → Khó mở rộng

### 5.3 Đề Xuất Cấu Trúc Mới - Centralized Promotion Strategy Architecture

#### **Tùy Chọn A: Gom tất cả vào 1 folder (Recommended)**
```
promotion/
├── strategy/                       # Chứa tất cả strategy-related logic
│   ├── action/
│   │   ├── PromotionActionStrategy.java         # Interface
│   │   ├── PromotionActionFactory.java
│   │   └── impl/
│   │       ├── FreeProductStrategy.java
│   │       ├── FreeShipStrategy.java
│   │       └── PercentDiscountStrategy.java
│   │
│   ├── condition/
│   │   ├── PromotionConditionStrategy.java      # Interface
│   │   ├── PromotionConditionFactory.java
│   │   └── impl/
│   │       ├── MinOrderAmountStrategy.java
│   │       └── MinQuantityConditionStrategy.java
│   │
│   └── scope/
│       ├── PromotionScopeStrategy.java          # Interface (rename từ I...)
│       ├── PromotionScopeFactory.java
│       └── impl/
│           ├── AllUserScopeStrategy.java
│           ├── SpecificUserScopeStrategy.java
│           └── MembershipRankScopeStrategy.java
│
├── engine/                         # (Mục đích của engine/ folder)
│   └── PromotionEngine.java        # Orchestrates strategy execution
│
├── service/
│   ├── PromotionService.java
│   ├── PromotionGroupService.java
│   └── PromotionValidationService.java
│
├── event/                          # (Optional) Event-driven promotion logic
│   ├── PromotionAppliedEvent.java
│   └── PromotionCalculatedEvent.java
│
├── model/
│   ├── Promotion.java
│   ├── PromotionAction.java
│   ├── PromotionCondition.java
│   ├── PromotionGroup.java
│   ├── PromotionMemberTier.java
│   ├── PromotionTargetUser.java
│   └── ...
│
├── dto/
│   ├── PromotionDTO.java
│   ├── PromotionRequestDTO.java
│   ├── PromotionResponseDTO.java
│   ├── PromotionGroupDTO.java
│   └── ...
│
├── mapper/
│   ├── PromotionMapper.java
│   ├── PromotionGroupMapper.java
│   ├── PromotionMemberTierMapper.java
│   └── PromotionTargetUserMapper.java
│
├── repository/
│   ├── PromotionRepository.java
│   ├── PromotionGroupRepository.java
│   ├── PromotionMemberTierRepository.java
│   ├── PromotionTargetUserRepository.java
│   └── ...
│
└── validator/
    └── PromotionValidator.java
```

**Lợi ích:**
- ✓ Rõ ràng: Tất cả strategy logic ở `promotion/strategy/`
- ✓ Dễ bảo trì: Một folder cho tất cả stratgies
- ✓ Dễ mở rộng: Thêm strategy mới → Thêm `/impl/` class
- ✓ Giải quyết `engine/` folder trống
- ✓ Nhóm tất cả promotion logic ở một chỗ (cohesion tốt)

#### **Tùy Chọn B: Giữ nguyên nhưng tổ chức lại**
```
action/
├── PromotionActionStrategy.java
├── PromotionActionFactory.java
└── impl/                           # ← Phân tách implementations
    ├── FreeProductStrategy.java
    ├── FreeShipStrategy.java
    └── PercentDiscountStrategy.java

condition/
├── PromotionConditionStrategy.java
├── PromotionConditionFactory.java
└── impl/                           # ← Phân tách implementations
    ├── MinOrderAmountStrategy.java
    └── MinQuantityConditionStrategy.java

scope/
├── PromotionScopeStrategy.java     # Đổi từ IPromotionScopeStrategy
├── PromotionScopeFactory.java
└── impl/                           # ← Phân tách implementations
    ├── AllUserScopeStrategy.java
    ├── SpecificUserScopeStrategy.java
    └── MembershipRankScopeStrategy.java
```

**Lợi ích:**
- ✓ Nhẹ hơn (không gom lại quá nhiều)
- ✓ Giữ cấu trúc hiện tại nhưng tổ chức rõ
- ✓ Interface + Factory + Implementations trong 1 folder

### 5.4 Đề Xuất Code Template - Generic Strategy Base (Optional)
Nếu muốn tạo shared abstraction cho Strategy Pattern:

```java
// common/strategy/StrategyProvider.java
public interface StrategyProvider<T extends Enum<T>, S> {
    S getStrategy(T type);
}

// common/strategy/BaseStrategyFactory.java
public abstract class BaseStrategyFactory<T extends Enum<T>, S> implements StrategyProvider<T, S> {
    protected final Map<T, S> strategies;
    
    protected BaseStrategyFactory(List<S> strategyList, Function<S, T> typeExtractor) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(typeExtractor, Function.identity()));
    }
    
    @Override
    public S getStrategy(T type) {
        return strategies.get(type);
    }
}
```

Sau đó các Factory cụ thể có thể extend từ `BaseStrategyFactory`.

---

## 6. CẤU TRÚC PACKAGE - ENGINE PACKAGE

### 6.1 Vấn đề Hiện Tại
```
engine/
├── (Folder trống - không có file)
```

**Các vấn đề:**
- ✗ Package trống mà vẫn tồn tại → Code smell
- ✗ Không rõ mục đích của package này
- ✗ **Mục đích thực tế:** Nên chứa orchestrator cho Promotion Engine

### 6.2 Đề Xuất
**Lựa chọn 1 (Recommended):** Tạo structure cho Promotion Engine
```
engine/
├── PromotionEngine.java            # Orchestrates all strategy execution
├── PromotionEvaluationEngine.java  # Evaluates conditions & actions
└── PromotionCalculationEngine.java # Calculates discounts & prices
```

**Lựa chọn 2 (Alternative):** Gom vào promotion/ package
```
promotion/
├── strategy/
│   ├── action/
│   ├── condition/
│   └── scope/
├── engine/                         # Move từ root
│   ├── PromotionEngine.java
│   └── ...
└── ...
```

Các file này sẽ orchestrate việc sử dụng factory patterns từ `action/`, `condition/`, `scope/`.

---

## 7. SECURITY PACKAGE - REORGANIZATION

### 7.1 Vấn đề Hiện Tại
```
security/
└── CustomerUserDetails.java
```

**Các vấn đề:**
- ✗ Chỉ có 1 file trong package lớn
- ✗ Các authentication components khác nằm ở đâu? (JwtAuthFilter, SecurityConfig)
- ✗ Thiếu tính cohesion - security logic phân tán

### 7.2 Đề Xuất Cấu Trúc Mới
```
security/
├── authentication/
│   ├── CustomerUserDetails.java
│   ├── JwtAuthenticationProvider.java
│   └── ...
├── filter/
│   └── JwtAuthFilter.java          # Move từ filter/ package
├── jwt/
│   ├── JwtTokenProvider.java       # Move từ util/
│   └── JwtUtil.java                # Move từ util/
└── config/
    └── SecurityConfig.java         # Move từ config/
```

---

## 8. CONFIG PACKAGE - REORGANIZATION

### 8.1 Vấn đề Hiện Tại
```
config/
├── SecurityConfig.java
├── SwaggerConfig.java
└── ZaloPayConfig.java
```

**Các vấn đề:**
- ✗ Quá phổ quát - SecurityConfig nên ở trong security/ package
- ✗ Validation/Mapper config có nên ở đây?

### 8.2 Đề Xuất Cấu Trúc Mới
```
config/
├── SwaggerConfig.java              # Giữ nguyên (API documentation)
├── payment/
│   └── ZaloPayConfig.java
├── database/                       # Nếu có DB config
│   └── ...
└── jackson/                        # Nếu có JSON serialization config
    └── ...
```

Và move `SecurityConfig.java` sang:
```
security/
└── config/
    └── SecurityConfig.java
```

---

## 9. FILTER PACKAGE - REORGANIZATION

### 9.1 Vấn đề Hiện Tại
```
filter/
└── JwtAuthFilter.java
```

**Các vấn đề:**
- ✗ Chỉ 1 file trong package
- ✗ Filter này liên quan tới security, nên nên ở `security/filter/`

### 9.2 Đề Xuất
Move `JwtAuthFilter.java` vào `security/authentication/filter/` hoặc `security/filter/`

---

## 10. CRYPTO PACKAGE - REORGANIZATION

### 10.1 Vấn đề Hiện Tại
```
crypto/
├── HexStringUtil.java
├── HMACUtil.java
└── RSAUtil.java
```

**Các vấn đề:**
- ✗ Utilities này có thể gom vào `util/` package
- ✗ Crypto utilities không cần riêng 1 package

### 10.2 Đề Xuất
**Lựa chọn 1:** Move vào `util/`
```
util/
├── crypto/
│   ├── HexStringUtil.java
│   ├── HMACUtil.java
│   └── RSAUtil.java
├── JwtUtil.java
└── ApiResponse.java
```

**Lựa chọn 2:** Giữ nguyên nhưng organize lại:
```
security/
├── crypto/
│   ├── HexStringUtil.java
│   ├── HMACUtil.java
│   └── RSAUtil.java
└── ...
```

---

## 11. UTIL PACKAGE - EXPANSION

### 11.1 Vấn đề Hiện Tại
```
util/
├── ApiResponse.java
└── JwtUtil.java
```

**Các vấn đề:**
- ✗ Chỉ có 2 utilities
- ✗ Crypto utilities nên được organize

### 11.2 Đề Xuất Cấu Trúc Mới
```
util/
├── ApiResponse.java                # General API response wrapper
├── string/                         # String utilities
│   └── StringUtil.java
├── date/                           # Date/Time utilities
│   └── DateUtil.java
├── validation/                     # Validation utilities
│   └── ValidationUtil.java
├── collection/                     # Collection utilities
│   └── CollectionUtil.java
└── crypto/                         # Cryptography utilities
    ├── HexStringUtil.java
    ├── HMACUtil.java
    └── RSAUtil.java
```

---

## 12. VALIDATOR PACKAGE - EXPANSION

### 12.1 Vấn đề Hiện Tại
```
validator/
└── PromotionValidator.java
```

**Các vấn đề:**
- ✗ Chỉ có 1 validator cho promotion
- ✗ Có validator khác ở đâu?

### 12.2 Đề Xuất Cấu Trúc Mới
```
validator/
├── PromotionValidator.java
├── OrderValidator.java             # Nếu có
├── ProductValidator.java           # Nếu có
├── CartValidator.java              # Nếu có
└── ...
```

---

## 13. SERVICE PACKAGE - QUẢN LÝ COMPLEXITY

### 13.1 Vấn đề Hiện Tại
```
service/
├── AuthService.java
├── CartService.java
├── CategoryService.java
├── CustomerService.java
├── FileUploadService.java
├── MembershipTierService.java
├── OrderService.java               # ← Service file rất lớn (1000+ lines)
├── ProductService.java
├── PromotionGroupService.java
├── PromotionService.java
├── PromotionValidator.java         # ← Đặt sai chỗ, nên ở validator/
├── ReviewService.java
├── ShippingAddressService.java
├── VoucherWalletService.java
└── ZaloPayService.java
```

**Các vấn đề:**
- ✗ `OrderService.java` rất lớn → Cần chia nhỏ theo Single Responsibility Principle
- ✗ `PromotionValidator.java` nên ở trong `validator/` package, không phải `service/`
- ✗ Order processing, promotion calculation, validation logic nên tách riêng

### 13.2 Đề Xuất Cấu Trúc Mới

#### a. Move `PromotionValidator.java` vào `validator/` package

#### b. Chia nhỏ `OrderService.java`

**Từ structure hiện tại:**
```
service/
└── OrderService.java               # 500+ lines
```

**Sang structure mới:**
```
service/
├── order/
│   ├── OrderService.java           # Main orchestrator service
│   ├── OrderCreationService.java   # Responsible for creating orders
│   ├── OrderStatusService.java     # Responsible for status management
│   ├── OrderPricingService.java    # Responsible for price calculation & validation
│   └── OrderPromotionService.java  # Responsible for applying promotions
├── promotion/
│   ├── PromotionService.java       # Main service
│   ├── PromotionEvaluationService.java
│   ├── PromotionApplicationService.java
│   └── PromotionGroupService.java
├── payment/
│   └── ZaloPayService.java
├── ...
```

**Hoặc nếu prefer domain-driven approach:**
```
order/
├── service/
│   ├── OrderService.java           # Facade/Orchestrator
│   ├── OrderCreationService.java
│   ├── OrderPricingService.java
│   ├── OrderPromotionApplicationService.java
│   └── OrderStatusService.java
├── dto/
│   ├── OrderRequestDTO.java
│   ├── OrderResponseDTO.java
│   └── ...
├── model/
│   ├── Order.java
│   └── OrderDetail.java
├── mapper/
│   └── OrderMapper.java
└── repository/
    └── OrderRepository.java
```

---

## 14. REPOSITORY PACKAGE - REVIEW

### 14.1 Vấn đề Hiện Tại
```
repository/
├── AccountRepository.java
├── AdminRepository.java
├── CartDetailRepository.java
├── CartRepository.java
├── CategoryRepository.java
├── CustomerRepository.java
├── MembershipTierRepository.java
├── OrderDetailRepository.java
├── OrderRepository.java
├── ProductColorRepository.java
├── ProductDetailRepository.java
├── ProductRepository.java
├── PromotionGroupRepository.java
├── PromotionMemberTierRepository.java
├── PromotionRepository.java
├── PromotionTargetUserRepository.java
├── ReviewRepository.java
├── ShippingAddressRepository.java
└── VoucherWalletRepository.java
```

**Các vấn đề:**
- ✗ Có thể tổ chức theo domain (giống như đề xuất cho DTO)

### 14.2 Đề Xuất Cấu Trúc Mới (Optional - nếu cần organize)
```
repository/
├── product/
│   ├── ProductRepository.java
│   ├── ProductColorRepository.java
│   └── ProductDetailRepository.java
├── order/
│   ├── OrderRepository.java
│   └── OrderDetailRepository.java
├── cart/
│   ├── CartRepository.java
│   └── CartDetailRepository.java
├── account/
│   ├── AccountRepository.java
│   └── AdminRepository.java
├── promotion/
│   ├── PromotionRepository.java
│   ├── PromotionGroupRepository.java
│   ├── PromotionMemberTierRepository.java
│   └── PromotionTargetUserRepository.java
└── ...
```

---

## 15. PACKAGE NAMING - TỔNG QUAN

### 15.1 Tên Package Cần Cải Thiện

| Tên Hiện Tại | Tên Đề Xuất | Lý Do |
|--------------|-----------|-------|
| `shippingaddress` | `address` | Ngắn gọn, dễ gọi tên |
| `membershiptier` | `membership` | Ngắn gọn hơn |
| `voucherwallet` | `wallet` | Ngắn gọn hơn |
| `productvariant` | `variant` | Ngắn gọn hơn |
| `ordergift` | Gom vào `order/` | Liên kết chặt chẽ với order |
| `cartdetail` | Gom vào `cart/` | Liên kết chặt chẽ với cart |
| `productdetail` | Gom vào `product/` | Liên kết chặt chẽ với product |
| `productcolor` | Gom vào `product/` | Liên kết chặt chẽ với product |

---

## 16. KIẾN TRÚC PACKAGE RECOMMEND - TỔNG THỂ

### 16.1 Structure Cuối Cùng Recommended

```
com.example.clothingstore/
├── ClothingstoreApplication.java
│
├── config/
│   ├── SwaggerConfig.java
│   ├── payment/
│   │   └── ZaloPayConfig.java
│   └── ...
│
├── dto/
│   ├── auth/
│   ├── product/
│   ├── order/
│   ├── cart/
│   ├── customer/
│   ├── category/
│   ├── promotion/
│   ├── review/
│   ├── address/
│   ├── membership/
│   ├── discount/
│   ├── gift/
│   ├── wallet/
│   ├── payment/
│   └── fileupload/
│
├── model/
│   ├── Account.java
│   ├── Admin.java
│   ├── Base.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── Customer.java
│   ├── Discount.java
│   ├── Gift.java
│   ├── MembershipTier.java
│   ├── Order.java
│   ├── OrderDetail.java
│   ├── OrderGift.java
│   ├── Product.java
│   ├── ProductColor.java
│   ├── ProductDetail.java
│   ├── Promotion.java
│   ├── PromotionAction.java
│   ├── PromotionCondition.java
│   ├── PromotionGroup.java
│   ├── PromotionMemberTier.java
│   ├── PromotionTargetUser.java
│   ├── Review.java
│   ├── ShippingAddress.java
│   └── VoucherWallet.java
│
├── repository/
│   ├── AccountRepository.java
│   ├── AdminRepository.java
│   ├── CartRepository.java
│   ├── CartDetailRepository.java
│   ├── CategoryRepository.java
│   ├── CustomerRepository.java
│   ├── MembershipTierRepository.java
│   ├── OrderRepository.java
│   ├── OrderDetailRepository.java
│   ├── ProductRepository.java
│   ├── ProductColorRepository.java
│   ├── ProductDetailRepository.java
│   ├── PromotionRepository.java
│   ├── PromotionGroupRepository.java
│   ├── PromotionMemberTierRepository.java
│   ├── PromotionTargetUserRepository.java
│   ├── ReviewRepository.java
│   ├── ShippingAddressRepository.java
│   └── VoucherWalletRepository.java
│
├── mapper/
│   ├── cartmapper/
│   │   ├── CartMapper.java
│   │   └── CartDetailMapper.java
│   ├── productmapper/
│   │   ├── ProductMapper.java
│   │   ├── ProductColorMapper.java
│   │   └── ProductDetailMapper.java
│   ├── ordermapper/
│   │   ├── OrderMapper.java
│   │   ├── OrderDetailMapper.java
│   │   └── OrderGiftMapper.java
│   ├── CustomerMapper.java
│   ├── CategoryMapper.java
│   ├── ReviewMapper.java
│   ├── PromotionMapper.java
│   ├── PromotionGroupMapper.java
│   ├── MembershipTierMapper.java
│   ├── DiscountMapper.java
│   ├── GiftMapper.java
│   └── ShippingAddressMapper.java
│
├── service/
│   ├── order/
│   │   ├── OrderService.java           # Orchestrator
│   │   ├── OrderCreationService.java
│   │   ├── OrderPricingService.java
│   │   ├── OrderPromotionService.java
│   │   └── OrderStatusService.java
│   ├── promotion/
│   │   ├── PromotionService.java
│   │   ├── PromotionEvaluationService.java
│   │   ├── PromotionApplicationService.java
│   │   └── PromotionGroupService.java
│   ├── product/
│   │   ├── ProductService.java
│   │   ├── ProductColorService.java
│   │   └── ProductDetailService.java
│   ├── cart/
│   │   └── CartService.java
│   ├── customer/
│   │   └── CustomerService.java
│   ├── category/
│   │   └── CategoryService.java
│   ├── review/
│   │   └── ReviewService.java
│   ├── auth/
│   │   └── AuthService.java
│   ├── payment/
│   │   └── ZaloPayService.java
│   ├── file/
│   │   └── FileUploadService.java
│   ├── membership/
│   │   └── MembershipTierService.java
│   ├── address/
│   │   └── ShippingAddressService.java
│   └── wallet/
│       └── VoucherWalletService.java
│
├── controller/
│   ├── AccountController.java
│   ├── AuthController.java
│   ├── CartController.java
│   ├── CategoryController.java
│   ├── CustomerController.java
│   ├── FileUploadController.java
│   ├── MembershipTierController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   ├── PromotionController.java
│   ├── PromotionGroupController.java
│   ├── ReviewController.java
│   ├── ShippingAddressController.java
│   ├── VoucherWalletController.java
│   └── ZaloPayController.java
│
├── enums/
│   ├── AccountStatusEnum.java
│   ├── CategoryStatusEnum.java
│   ├── GenderEnum.java
│   ├── OrderPaymentStatusEnum.java
│   ├── OrderStatusEnum.java
│   ├── PaymentMethodEnum.java
│   ├── PromotionActionTypeEnum.java
│   ├── PromotionApplicationTypeEnum.java
│   ├── PromotionConditionTypeEnum.java
│   ├── PromotionScopeTypeEnum.java
│   ├── PromotionTypeEnum.java
│   ├── RoleEnum.java
│   └── StatusEnum.java
│
├── promotion/                      # ═══ CENTRALIZED PROMOTION PACKAGE ═══
│   ├── strategy/
│   │   ├── action/
│   │   │   ├── PromotionActionStrategy.java        # Interface
│   │   │   ├── PromotionActionFactory.java
│   │   │   └── impl/
│   │   │       ├── FreeProductStrategy.java
│   │   │       ├── FreeShipStrategy.java
│   │   │       └── PercentDiscountStrategy.java
│   │   │
│   │   ├── condition/
│   │   │   ├── PromotionConditionStrategy.java     # Interface
│   │   │   ├── PromotionConditionFactory.java
│   │   │   └── impl/
│   │   │       ├── MinOrderAmountStrategy.java
│   │   │       └── MinQuantityConditionStrategy.java
│   │   │
│   │   └── scope/
│   │       ├── PromotionScopeStrategy.java         # Interface (rename từ I...)
│   │       ├── PromotionScopeFactory.java
│   │       └── impl/
│   │           ├── AllUserScopeStrategy.java
│   │           ├── SpecificUserScopeStrategy.java
│   │           └── MembershipRankScopeStrategy.java
│   │
│   ├── engine/
│   │   ├── PromotionEngine.java
│   │   ├── PromotionEvaluationEngine.java
│   │   └── PromotionCalculationEngine.java
│   │
│   ├── repository/
│   │   ├── PromotionRepository.java
│   │   ├── PromotionGroupRepository.java
│   │   ├── PromotionMemberTierRepository.java
│   │   └── PromotionTargetUserRepository.java
│   │
│   ├── mapper/
│   │   ├── PromotionMapper.java
│   │   ├── PromotionGroupMapper.java
│   │   ├── PromotionMemberTierMapper.java
│   │   └── PromotionTargetUserMapper.java
│   │
│   ├── dto/
│   │   ├── PromotionDTO.java
│   │   ├── PromotionRequestDTO.java
│   │   ├── PromotionResponseDTO.java
│   │   ├── PromotionGroupDTO.java
│   │   └── ...
│   │
│   ├── service/
│   │   ├── PromotionService.java
│   │   ├── PromotionGroupService.java
│   │   ├── PromotionEvaluationService.java
│   │   ├── PromotionApplicationService.java
│   │   └── PromotionValidationService.java
│   │
│   └── validator/
│       └── PromotionValidator.java
│
├── security/
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── authentication/
│   │   ├── CustomerUserDetails.java
│   │   └── JwtAuthenticationProvider.java
│   ├── filter/
│   │   └── JwtAuthFilter.java
│   ├── jwt/
│   │   └── JwtUtil.java
│   └── crypto/
│       ├── HexStringUtil.java
│       ├── HMACUtil.java
│       └── RSAUtil.java
│
├── exception/
│   ├── GlobalExceptionHandler.java       # Sửa typo
│   ├── ApiException.java                 # Base exception
│   ├── business/
│   │   ├── ConflictException.java
│   │   ├── NotFoundException.java
│   │   ├── ValidationException.java
│   │   └── BusinessRuleException.java
│   └── security/
│       ├── UnauthorizedException.java
│       └── ForbiddenException.java
│
├── validator/
│   ├── OrderValidator.java
│   ├── ProductValidator.java
│   └── CartValidator.java
│
└── util/
    ├── ApiResponse.java
    ├── string/
    │   └── StringUtil.java
    ├── date/
    │   └── DateUtil.java
    ├── validation/
    │   └── ValidationUtil.java
    └── collection/
        └── CollectionUtil.java
```

---

## 17. HÀNH ĐỘNG CỤ THỂ - PRIORITIZED TASKS

### 🔴 **CRITICAL (Làm ngay)**
1. ✅ Sửa typo: `GolbalExceptionHandler.java` → `GlobalExceptionHandler.java`
2. ✅ Đổi tên interface: `IPromotionScopeStrategy.java` → `PromotionScopeStrategy.java` (Java convention không dùng tiền tố "I")
3. ✅ **Reorganize Promotion Strategy Architecture (NEW - CRITICAL):**
   - Move `action/`, `condition/`, `scope/` vào `promotion/strategy/`
   - Tạo `promotion/strategy/action/impl/`, `promotion/strategy/condition/impl/`, `promotion/strategy/scope/impl/` subfolder cho implementations
   - Move `PromotionValidator.java` từ `service/` sang `promotion/validator/`
   - Đổi tên `IPromotionScopeStrategy.java` → `PromotionScopeStrategy.java`
4. ✅ Reorganize DTO package (giảm từ 21 subfolder xuống ~15)

### 🟠 **HIGH (Làm trong sprint tiếp theo)**
5. Tạo `promotion/engine/` package với:
   - `PromotionEngine.java` (Orchestrator)
   - `PromotionEvaluationEngine.java`
   - `PromotionCalculationEngine.java`
6. Chia nhỏ `OrderService.java` thành các service nhỏ hơn
7. Reorganize `mapper/` package theo domain
8. Reorganize `security/` package (gom authentication, filter, crypto)

### 🟡 **MEDIUM (Có thể làm sau)**
9. Reorganize `exception/` package theo loại (business, security)
10. Reorganize `util/` package theo category
11. Reorganize `repository/` package theo domain (optional)
12. Chuẩn bị structure cho future growth
13. (Optional) Tạo `common/strategy/` base classes cho generic Strategy Pattern

### 🟢 **LOW (Tùy chọn)**
14. Thêm các validator classes mới nếu cần
15. Optimize `config/` package structure

---

## 18. STRATEGY PATTERN - IMPLEMENTATION BEST PRACTICES

### 18.1 Cấu Trúc Chuẩn cho Strategy + Factory Pattern

**Tệp lớp Interface (Base Strategy):**
```java
// promotion/strategy/action/PromotionActionStrategy.java
package com.example.clothingstore.promotion.strategy.action;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.model.Promotion;

public interface PromotionActionStrategy {
    PromotionActionTypeEnum getType();
    void execute(OrderPreviewDTO orderPreviewDTO, Promotion promotionContext, Integer actionIndex);
}
```

**File Factory:**
```java
// promotion/strategy/action/PromotionActionFactory.java
package com.example.clothingstore.promotion.strategy.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.example.clothingstore.enums.PromotionActionTypeEnum;

@Component
public class PromotionActionFactory {
    private final Map<PromotionActionTypeEnum, PromotionActionStrategy> strategyMap;

    public PromotionActionFactory(List<PromotionActionStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                PromotionActionStrategy::getType,
                strategy -> strategy
            ));
    }

    public PromotionActionStrategy getStrategy(PromotionActionTypeEnum type) {
        return strategyMap.get(type);
    }
}
```

**File Implementation:**
```java
// promotion/strategy/action/impl/FreeProductStrategy.java
package com.example.clothingstore.promotion.strategy.action.impl;

import org.springframework.stereotype.Component;
import com.example.clothingstore.promotion.strategy.action.PromotionActionStrategy;
import com.example.clothingstore.enums.PromotionActionTypeEnum;

@Component
public class FreeProductStrategy implements PromotionActionStrategy {
    
    @Override
    public PromotionActionTypeEnum getType() {
        return PromotionActionTypeEnum.FREE_PRODUCT;
    }
    
    @Override
    public void execute(OrderPreviewDTO orderPreviewDTO, Promotion promotionContext, Integer actionIndex) {
        // Implementation here
    }
}
```

### 18.2 Quy Tắc Tổ Chức File

| Loại File | Vị Trí | Mục Đích |
|-----------|--------|---------|
| Interface | `promotion/strategy/*/` | Định nghĩa contract |
| Factory | `promotion/strategy/*/` | Tạo instance strategy |
| Implementations | `promotion/strategy/*/impl/` | Các triển khai cụ thể |
| Engine/Orchestrator | `promotion/engine/` | Gọi factory & strategies |

### 18.3 Naming Convention cho Strategy Pattern

| Loại | Naming | Ví Dụ |
|------|--------|--------|
| Interface | `<Domain>Strategy` | `PromotionActionStrategy` |
| Factory | `<Domain>StrategyFactory` | `PromotionActionFactory` |
| Implementation | `<Specific><Domain>Strategy` | `FreeProductStrategy` |
| Enum (Types) | `<Domain>TypeEnum` | `PromotionActionTypeEnum` |

### 18.4 Generic Strategy Base Class (Optional Enhancement)

Nếu muốn tạo abstraction chung cho tất cả strategies:

```java
// common/pattern/strategy/StrategyProvider.java
package com.example.clothingstore.common.pattern.strategy;

public interface StrategyProvider<T extends Enum<T>, S> {
    S getStrategy(T type);
}

// common/pattern/strategy/BaseStrategyFactory.java
package com.example.clothingstore.common.pattern.strategy;

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseStrategyFactory<T extends Enum<T>, S> implements StrategyProvider<T, S> {
    protected final Map<T, S> strategies;

    protected BaseStrategyFactory(List<S> strategyList, Function<S, T> typeExtractor) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(typeExtractor, Function.identity()));
    }

    @Override
    public S getStrategy(T type) {
        S strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + type);
        }
        return strategy;
    }
}
```

Sau đó, Factory cụ thể có thể extend từ `BaseStrategyFactory`:
```java
public class PromotionActionFactory extends BaseStrategyFactory<PromotionActionTypeEnum, PromotionActionStrategy> {
    public PromotionActionFactory(List<PromotionActionStrategy> strategies) {
        super(strategies, PromotionActionStrategy::getType);
    }
}
```

---

## 19. LỢI ÍCH CỦA REFACTORING

| Khía Cạnh | Hiện Tại | Sau Refactoring |
|-----------|---------|-----------------|
| **Số lượng DTO packages** | 21 | ~15 |
| **Strategy/Factory organization** | Phân tán (3 folder) | Tập trung (1 promotion package) |
| **Dễ tìm kiếm** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Dễ mở rộng** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Dễ bảo trì** | ⭐⭐ | ⭐⭐⭐⭐ |
| **Code clarity** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Team collaboration** | ⭐⭐ | ⭐⭐⭐⭐ |
| **Service complexity** | ⭐ (OrderService 500+ lines) | ⭐⭐⭐⭐⭐ (Small, focused services) |
| **Validation clarity** | ⭐⭐ (PromotionValidator trong service/) | ⭐⭐⭐⭐⭐ (PromotionValidator trong promotion/) |

---

## 20. BEST PRACTICES ĐỀ XUẤT

### 20.1 Naming Convention
- ✓ **Packages:** lowercase, snake_case (nếu multi-word). VD: `shipping_address` hoặc `address`
- ✓ **Classes:** PascalCase. VD: `OrderService`, `OrderDTO`
- ✓ **Interfaces:** Đừng dùng tiền tố "I". VD: `AuthenticationService` (không `IAuthenticationService`)
- ✓ **Exceptions:** Đặt tên kết thúc với "Exception". VD: `NotFoundException`
- ✓ **Strategy Pattern:** `<Name>Strategy` (Interface), `<Specific><Name>Strategy` (Implementation)

### 20.2 Package Organization
- ✓ **Domain-Driven:** Organize theo business domain (order, product, cart, promotion)
- ✓ **Single Responsibility:** Mỗi package/class chỉ có 1 trách nhiệm
- ✓ **Cohesion:** Các class liên quan nên ở gần nhau (VD: tất cả promotion logic trong promotion/ package)
- ✓ **Coupling:** Giảm dependency giữa các package

### 20.3 Service Layer
- ✓ **Orchestrator Pattern:** Service lớn nên tách thành:
  - Orchestrator (gọi các service nhỏ)
  - Specialized services (từng trách nhiệm)
- ✓ **Validator Pattern:** Business logic validation nên ở trong domain package, không ở `service/` root
- ✓ **Factory Pattern:** Cho creation logic phức tạp, nên ở trong domain/strategy package

### 20.4 Strategy + Factory Pattern
- ✓ **File Layout:** Interface → Factory → `impl/` folder cho implementations
- ✓ **Auto-discovery:** Dùng `@Component` cho implementations, Factory sẽ inject tất cả
- ✓ **Type-safe:** Sử dụng Enum cho strategy type, tránh magic strings
- ✓ **Scalability:** Thêm strategy mới chỉ cần tạo implementation class, không modify factory

---

## 21. CÁC TÀI LIỆU THAM KHẢO

- [Spring Boot Project Structure Best Practices](https://spring.io/guides)
- [Java Naming Conventions](https://oracle.com/java)
- [Strategy Pattern - Design Patterns](https://en.wikipedia.org/wiki/Strategy_pattern)
- [Factory Pattern - Design Patterns](https://en.wikipedia.org/wiki/Factory_method_pattern)
- [Clean Code by Robert Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
- [Building Microservices by Sam Newman](https://www.oreilly.com/library/view/building-microservices/9781491950340/)

---

## Kết Luận

Refactoring structure này sẽ mang lại:
- **Dễ bảo trì hơn** cho team trong tương lai (Promotion logic ở một chỗ)
- **Dễ mở rộng hơn** khi thêm feature mới (Thêm strategy mới dễ dàng)
- **Code clarity tốt hơn** cho onboarding team members mới
- **Reduced technical debt** trong long term
- **Better pattern recognition** (Tất cả Strategy+Factory ở một folder)

### Chiến Lược Thực Hiện

**Phase 1 (This Sprint):** CRITICAL tasks
- Sửa typo
- Rename interface
- Reorganize Promotion Strategy Architecture
- Reorganize DTO package

**Phase 2 (Next Sprint):** HIGH priority
- Tạo Promotion Engine
- Chia OrderService
- Reorganize Mapper/Security/Exception

**Phase 3 (Future):** MEDIUM priority
- Reorganize Util/Repository
- Tạo generic Strategy base classes

Hãy bắt đầu từ các task CRITICAL trước, sau đó tiến hành HIGH priority tasks.
