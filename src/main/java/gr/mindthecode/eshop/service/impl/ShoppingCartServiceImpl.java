package gr.mindthecode.eshop.service.impl;

import gr.mindthecode.eshop.dto.NewOrderDto;
import gr.mindthecode.eshop.dto.ProductQuantity;
import gr.mindthecode.eshop.model.*;
import gr.mindthecode.eshop.repository.OrdersRepository;
import gr.mindthecode.eshop.repository.ProductRepository;
import gr.mindthecode.eshop.repository.ShoppingCartRepository;
import gr.mindthecode.eshop.repository.UserRepository;
import gr.mindthecode.eshop.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private ProductRepository productRepository;
    private OrdersRepository ordersRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private UserRepository userRepository;


    public ShoppingCartServiceImpl(ProductRepository productRepository, OrdersRepository ordersRepository,
                                   ShoppingCartRepository shoppingCartRepository,
                                   UserRepository userRepository) {
        this.productRepository = productRepository;
        this.ordersRepository = ordersRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ShoppingCart addToCart(Integer productId, int quantity) throws Exception {
        UserDetails userDetailService = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetailService.getUsername();
        User user =  userRepository.findByUsername(username);

        Optional<Product> check = productRepository.findById(productId);
        if(check.isEmpty()){
            throw new RuntimeException("Product doesnot exist");
        }

        double totalCost;
        ShoppingCartPK shoppingCartPK = new ShoppingCartPK();
        ShoppingCart shoppingCart = new ShoppingCart();

        Orders finalOrder;
        Optional<Orders> order = ordersRepository.findByStatusAndUsers("pending",user);

        if(order.isPresent()){
            finalOrder = order.get();

            //Check if the cart has the same product again and increasing quantity
            List<ShoppingCart> carts = shoppingCartRepository.findAll();
            for(int i=0;i<carts.size();i++){
                if(carts.get(i).getId().getOrdersId()==finalOrder.getOrdersId()){
                    if(productId == carts.get(i).getId().getProductId()){
                        throw new Exception("Item already in cart");
                    }
                }
            }

            totalCost = finalOrder.getTotalCost();
            totalCost+=check.get().getProductPrice()*quantity;
            finalOrder.setTotalCost(totalCost);
            ordersRepository.save(finalOrder);
        }else{
            finalOrder = new Orders();
            finalOrder.setUser(user);
            finalOrder.setStatus("pending");
            totalCost=check.get().getProductPrice()*quantity;
            finalOrder.setTotalCost(totalCost);
            ordersRepository.save(finalOrder);
        }

        shoppingCartPK.setOrdersId(finalOrder.getOrdersId());
        shoppingCartPK.setProductId(check.get().getProductId());

        shoppingCart.setId(shoppingCartPK);
        shoppingCart.setOrder(finalOrder);
        shoppingCart.setProducts(check.get());
        shoppingCart.setQuantity(quantity);
        shoppingCart.setUser(user);

        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public Orders sendOrder(String address) {
        UserDetails userDetailService = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetailService.getUsername();
        User user =  userRepository.findByUsername(username);

        Optional<Orders> order = ordersRepository.findByStatusAndUsers("pending",user);
        if(order.isEmpty()){
            throw new RuntimeException("Order not found");
        }
        order.get().setAddress(address);
        order.get().setStatus("submitted");
        ordersRepository.save(order.get());

        return order.get();
    }

    @Override
    public NewOrderDto getCart() {
        UserDetails userDetailService = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetailService.getUsername();
        User user =  userRepository.findByUsername(username);

        Optional<Orders> order = ordersRepository.findByStatusAndUsers("pending",user);
        if(order.isEmpty()){
            return new NewOrderDto();
        }

        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        List<ProductQuantity> productQuantities = new ArrayList<>();

        for(int i=0;i<carts.size();i++){

            Integer prodId;
            Product tmp;
            ProductQuantity productQuantity = new ProductQuantity();

            if(carts.get(i).getId().getOrdersId()==order.get().getOrdersId()){
                prodId = carts.get(i).getId().getProductId();
                tmp = productRepository.findByProductId(prodId);

                productQuantity.setProductId(tmp.getProductId());
                productQuantity.setQuantity(carts.get(i).getQuantity());
                productQuantity.setProductPrice(tmp.getProductPrice());
                productQuantity.setProductDescription(tmp.getProductDescription());
                productQuantity.setCategory(tmp.getCategory());
                productQuantity.setAvailable(tmp.getAvailable());
                productQuantities.add(productQuantity);
            }

        }

        NewOrderDto newOrderDto = new NewOrderDto();
        newOrderDto.setProducts(productQuantities);
        newOrderDto.setTotalCost(order.get().getTotalCost());

        return newOrderDto;
    }

    @Override
    public NewOrderDto removeFromCart(Integer productId) {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();

        User user =  userRepository.findByUsername(username);

        Optional<Orders> order = ordersRepository.findByStatusAndUsers("pending",user);
        if(order.isEmpty()){
            throw new RuntimeException("Order not found");
        }

        List<ShoppingCart> carts = shoppingCartRepository.findAll();
        for(int i=0;i<carts.size();i++) {
            if (carts.get(i).getId().getOrdersId() == order.get().getOrdersId()) {
                if (productId == carts.get(i).getId().getProductId()) {

                    Integer quantity = carts.get(i).getQuantity();
                    Double totalPrice = order.get().getTotalCost();
                    Optional<Product> tmp = productRepository.findById(carts.get(i).getId().getProductId());
                    if(tmp.isEmpty()){
                        throw new RuntimeException("Product didnot found");
                    }
                    Double reducedPrice = tmp.get().getProductPrice() * quantity;
                    Double finalPrice = totalPrice - reducedPrice;

                    order.get().setTotalCost(finalPrice);
                    ordersRepository.save(order.get());

                    shoppingCartRepository.delete(carts.get(i));
                }
            }

        }
        return getCart();
    }

}
