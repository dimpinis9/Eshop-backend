package gr.mindthecode.eshop.service.impl;


import gr.mindthecode.eshop.dto.ProductQuantity;
import gr.mindthecode.eshop.model.Orders;
import gr.mindthecode.eshop.model.Product;
import gr.mindthecode.eshop.model.ShoppingCart;
import gr.mindthecode.eshop.model.User;
import gr.mindthecode.eshop.repository.OrdersRepository;
import gr.mindthecode.eshop.repository.ProductRepository;
import gr.mindthecode.eshop.repository.ShoppingCartRepository;
import gr.mindthecode.eshop.repository.UserRepository;
import gr.mindthecode.eshop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private OrdersRepository ordersRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private ShoppingCartRepository shoppingCartRepository;

    public OrderServiceImpl(OrdersRepository ordersRepository,UserRepository userRepository,ProductRepository productRepository,
                            ShoppingCartRepository shoppingCartRepository){
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    @Override
    public Page<Orders> findByStatus(int page, int size, String sort) {
        PageRequest paging = PageRequest
                .of(page, size)
                .withSort(sort.equalsIgnoreCase("ASC") ?
                        Sort.by("totalCost").ascending() :
                        Sort.by("totalCost").descending());

        Page<Orders> res = ordersRepository.findByStatus("submitted",paging);
        return res;
    }

    @Override
    public Page<Orders> getOrders(String address,String status, int page, int size, String sort) {
        PageRequest paging = PageRequest
                .of(page, size)
                .withSort(sort.equalsIgnoreCase("ASC") ?
                        Sort.by("totalCost").ascending() :
                        Sort.by("totalCost").descending());

        Page<Orders> res = null;
        if (address == null && status==null) {
            res = ordersRepository.findAll(paging);
        } 
        if(address != null && status==null){
            res = ordersRepository.findByAddressContaining(address,paging);
        }//
        if(address == null && status!=null){
            res = ordersRepository.findByStatus(status,paging);
        }
        if(address != null && status!=null){
            res = ordersRepository.findByAddressContainingAndStatus(address,status,paging);
        }

        return res;
    }

    public List<Orders> getMyOrders(String status){
        UserDetails userDetailService = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetailService.getUsername();
        User user =  userRepository.findByUsername(username);
        List<Orders> finalList = new ArrayList<>();

        List<Orders> myOrders = ordersRepository.findOrdersByStatusAndUsers("submitted",user);
        List<Orders> myOrders2 = ordersRepository.findOrdersByStatusAndUsers("completed",user);

        if(status==null){
            myOrders.addAll(myOrders2);
            finalList.addAll(myOrders);
        }else{
            if(status.equals("submitted")){
                finalList.addAll(myOrders);
            }
            if(status.equals("completed")){
                finalList.addAll(myOrders2);
            }
        }


        return finalList;
    }

    public  List<ProductQuantity> getProductsFromOrder(Integer id){
        Optional<Orders> order = ordersRepository.findById(id);

        List<ShoppingCart> cart = shoppingCartRepository.findAll();
        List<Product> products = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for(int i=0;i< cart.size();i++){
            if(cart.get(i).getId().getOrdersId()==order.get().getOrdersId()){
                Integer tmpId = cart.get(i).getId().getProductId();
                Integer tmpQuantity = cart.get(i).getQuantity();
                Product tmpProd = productRepository.findByProductId(tmpId);
                products.add(tmpProd);
                quantities.add(tmpQuantity);
            }

        }

        List<ProductQuantity> finalProds = new ArrayList<>();
        for(int i=0;i< products.size();i++){
           ProductQuantity productQuantity = new ProductQuantity();

           productQuantity.setProductId(products.get(i).getProductId());
           productQuantity.setProductDescription(products.get(i).getProductDescription());
           productQuantity.setQuantity(quantities.get(i));
           productQuantity.setProductPrice(products.get(i).getProductPrice());
           productQuantity.setCategory(products.get(i).getCategory());

           finalProds.add(productQuantity);
        }

        return finalProds;
    }

    public Orders validateOrder(Integer id){
        Orders orders = ordersRepository.findOrdersByStatusAndOrdersId("submitted",id);
        orders.setStatus("completed");
        return  ordersRepository.save(orders);
    }
}
