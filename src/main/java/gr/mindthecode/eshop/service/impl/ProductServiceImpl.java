package gr.mindthecode.eshop.service.impl;

import gr.mindthecode.eshop.model.Product;
import gr.mindthecode.eshop.repository.ProductRepository;
import gr.mindthecode.eshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createOrUpdateProduct(Integer id,Product product) throws Exception {
        Optional<Product> check;
        if (id != null) {
             check = productRepository.findProductByProductId(id);
            if (check.isPresent()) {
                check.get().setProductDescription(product.getProductDescription());
                check.get().setProductPrice(product.getProductPrice());
                check.get().setCategory(product.getCategory());
                check.get().setAvailable(product.getAvailable());
                return productRepository.save(check.get());
            }
            else{
                throw new Exception("no product found");
            }
        }
        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(Integer id) {
        Product match = productRepository.findById(id).orElseThrow();
        productRepository.delete(match);
        return new Product();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findByProductId(id);
    }

    @Override
    public Page<Product> getProducts(String description,String category,int page, int size, String sort) {
        PageRequest paging = PageRequest
                .of(page, size)
                .withSort(sort.equalsIgnoreCase("ASC") ?
                        Sort.by("productPrice").ascending() :
                        Sort.by("productPrice").descending());

        Page<Product> res = null;
        if (description == null && category == null) {
            res = productRepository.findAll(paging);
        }
        if (description != null && category == null) {
            res = productRepository.findByProductDescriptionContaining(description, paging);
        }
        if (category != null && description == null) {
            res = productRepository.findByCategoryContaining(category,paging);
        }
        if (category != null && description != null) {
            res = productRepository.findByCategoryContainingAndProductDescriptionContaining(category, description, paging);
        }

        return res;
    }
}
