package gr.mindthecode.eshop.controller;

import gr.mindthecode.eshop.dto.ProductQuantity;
import gr.mindthecode.eshop.model.Orders;
import gr.mindthecode.eshop.repository.OrdersRepository;
import gr.mindthecode.eshop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eshop")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public Page<Orders> getOrdersByStatus(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "ASC", required = false) String sort
    ){
        return orderService.getOrders(address,status,page, size, sort);
    }

    @GetMapping("/orders/user")
    public List<Orders> getMyOrders(@RequestParam(required = false) String status){
        return orderService.getMyOrders(status);
    }

    @GetMapping("/orders/products/{id}")
    public List<ProductQuantity> getProductOrders(@PathVariable Integer id){
        return orderService.getProductsFromOrder(id);
    }

    @PostMapping("/orders/validate")
    public Orders validateOrder(@RequestParam Integer ordersId){
        return orderService.validateOrder(ordersId);
    }

}
