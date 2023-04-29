package gr.mindthecode.eshop.repository;

import gr.mindthecode.eshop.model.Orders;
import gr.mindthecode.eshop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders,Integer> {
    Page<Orders> findByAddressContaining(String address, Pageable pageable);
    Page<Orders> findByAddressContainingAndStatus(String address, String status,Pageable pageable);
    Optional<Orders> findByStatusAndUsers(String status, User user);
    List<Orders> findOrdersByStatusAndUsers(String status, User user);
    Page<Orders> findByStatus(String status,Pageable pageable);
    List<Orders> findAll();
    Orders findOrdersByStatusAndOrdersId(String status, Integer id);
}
