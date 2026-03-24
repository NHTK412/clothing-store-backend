package com.example.clothingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.review.ReviewRequestDTO;
import com.example.clothingstore.dto.review.ReviewResponseDTO;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.ReviewMapper;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.Review;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.OrderDetailRepository;
import com.example.clothingstore.repository.ProductRepository;
import com.example.clothingstore.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    // @Autowired
    // private ReviewRepository reviewRepository;

    // @Autowired
    // private ProductRepository productRepository;

    // @Autowired
    // private CustomerRepository customerRepository;

    // @Autowired
    // private OrderDetailRepository orderDetailRepository;

    // @Autowired
    // private ReviewMapper reviewMapper;

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewMapper reviewMapper;

    private final com.example.clothingstore.mapper.mapstruct.ReviewMapper reviewMapper2;

    @Transactional
    public Page<ReviewResponseDTO> getALLReviewByProductId(Integer productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProduct_ProductId(productId, pageable);

        // return reviews
        // .map((review) -> reviewMapper.convertModelToReviewResponseDTO(review))
        // .toList();

        return reviews.map(reviewMapper::convertModelToReviewResponseDTO);

        // return reviews.map(reviewMapper2::toResponseDTO);

    }

    @Transactional
    public ReviewResponseDTO createReviewByProductId(
            Integer productId,
            Integer customerId,
            // Integer productId,
            ReviewRequestDTO reviewRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Invalid product code"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid customer code"));

        OrderDetail orderDetail = orderDetailRepository.findById(reviewRequestDTO.getOrderdetailId())
                .orElseThrow(() -> new NotFoundException("Invalid order detail code"));

        if (orderDetail.getIsReview() == true) {
            throw new ConflictException("This order detail has already been reviewed");
        }

        Review review = new Review();

        review.setProduct(product);

        review.setCustomer(customer);

        review = reviewMapper.convertReviewRequestDTOtoModel(reviewRequestDTO, review);

        reviewRepository.save(review);

        // Cập nhật isReview trong OrderDetail
        orderDetail.setIsReview(true);
        orderDetailRepository.save(orderDetail);

        for (int i = 0; i < orderDetail.getOrder().getOrderDetails().size(); i++) {
            if (orderDetail.getIsReview() == false) {
                // return reviewMapper.convertModelToReviewResponseDTO(review);
                break;
            }
            if (i == orderDetail.getOrder().getOrderDetails().size() - 1) {
                orderDetail.getOrder().setIsReview(true);
            }
        }

        return reviewMapper.convertModelToReviewResponseDTO(review);

    }

    @Transactional
    public ReviewResponseDTO updateReview(Integer productId, Integer reviewId, ReviewRequestDTO reviewRequestDTO) {

        Review review = reviewRepository.findByReviewIdAndProduct_ProductId(reviewId,
                productId)
                .orElseThrow(() -> new NotFoundException("Invalid review code"));

        // Review review = reviewRepository.findById(reviewId)
        // .orElseThrow(() -> new NotFoundException("Invalid review code"));

        review = reviewMapper.convertReviewRequestDTOtoModel(reviewRequestDTO, review);

        reviewRepository.save(review);

        return reviewMapper.convertModelToReviewResponseDTO(review);

    }

    @Transactional
    public ReviewResponseDTO deleteReview(Integer productId, Integer reviewId) {

        Review review = reviewRepository.findByReviewIdAndProduct_ProductId(reviewId, productId)
                .orElseThrow(() -> new NotFoundException("Invalid review code"));

        ReviewResponseDTO reviewResponseDTO = reviewMapper.convertModelToReviewResponseDTO(review);

        reviewRepository.delete(review);

        return reviewResponseDTO;
    }

    // New

}
