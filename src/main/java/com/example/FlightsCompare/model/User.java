package com.example.FlightsCompare.model;

import com.example.FlightsCompare.config.DefaultAdmin;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode

@Entity
@Table(name = "USER_")
public class User implements UserDetails {

    @Id
    @NonNull
    private UUID id;

    private String username;

    @Column(unique = true)
    @NonNull
    private String email;

    private String avatarUrl;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LinkedProvider> linkedProviders = new ArrayList<>();




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(DefaultAdmin.getDefaultAdminEmail().equals(email))
            return Arrays.stream(UserRole.values()).map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).toList();
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).toList();
    }

    public String getActualUsername() {
        return username;
    }

    // used by spring security UserDetails
    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
