package com.epages.microservice.handson.order;

import org.javamoney.moneta.Money;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "PIZZA_ORDER")
@EntityListeners(AuditingEntityListener.class)
public class Order implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Basic
    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, insertable = true, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LineItem> items = new ArrayList<>();

    @Basic
    @Column(name = "COMMENT", length = 255)
    private String comment;

    @Embedded
    private Address deliveryAddress;

    @Version
    private Integer version;

    @Basic
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT", nullable = false, insertable = true, updatable = true)
    private LocalDateTime lastModifiedAt;

    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public MonetaryAmount getTotalPrice() {
        return items.stream()
                .map(lineItem -> lineItem.getPrice().multiply(lineItem.getAmount()))
                .reduce(MonetaryAmount::add).orElse(Money.of(0.0, "EUR"));
    }
}
