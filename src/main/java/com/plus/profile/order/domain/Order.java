package com.plus.profile.order.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ORDERS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ORDER_ID")
    private UUID id;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;

    @Column(name = "ORDER_AMOUNT", nullable = false)
    private Long orderAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private OrderStatus orderStatus;
}
