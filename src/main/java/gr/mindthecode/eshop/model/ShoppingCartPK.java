package gr.mindthecode.eshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ShoppingCartPK implements Serializable {
    @Column(name = "orders_id")
    private Integer ordersId;

    @Column(name = "product_id")
    private Integer productId;

    public Integer getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(Integer orderId) {
        this.ordersId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
