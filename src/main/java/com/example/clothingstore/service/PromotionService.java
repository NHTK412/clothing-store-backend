package com.example.clothingstore.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.promotion.PromotionActionResponseDTO;
import com.example.clothingstore.dto.promotion.PromotionConditionResponseDTO;
import com.example.clothingstore.dto.promotion.PromotionCreateRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionGroupRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionGroupResponseDTO;
import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
// import com.example.clothingstore.action.PromotionActionFactory;
// import com.example.clothingstore.action.PromotionActionStrategy;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;
import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.exception.business.BadRequestException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.PromotionGroupMapper;
import com.example.clothingstore.model.Admin;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.MembershipTier;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.model.PromotionAction;
import com.example.clothingstore.model.PromotionCondition;
import com.example.clothingstore.model.PromotionGroup;
import com.example.clothingstore.model.PromotionMemberTier;
import com.example.clothingstore.model.PromotionTargetUser;
import com.example.clothingstore.model.User;
import com.example.clothingstore.model.VoucherWallet;
import com.example.clothingstore.repository.AdminRepository;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.MembershipTierRepository;
import com.example.clothingstore.repository.ProductRepository;
import com.example.clothingstore.repository.PromotionGroupRepository;
import com.example.clothingstore.repository.PromotionMemberTierRepository;
import com.example.clothingstore.repository.PromotionRepository;
import com.example.clothingstore.repository.PromotionTargetUserRepository;
import com.example.clothingstore.repository.UserRepository;
import com.example.clothingstore.repository.VoucherWalletRepository;
import com.example.clothingstore.strategy.scope.PromotionScopeStrategy;
import com.example.clothingstore.strategy.scope.PromotionScopeFactory;
import com.example.clothingstore.validator.PromotionValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {

    final private PromotionRepository promotionRepository;

    final private ProductRepository productRepository;

    final private PromotionGroupRepository promotionGroupRepository;

    final private PromotionValidator promotionValidator;

    final private PromotionGroupMapper promotionGroupMapper;

    final private PromotionScopeFactory promotionScopeFactory;

    final private CustomerRepository customerRepository;

    final private VoucherWalletRepository voucherWalletRepository;

    final private PromotionTargetUserRepository promotionTargetUserRepository;

    final private PromotionMemberTierRepository promotionMemberTierRepository;

    final private MembershipTierRepository membershipTierRepository;

    final private AdminRepository adminRepository;

    // final private PromotionActionFactory promotionActionFactory;

    @Scheduled(fixedDelay = 1000 * 60) // Chạy sau mỗi 1 giây kể từ khi kết thúc lần chạy trước
    // Kiểm tra hết hạn khuyến mãi và cập nhật trạng thái
    @Transactional
    public void checkAndExpirePromotions() {

        LocalDateTime now = LocalDateTime.now();

        List<Promotion> activePromotions = promotionRepository.findByIsActive(true);

        for (Promotion promotion : activePromotions) {

            if (promotion.getEndDate().isBefore(now)) {
                promotion.setIsActive(false);
                promotionRepository.save(promotion);

                if (promotion.getPromotionType() == PromotionTypeEnum.AUTOMATIC) {

                    List<PromotionCondition> promotionConditions = promotion.getPromotionConditions();

                    boolean checkCondtion = promotionConditions.stream()
                            .allMatch((promotionCondition) -> promotionCondition
                                    .getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

                    if (checkCondtion) {
                        List<PromotionAction> promotionActions = promotion.getPromotionActions();

                        for (PromotionAction promotionAction : promotionActions) {
                            if (promotionAction.getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT
                                    || promotionAction
                                            .getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT) {
                                Map<String, Object> actionParams = promotionAction.getValue();

                                Integer promtionGroupId = (Integer) actionParams.get("promtionGroupId");

                                Optional<PromotionGroup> promotionGroup = promotionGroupRepository
                                        .findById(promtionGroupId);

                                if (!promotionGroup.isPresent()) {
                                    break;
                                }

                                List<Product> products = promotionGroup.get().getProducts();

                                for (Product product : products) {
                                    product.setDiscount(0.0);
                                }

                                productRepository.saveAll(products);

                            }
                        }
                    }
                }

            }

        }
    }

    // }

    @Scheduled(fixedDelay = 1000 * 60) // Chạy sau mỗi 1 giây kể từ khi kết thúc lần chạy trước
    @Transactional
    public void checkAndActivatePromotions() {

        LocalDateTime now = LocalDateTime.now();

        List<Promotion> inactivePromotions = promotionRepository.findByIsActive(false);

        for (Promotion promotion : inactivePromotions) {
            if (promotion.getStartDate().isBefore(now) && promotion.getEndDate().isAfter(now)) {
                promotion.setIsActive(true);
                promotionRepository.save(promotion);

                // Nếu khuyến mãi là giảm giá, thì gán lại giá trị discount cho sản phẩm
                PromotionTypeEnum promotionType = promotion.getPromotionType();

                if (promotionType == PromotionTypeEnum.AUTOMATIC) {
                    List<PromotionCondition> promotionConditions = promotion.getPromotionConditions();

                    boolean checkCondtion = promotionConditions.stream()
                            .allMatch((promotionCondition) -> promotionCondition
                                    .getConditionType() == PromotionConditionTypeEnum.PRODUCT_SPECIFIC);

                    if (checkCondtion) {
                        List<PromotionAction> promotionActions = promotion.getPromotionActions();

                        for (PromotionAction promotionAction : promotionActions) {
                            if (promotionAction.getActionType() == PromotionActionTypeEnum.PRODUCT_FIXED_DISCOUNT) {
                                Map<String, Object> actionParams = promotionAction.getValue();

                                Integer promtionGroupId = (Integer) actionParams.get("promtionGroupId");

                                // Number discount = ;
                                Double fixedDiscount = (Double) ((Number) actionParams.get("fixedDiscount"))
                                        .doubleValue();

                                // này break khỏi vòng lặp ok hơn
                                // PromotionGroup promotionGroup =
                                // promotionGroupRepository.findById(promtionGroupId)
                                // .orElseThrow(() -> new NotFoundException("Invalid Promotion Group Code"));
                                Optional<PromotionGroup> promotionGroup = promotionGroupRepository
                                        .findById(promtionGroupId);

                                if (!promotionGroup.isPresent()) {
                                    break;
                                }

                                List<Product> products = promotionGroup.get().getProducts();

                                for (Product product : products) {
                                    product.setDiscount(fixedDiscount);
                                }

                                productRepository.saveAll(products);

                            } else if (promotionAction
                                    .getActionType() == PromotionActionTypeEnum.PRODUCT_PERCENT_DISCOUNT) {
                                Map<String, Object> actionParams = promotionAction.getValue();

                                Number discount = (Number) actionParams.get("discountPercentage");
                                Double discountPercentage = (Double) discount.doubleValue();

                                Integer promtionGroupId = (Integer) actionParams.get("promtionGroupId");

                                // // này break khỏi vòng lặp ok hơn
                                // PromotionGroup promotionGroup =
                                // promotionGroupRepository.findById(promtionGroupId)
                                // .orElseThrow(() -> new NotFoundException("Invalid Promotion Group Code"));

                                Optional<PromotionGroup> promotionGroup = promotionGroupRepository
                                        .findById(promtionGroupId);

                                if (!promotionGroup.isPresent()) {
                                    break;
                                }

                                List<Product> products = promotionGroup.get().getProducts();

                                // List<Product> products = promotionGroup.getProducts();

                                for (Product product : products) {
                                    // product.setDiscount(fixedDiscount);
                                    Double discountValue = product.getUnitPrice() * discountPercentage / 100;

                                    product.setDiscount(discountValue);
                                }

                                productRepository.saveAll(products);

                            }
                        }

                    }
                }

                if (promotionType == PromotionTypeEnum.CONDITIONAL) {

                    PromotionScopeTypeEnum promotionScopeType = promotion.getPromotionScopeType();

                    PromotionScopeStrategy promotionScopeStrategy = promotionScopeFactory
                            .getPromotionScopeStrategy(promotionScopeType);

                    if (promotionScopeType == PromotionScopeTypeEnum.ALL_USER) {
                        List<Customer> allCustomers = customerRepository.findAll();

                        for (Customer customer : allCustomers) {

                            VoucherWallet voucherWallet = new VoucherWallet();

                            voucherWallet.setCustomer(customer);
                            voucherWallet.setPromotion(promotion);
                            voucherWallet.setUsedCount(0);

                            voucherWalletRepository.save(voucherWallet);

                        }
                    } else if (promotionScopeType == PromotionScopeTypeEnum.SPECIFIC_USER) {
                        List<Customer> targetCustomers = promotion.getPromotionTargetUsers().stream()
                                .map(promotionTargetUser -> promotionTargetUser.getCustomer())
                                .collect(Collectors.toList());

                        for (Customer customer : targetCustomers) {

                            VoucherWallet voucherWallet = new VoucherWallet();

                            voucherWallet.setCustomer(customer);
                            voucherWallet.setPromotion(promotion);
                            voucherWallet.setUsedCount(0);

                            voucherWalletRepository.save(voucherWallet);

                        }
                    } else if (promotionScopeType == PromotionScopeTypeEnum.MEMBER_RANK) {

                        List<MembershipTier> targetMembershipTiers = promotion.getPromotionMemberTiers().stream()
                                .map(promotionMemberTier -> promotionMemberTier.getMembershipTier())
                                .collect(Collectors.toList());

                        List<Customer> eligibleCustomers = customerRepository
                                .findByMembershipTierIn(targetMembershipTiers);

                        for (Customer customer : eligibleCustomers) {

                            VoucherWallet voucherWallet = new VoucherWallet();

                            voucherWallet.setCustomer(customer);
                            voucherWallet.setPromotion(promotion);
                            voucherWallet.setUsedCount(0);

                            voucherWalletRepository.save(voucherWallet);

                        }

                    }

                }
            }
        }

    }

    @Transactional
    public PromotionResponseDTO createPromotion(PromotionCreateRequestDTO requestDTO, Integer adminId) {

        promotionValidator.validatePromotionRequest(requestDTO);

        Promotion promotion = new Promotion();
        promotion.setPromotionName(requestDTO.getPromotionName());
        promotion.setDescription(requestDTO.getDescription());
        promotion.setPromotionType(requestDTO.getPromotionType());
        promotion.setStartDate(requestDTO.getStartDate());
        promotion.setEndDate(requestDTO.getEndDate());
        // promotion.setPriority(requestDTO.getPriority());
        promotion.setStackable(requestDTO.getStackable());
        promotion.setPromotionScopeType(requestDTO.getPromotionScopeType());
        promotion.setIsActive(false);

        if (requestDTO.getPromotionType() == PromotionTypeEnum.AUTOMATIC) {
            promotion.setStackable(true); // Tự động kích hoạt nếu là khuyến mãi tự động
        }

        if (requestDTO.getPromotionType() == PromotionTypeEnum.COUPON_CODE) {
            if (requestDTO.getCouponCode() == null || requestDTO.getCouponCode().isBlank()) {
                throw new BadRequestException("Coupon code is required for COUPON_CODE type");
            }
            promotion.setCouponCode(requestDTO.getCouponCode());
            promotion.setUsageLimit(requestDTO.getUsageLimit());
        }

        // ================================================================
        if (requestDTO.getPromotionGroupIds() != null && !requestDTO.getPromotionGroupIds().isEmpty()) {
            List<PromotionGroup> promotionGroups = promotionGroupRepository
                    .findAllById(requestDTO.getPromotionGroupIds());

            for (PromotionGroup promotionGroup : promotionGroups) {
                promotionGroup.setPromotion(promotion);
            }

            promotionGroupRepository.saveAll(promotionGroups);

            promotion.setPromotionGroups(promotionGroups);

        }
        // ================================================================

        // if (requestDTO.getPromotionGroups() != null &&
        // !requestDTO.getPromotionGroups().isEmpty()) {
        // List<PromotionGroup> promotionGroups =
        // requestDTO.getPromotionGroups().stream()
        // .map(groupDTO -> createPromotionGroup(groupDTO, promotion))
        // .collect(Collectors.toList());
        // promotion.setPromotionGroups(promotionGroups);
        // }

        if (requestDTO.getConditions() != null && !requestDTO.getConditions().isEmpty()) {
            List<PromotionCondition> conditions = requestDTO.getConditions().stream()
                    .map(conditionDTO -> {
                        PromotionCondition condition = new PromotionCondition();
                        condition.setConditionType(conditionDTO.getConditionType());
                        condition.setOperator(conditionDTO.getOperator());
                        condition.setValue(conditionDTO.getValue());
                        condition.setPromotion(promotion);
                        return condition;
                    })
                    .collect(Collectors.toList());
            promotion.setPromotionConditions(conditions);
        }

        if (requestDTO.getActions() != null && !requestDTO.getActions().isEmpty()) {
            List<PromotionAction> actions = requestDTO.getActions().stream()
                    .map(actionDTO -> {
                        PromotionAction action = new PromotionAction();
                        action.setActionType(actionDTO.getActionType());
                        action.setValue(actionDTO.getValue());
                        action.setPromotion(promotion);
                        return action;
                    })
                    .collect(Collectors.toList());
            promotion.setPromotionActions(actions);
        }

        if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.SPECIFIC_USER) {
            List<Integer> targetUserIds = requestDTO.getTargetUserIds();

            List<Customer> targetUsers = customerRepository.findAllById(targetUserIds);

            if (targetUsers.size() != targetUserIds.size()) {
                throw new NotFoundException("Some target user IDs were not found");
            }

            List<PromotionTargetUser> promotionTargetUsers = targetUsers.stream()
                    .map((targetUser) -> {
                        PromotionTargetUser promotionTargetUser = new PromotionTargetUser();
                        promotionTargetUser.setCustomer(targetUser);
                        promotionTargetUser.setPromotion(promotion);
                        return promotionTargetUser;
                    }).toList();

            promotionTargetUserRepository.saveAll(promotionTargetUsers);
            promotion.setPromotionTargetUsers(promotionTargetUsers);

        }

        if (requestDTO.getPromotionScopeType() == PromotionScopeTypeEnum.MEMBER_RANK) {

            List<Integer> targetMembershipRankIds = requestDTO.getTargetMemberTierIds();

            List<MembershipTier> targetMembershipTiers = membershipTierRepository.findAllById(targetMembershipRankIds);

            if (targetMembershipTiers.size() != targetMembershipRankIds.size()) {
                throw new NotFoundException("Some target membership tier IDs were not found");
            }

            List<PromotionMemberTier> promotionMemberTiers = targetMembershipTiers.stream()
                    .map(
                            (targetMembershipTier) -> {
                                PromotionMemberTier promotionMemberTier = new PromotionMemberTier();

                                promotionMemberTier.setMembershipTier(targetMembershipTier);

                                promotionMemberTier.setPromotion(promotion);

                                return promotionMemberTier;

                            })
                    .toList();

            promotionMemberTierRepository.saveAll(promotionMemberTiers);
            promotion.setPromotionMemberTiers(promotionMemberTiers);

        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin not found"));

        promotion.setAdmin(admin);

        Promotion savedPromotion = promotionRepository.save(promotion);

        // logger.info("Promotion created successfully with ID: {}",
        // savedPromotion.getPromotionId());

        return mapToResponseDTO(savedPromotion);
    }

    private PromotionGroup createPromotionGroup(PromotionGroupRequestDTO groupDTO, Promotion promotion) {

        PromotionGroup group = new PromotionGroup();
        group.setGroupName(groupDTO.getGroupName());
        group.setDescription(groupDTO.getDescription());
        group.setPromotion(promotion);

        // Lấy danh sách sản phẩm từ database
        List<Product> products = productRepository.findAllById(groupDTO.getProductIds());

        if (products.isEmpty()) {
            throw new NotFoundException("No products found with provided IDs");
        }

        if (products.size() != groupDTO.getProductIds().size()) {
            // logger.warn("Some product IDs were not found");
            throw new NotFoundException("Some product IDs were not found");
        }

        group.setProducts(products);

        return group;
    }

    private PromotionResponseDTO mapToResponseDTO(Promotion promotion) {

        List<PromotionConditionResponseDTO> conditions = promotion.getPromotionConditions()
                .stream()
                .map(condition -> PromotionConditionResponseDTO.builder()
                        .promotionConditionId(condition.getPromotionConditionId())
                        .conditionType(condition.getConditionType())
                        .operator(condition.getOperator())
                        .value(condition.getValue())
                        .build())
                .collect(Collectors.toList());

        List<PromotionActionResponseDTO> actions = promotion.getPromotionActions()
                .stream()
                .map(action -> PromotionActionResponseDTO.builder()
                        .promotionActionId(action.getPromotionActionId())
                        .actionType(action.getActionType())
                        .value(action.getValue())
                        .build())
                .collect(Collectors.toList());

        List<PromotionGroupResponseDTO> groups = (promotion.getPromotionGroups() != null
                && !promotion.getPromotionGroups().isEmpty())
                        ? promotion.getPromotionGroups().stream()
                                .map(group -> {
                                    return promotionGroupMapper.toResponseDTO(group);
                                })
                                .toList()
                        : List.of();

        return PromotionResponseDTO.builder()
                .promotionId(promotion.getPromotionId())
                .promotionName(promotion.getPromotionName())
                .description(promotion.getDescription())
                .promotionType(promotion.getPromotionType())
                // .priority(promotion.getPriority())
                .isActive(promotion.getIsActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .stackable(promotion.getStackable())
                .couponCode(promotion.getCouponCode())
                .usageLimit(promotion.getUsageLimit())
                .promotionScopeType(promotion.getPromotionScopeType())
                .conditions(conditions)
                .actions(actions)
                .promotionGroups(groups)
                .build();
    }

}
