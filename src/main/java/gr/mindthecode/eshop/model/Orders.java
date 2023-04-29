package gr.mindthecode.eshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ordersId;

    private Double totalCost;

    private String address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Collection<ShoppingCart> orderProducts = new ArrayList<>();

    @ManyToOne( cascade = {CascadeType.PERSIST})
    @JoinColumn(
            name="user_id",
            referencedColumnName = "userId"
    )
    private User users;


    private String status;

    //Getters and Setters

    public Integer getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(Integer ordersId) {
        this.ordersId = ordersId;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Collection<ShoppingCart> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Collection<ShoppingCart> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public User getUser() {
        return users;
    }

    public void setUser(User user) {
        this.users = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
