package com.example.FlightsCompare.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class RefreshToken {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    private String refreshToken;

    private Long lastRefresh;
}
