package de.diedavids.cuba.defaultvalues.entity.example.sales;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@NamePattern("%s|orderId")
@Table(name = "DDCDV_ORDER")
@Entity(name = "ddcdv$Order")
public class Order extends StandardEntity {
    private static final long serialVersionUID = 3061046629726775722L;

    @NotNull
    @Column(name = "ORDER_ID", nullable = false)
    protected String orderId;

    @NotNull
    @Column(name = "ORDER_DATE", nullable = false)
    protected LocalDate orderDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "SHIPPING_DATE")
    protected Date shippingDate;

    @Column(name = "SHIPPING_MODE")
    protected Integer shippingMode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    @Column(name = "PRICE")
    protected BigDecimal price;

    @Column(name = "QUANTITY")
    protected Double quantity;

    @Column(name = "TOTAL_PRICE")
    protected BigDecimal totalPrice;

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }


    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }


    public void setShippingMode(ShipMode shippingMode) {
        this.shippingMode = shippingMode == null ? null : shippingMode.getId();
    }

    public ShipMode getShippingMode() {
        return shippingMode == null ? null : ShipMode.fromId(shippingMode);
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Date getShippingDate() {
        return shippingDate;
    }


}