package com.example.clothingstore.mapper.mapstruct;

import java.math.BigDecimal;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.refund.RefundResponseDTO;
import com.example.clothingstore.dto.refund.RefundSummaryDTO;
import com.example.clothingstore.model.RefundRequest;

@Mapper(componentModel = "spring", uses = {
                RefundItemMapper.class
})
public interface RefundMapper {

        @Mapping(target = "orderId", source = "refundRequest.order.orderId")
        RefundSummaryDTO toSummaryDTO(RefundRequest refundRequest);

        @AfterMapping
        default void setTotalRefundAmount(@MappingTarget RefundSummaryDTO refundSummaryDTO,
                        RefundRequest refundRequest) {
                BigDecimal totalRefundAmount = refundRequest.getRefundItems().stream()
                                .map(refundItem -> refundItem.getRefundAmount()
                                                .multiply(BigDecimal.valueOf(refundItem.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                refundSummaryDTO.setTotalRefundAmount(totalRefundAmount);
        }

        @Mapping(target = "orderId", source = "refundRequest.order.orderId")
        @Mapping(target = "gatewayRefundId", source = "refundRequest.refundPayment.gatewayRefundId")
        @Mapping(target = "imageRefund", source = "refundRequest.refundPayment.imageRefund")
        @Mapping(target = "note", source = "refundRequest.refundPayment.note")
        RefundResponseDTO toResponseDTO(RefundRequest refundRequest);

        @AfterMapping
        default void setTotalRefundAmount(@MappingTarget RefundResponseDTO refundResponseDTO,
                        RefundRequest refundRequest) {
                BigDecimal totalRefundAmount = refundRequest.getRefundItems().stream()
                                .map(refundItem -> refundItem.getRefundAmount()
                                                .multiply(BigDecimal.valueOf(refundItem.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                refundResponseDTO.setTotalRefundAmount(totalRefundAmount);
        }

}
