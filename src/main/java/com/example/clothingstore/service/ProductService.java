package com.example.clothingstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.product.ProductColorRequestDTO;
import com.example.clothingstore.dto.product.ProductColorResponseDTO;
import com.example.clothingstore.dto.product.ProductColorUpdateDTO;
import com.example.clothingstore.dto.product.ProductDetailRequestDTO;
import com.example.clothingstore.dto.product.ProductDetailResponseDTO;
import com.example.clothingstore.dto.product.ProductDetailUpdateDTO;
import com.example.clothingstore.dto.product.ProductRequestDTO;
import com.example.clothingstore.dto.product.ProductResponseDTO;
import com.example.clothingstore.dto.product.ProductSummaryDTO;
import com.example.clothingstore.dto.product.ProductUpdateDTO;
import com.example.clothingstore.enums.StatusEnum;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.ProductColorMapper;
import com.example.clothingstore.mapper.mapstruct.ProductDetailMapper;
import com.example.clothingstore.mapper.mapstruct.ProductMapper;
import com.example.clothingstore.model.Category;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.ProductColor;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.repository.CategoryRepository;
import com.example.clothingstore.repository.ProductColorRepository;
import com.example.clothingstore.repository.ProductDetailRepository;
import com.example.clothingstore.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CategoryRepository categoryRepository;

    // Các mapper
    private final ProductMapper productMapper;
    private final ProductColorMapper productColorMapper;
    private final ProductDetailMapper productDetailMapper;

    private final FileUploadService fileUploadService;

    @Transactional
    public ProductResponseDTO getProductDetailById(Integer productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Invalid product code"));

        // return new ProductResponseDTO(product);
        return productMapper.toResponseDTO(product);
    }

    @Transactional
    // public List<ProductSummaryDTO> getAllProduct(Integer categoryId, Pageable
    // pageable) {
    public Page<ProductSummaryDTO> getAllProduct(Integer categoryId, Pageable pageable) {

        Page<Product> products = (categoryId == null) ? productRepository.findAll(pageable)
                : productRepository.findByCategories_CategoryId(categoryId, pageable);

        // List<ProductSummaryDTO> productSummaryDTOs = products.toList()
        // .stream()
        // .map((product) -> {
        // // return new ProductSummaryDTO(product);
        // return productMapper.toSummaryDTO(product);
        // })
        // .toList();

        return products.map(productMapper::toSummaryDTO);

        // return productSummaryDTOs;
    }

    @Transactional
    public ProductSummaryDTO createProduct(ProductRequestDTO productRequest) {
        Product product = new Product();

        requestToEntity(product, productRequest);
        // Gán danh mục cho sản phẩm
        List<Category> categories = categoryRepository.findAllById(productRequest.getCategoryId());

        categories.forEach((category) -> category.getProducts().add(product));

        productRepository.save(product);

        // return new ProductSummaryDTO(product);
        return productMapper.toSummaryDTO(product);
    }

    @Transactional
    public ProductSummaryDTO deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Invalid product code"));

        // List<Category> categories =
        // categoryRepository.findByProducts_ProductId(productId);

        // categories.forEach((category) -> {
        // category.getProducts().removeIf(p -> p.getProductId().equals(productId));
        // });

        // productRepository.deleteById(productId);
        product.setStatus(com.example.clothingstore.enums.StatusEnum.INACTIVE);

        productRepository.save(product);

        // return new ProductSummaryDTO(product);
        return productMapper.toSummaryDTO(product);
    }

    // @Transactional
    // public ProductSummaryDTO updateProduct(Integer productId, ProductRequestDTO
    // productRequest) {
    // // Product product = requestToEntity(productRequest);
    // Product product = productRepository.findById(productId)
    // .orElseThrow(() -> new NotFoundException("Invalid product code"));

    // requestToEntity(product, productRequest);
    // // Lấy các category mới từ request
    // List<Category> newCategories =
    // categoryRepository.findAllById(productRequest.getCategoryId());

    // // Clear các category cũ chỉ quan hệ không xóa category
    // product.getCategories().forEach(c -> c.getProducts().remove(product));
    // product.getCategories().clear();

    // // Gán category mới
    // product.setCategories(new ArrayList<>(newCategories));
    // newCategories.forEach(c -> c.getProducts().add(product)); // đồng bộ hai
    // chiều

    // productRepository.save(product);

    // return new ProductSummaryDTO(product);
    // }

    private void requestToEntity(Product product, ProductRequestDTO productRequest) {

        product.setProductName(productRequest.getProductName());

        product.setUnitPrice(productRequest.getUnitPrice());

        product.setDiscount(productRequest.getDiscount());

        product.setDescription(productRequest.getDescription());

        if (productRequest.getProductImage() != null) {
            product.setProductImage(productRequest.getProductImage());
            fileUploadService.deleteFile(productRequest.getProductImage());
        }

        product.setStatus(StatusEnum.ACTIVE);

        // product.setCategories(categories);

        // List<ProductColor> productColors = new ArrayList<>();
        for (ProductColorRequestDTO productColorRequest : productRequest.getProductColors()) {
            ProductColor productColor = new ProductColor();
            productColor.setColor(productColorRequest.getColor());
            // productColor.setProductImage(productColorRequest.getProductImage());
            if (productColorRequest.getProductImage() != null) {
                productColor.setProductImage(productColorRequest.getProductImage());
                fileUploadService.deleteFile(productColorRequest.getProductImage());
            }

            List<ProductDetail> productDetails = new ArrayList<>();
            for (ProductDetailRequestDTO productDetailRequest : productColorRequest.getProductDetails()) {
                ProductDetail productDetail = new ProductDetail();

                productDetail.setSize(productDetailRequest.getSize());
                productDetail.setQuantity(productDetailRequest.getQuantity());

                productDetail.setStatus(StatusEnum.ACTIVE);

                // Thiết lập cha cho nó
                productDetail.setProductColor(productColor);

                productDetails.add(productDetail);
            }

            productColor.setProductDetails(productDetails);
            productColor.setStatus(StatusEnum.ACTIVE);

            // Thiết lập cha cho nó
            productColor.setProduct(product);
            // productColors.add(productColor);
            product.getProductColors().add(productColor);
        }

    }

    @Transactional
    public ProductColorResponseDTO updateProductColor(Integer productColorId,
            ProductColorRequestDTO productColorRequest) {

        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new NotFoundException("Invalid product color id"));

        // productColor.setColor(productColorRequest.getColor());
        // productColor.setProductImage(productColorRequest.getProductImage());
        productColorMapper.updateEntityFromDTO(productColorRequest, productColor);

        productColorRepository.save(productColor);

        // ProductColorResponseDTO productColorResponseDTO = new
        // ProductColorResponseDTO(productColor);
        ProductColorResponseDTO productColorResponseDTO = productColorMapper.toResponseDTO(productColor);
        return productColorResponseDTO;
    }

    @Transactional
    public ProductColorResponseDTO deleteProductColor(Integer productColorId) {

        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new NotFoundException("Invalid product color id"));

        productColor.setStatus(com.example.clothingstore.enums.StatusEnum.INACTIVE);

        productColorRepository.save(productColor);

        // ProductColorResponseDTO productColorResponseDTO = new
        // ProductColorResponseDTO(productColor);
        ProductColorResponseDTO productColorResponseDTO = productColorMapper.toResponseDTO(productColor);
        return productColorResponseDTO;
    }

    @Transactional
    public ProductColorResponseDTO createProductColor(ProductColorRequestDTO productColorRequest) {
        ProductColor productColor = new ProductColor();

        productColor.setColor(productColorRequest.getColor());
        productColor.setProductImage(productColorRequest.getProductImage());

        ProductColor savedProductColor = productColorRepository.save(productColor);

        // ProductColorResponseDTO productColorResponseDTO = new
        // ProductColorResponseDTO(savedProductColor);
        ProductColorResponseDTO productColorResponseDTO = productColorMapper.toResponseDTO(savedProductColor);
        return productColorResponseDTO;
    }

    @Transactional
    public ProductDetailResponseDTO createProductDetail(Integer productColorId,
            ProductDetailRequestDTO productDetailRequest) {
        ProductDetail productDetail = new ProductDetail();

        productDetail.setSize(productDetailRequest.getSize());
        productDetail.setQuantity(productDetailRequest.getQuantity());

        ProductColor productColor = productColorRepository.findById(productColorId)
                .orElseThrow(() -> new NotFoundException("Invalid product color id"));

        productDetail.setProductColor(productColor);

        ProductDetail savedProductDetail = productDetailRepository.save(productDetail);

        // ProductDetailResponseDTO productDetailResponseDTO =
        // productDetailMapper.toResponseDTO(savedProductDetail);
        ProductDetailResponseDTO productDetailResponseDTO = new ProductDetailResponseDTO(savedProductDetail);
        return productDetailResponseDTO;
    }

    @Transactional
    public ProductDetailResponseDTO updateProductDetail(Integer productColorId, Integer productDetailId,
            ProductDetailRequestDTO productDetailRequest) {

        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new NotFoundException("Invalid product detail id"));

        // productDetail.setSize(productDetailRequest.getSize());
        // productDetail.setQuantity(productDetailRequest.getQuantity());
        productDetailMapper.updateEntityFromDTO(productDetailRequest, productDetail);

        productDetailRepository.save(productDetail);

        // ProductDetailResponseDTO productDetailResponseDTO = new
        // ProductDetailResponseDTO(productDetail);
        ProductDetailResponseDTO productDetailResponseDTO = productDetailMapper.toResponseDTO(productDetail);
        return productDetailResponseDTO;
    }

    @Transactional
    public ProductDetailResponseDTO deleteProductDetail(Integer productColorId, Integer productDetailId) {

        ProductDetail productDetail = productDetailRepository.findById(productDetailId)
                .orElseThrow(() -> new NotFoundException("Invalid product detail id"));

        productDetail.setStatus(com.example.clothingstore.enums.StatusEnum.INACTIVE);

        productDetailRepository.save(productDetail);

        // ProductDetailResponseDTO productDetailResponseDTO = new
        // ProductDetailResponseDTO(productDetail);
        ProductDetailResponseDTO productDetailResponseDTO = productDetailMapper.toResponseDTO(productDetail);
        return productDetailResponseDTO;
    }

    // Còn mấy cái không có thì tức là đã xóa rồi, để nguyên trạng thôi
    @Transactional
    public ProductSummaryDTO updateProduct(Integer productId, ProductUpdateDTO productUpdate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Invalid product code"));
        // Lưu các url image để xóa sau khi cập nhật thành công
        List<String> oldImageUrls = new ArrayList<>();
        if (product.getProductImage() != null && productUpdate.getProductImage() != null
                && !product.getProductImage().equals(productUpdate.getProductImage())) {
            oldImageUrls.add(product.getProductImage());
        }
        List<String> colorImageUrls = product.getProductColors().stream()
                .filter(pc -> pc.getProductImage() != null)
                .map(pc -> pc.getProductImage())
                .collect(Collectors.toList());

        product.getProductColors().forEach(pc -> {
            if (pc.getProductImage() != null && !colorImageUrls.contains(pc.getProductImage())) { 
                oldImageUrls.add(pc.getProductImage());
            }
        });
        // Cập nhật các trường cơ bản
        product.setProductName(productUpdate.getProductName());
        product.setUnitPrice(productUpdate.getUnitPrice());
        product.setDescription(productUpdate.getDescription());
        product.setProductImage(productUpdate.getProductImage());
        product.setDiscount(productUpdate.getDiscount());
        // Cập nhật danh mục
        List<Category> newCategories = categoryRepository.findAllById(productUpdate.getCategoryId());
        product.getCategories().forEach(c -> c.getProducts().remove(product));
        product.getCategories().clear();
        product.setCategories(new ArrayList<>(newCategories));
        newCategories.forEach(c -> c.getProducts().add(product)); // đồng bộ hai chiều
        // Xử lý product colors
        Map<Integer, ProductColor> existingProductColorsMap = product.getProductColors().stream()
                .collect(Collectors.toMap(ProductColor::getColorId, pc -> pc));

        // Map<Integer, Boolean> colorIdsInUpdate = new HashMap<>();

        List<Integer> colorIdsUpIntegers = productUpdate.getProductColors().stream()
                .filter(pc -> pc.getColorId() != null)
                .map(pc -> pc.getColorId())
                .collect(Collectors.toList());

        // product.getProductColors().removeIf(pc ->
        // !colorIdsUpIntegers.contains(pc.getColorId()));

        // Chuyển status thành INACTIVE cho các product color không có trong update
        product.getProductColors().forEach(pc -> {
            if (!colorIdsUpIntegers.contains(pc.getColorId())) {
                pc.setStatus(com.example.clothingstore.enums.StatusEnum.INACTIVE);
            }
        });

        for (ProductColorUpdateDTO pcUpdate : productUpdate.getProductColors()) {

            if (pcUpdate.getColorId() != null && existingProductColorsMap.containsKey(pcUpdate.getColorId())) {
                // Cập nhật product color hiện có
                ProductColor existingPC = existingProductColorsMap.get(pcUpdate.getColorId());
                existingPC.setColor(pcUpdate.getColor());
                existingPC.setProductImage(pcUpdate.getProductImage());
                // colorIdsInUpdate.put(pcUpdate.getColorId(), true);
                // Xử lý product details
                Map<Integer, ProductDetail> existingProductDetailsMap = existingPC.getProductDetails().stream()
                        .collect(Collectors.toMap(ProductDetail::getDetailId, pd -> pd));

                // Map<Integer, Boolean> detailIdsInUpdate = new HashMap<>();
                List<Integer> detailIdsUpIntegers = pcUpdate.getProductDetails().stream()
                        .filter(pd -> pd.getDetailId() != null)
                        .map(pd -> pd.getDetailId())
                        .collect(Collectors.toList());

                // Xóa các product detail không có trong update
                // existingPC.getProductDetails().removeIf(pd ->
                // !detailIdsUpIntegers.contains(pd.getDetailId()));

                // Chuyển thành INACTIVE cho các product detail không có trong update
                existingPC.getProductDetails().forEach(pd -> {
                    if (!detailIdsUpIntegers.contains(pd.getDetailId())) {
                        pd.setStatus(com.example.clothingstore.enums.StatusEnum.INACTIVE);
                    }
                });

                for (ProductDetailUpdateDTO pdUpdate : pcUpdate.getProductDetails()) {
                    if (pdUpdate.getDetailId() != null
                            && existingProductDetailsMap.containsKey(pdUpdate.getDetailId())) {
                        // Cập nhật product detail hiện có
                        ProductDetail existingPD = existingProductDetailsMap.get(pdUpdate.getDetailId());
                        existingPD.setSize(pdUpdate.getSize());
                        existingPD.setQuantity(pdUpdate.getQuantity());
                        // detailIdsInUpdate.put(pdUpdate.getDetailId(), true);
                    } else {
                        // Thêm product detail mới
                        ProductDetail newPD = new ProductDetail();
                        newPD.setSize(pdUpdate.getSize());
                        newPD.setQuantity(pdUpdate.getQuantity());
                        newPD.setProductColor(existingPC);
                        existingPC.getProductDetails().add(newPD);
                    }
                }

            } else {
                // Thêm product color mới
                ProductColor newPC = new ProductColor();
                newPC.setColor(pcUpdate.getColor());
                newPC.setProductImage(pcUpdate.getProductImage());
                newPC.setProduct(product);
                // Thêm các product detail mới
                for (ProductDetailUpdateDTO pdUpdate : pcUpdate.getProductDetails()) {
                    ProductDetail newPD = new ProductDetail();
                    newPD.setSize(pdUpdate.getSize());
                    newPD.setQuantity(pdUpdate.getQuantity());
                    newPD.setProductColor(newPC);
                    newPC.getProductDetails().add(newPD);
                }
                product.getProductColors().add(newPC);
            }
        }
        // Xóa các product color không có trong update
        // product.getProductColors().removeIf(pc ->
        // !colorIdsInUpdate.containsKey(pc.getColorId()));

        // Chuyển thành INACTIVE cho các product detail không có trong update

        productRepository.save(product);
        // return new ProductSummaryDTO(product);
        for (String url : oldImageUrls) {
            fileUploadService.deleteFileFromCloudinary(url);
        }
        return productMapper.toSummaryDTO(product);

    }

}
