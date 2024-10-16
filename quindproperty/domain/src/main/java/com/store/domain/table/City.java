package com.store.domain.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class City {
    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(name = "cityId", nullable = false)
    private UUID cityId;

    @Column(nullable = false)
    private String name;
}
