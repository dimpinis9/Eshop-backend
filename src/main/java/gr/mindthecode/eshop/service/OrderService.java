package gr.mindthecode.eshop.service;

import gr.mindthecode.eshop.dto.ProductQuantity;
import gr.mindthecode.eshop.model.Orders;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    List<Orders> findAll();
    Page<Orders> findByStatus(int page, int size, String sort);
    Page<Orders> getOrders(String address,
                           String status,
                           int page,
                           int size,
                           String sort);
    List<Orders> getMyOrders(String status);
    List<ProductQuantity> getProductsFromOrder(Integer id);
    Orders validateOrder(Integer id);
}
