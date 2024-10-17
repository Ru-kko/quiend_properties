package com.store.domain.table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Rent {
    @Id
    @UuidGenerator
    @GeneratedValue
    @JoinColumn(name = "rentId", nullable = false)
    private UUID rentId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "propertyId", nullable = false)
    private Property property;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date rentDate = new Date();
}