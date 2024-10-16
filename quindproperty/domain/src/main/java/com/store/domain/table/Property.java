package com.store.domain.table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Property {
    @Id
    @Column(name = "propertyId", nullable = false)
    @UuidGenerator
    @GeneratedValue
    @Setter
    private UUID propertyId;

    @Column(nullable = false, unique = true)
    @Setter
    private String name;

    @ManyToOne
    @JoinColumn(name = "cityId", nullable = false)
    @Setter
    private City Location;

    @Column(nullable = false)
    @Setter
    private String img;

    @Column(nullable = false)
    @Setter
    private BigDecimal price;


    @Column(nullable = false)
    @Setter
    private Boolean available;


    @Column(nullable = false)
    @Setter
    private Boolean active = true;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCreated = new Date();
}