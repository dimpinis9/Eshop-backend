package gr.mindthecode.eshop.repository;

import gr.mindthecode.eshop.model.Orders;
import gr.mindthecode.eshop.model.ShoppingCart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findAll();
    List<ShoppingCart> findByOrder(Orders order);
    @Transactional
    @Modifying
    @Query(value ="update shopping_cart set quantity=?1 where orders_id=?2" ,nativeQuery = true)
    void updateQuantity(Integer quantity,Integer ordersId);
}
