package com.example.FlightsCompare.model;

import com.example.FlightsCompare.config.DefaultAdmin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<LinkedProvider> linkedProviders = new ArrayList<>();




    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(DefaultAdmin.getDefaultAdminEmail().equals(email))
            return Arrays.stream(UserRole.values()).map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).toList();
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).toList();
    }

    @JsonProperty("username")
    public String getActualUsername() {
        return username;
    }

    // used by spring security UserDetails
    @Override
    @JsonIgnore
    public String getUsername() {
        return id.toString();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
