package gr.mindthecode.eshop.controller;

import gr.mindthecode.eshop.dto.NewOrderDto;
import gr.mindthecode.eshop.model.Orders;
import gr.mindthecode.eshop.model.ShoppingCart;
import gr.mindthecode.eshop.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eshop")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/cart")
    public NewOrderDto getCart(){
        return shoppingCartService.getCart();
    }

    @PostMapping("/cart/add")
    public ShoppingCart addToCart(@RequestParam Integer productId
            , @RequestParam(defaultValue = "0") int quantity) throws Exception {
        return shoppingCartService.addToCart(productId,quantity);
    }

    @DeleteMapping("/cart/remove")
    public NewOrderDto removeFromCart(@RequestParam Integer productId){
        return shoppingCartService.removeFromCart(productId);
    }

    @PostMapping("/cart/checkout")
    public Orders sendOrder(@RequestParam String address){
        return shoppingCartService.sendOrder(address);
    }

}
