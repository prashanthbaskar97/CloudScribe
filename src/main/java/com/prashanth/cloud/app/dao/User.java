package com.prashanth.cloud.app.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="user_details")
public class User implements UserDetails {
    @Column(nullable = false)
    private String first_name;
    @Column(nullable = false)
    private String last_name;
    @Id
    @Column(name="email", unique=true, nullable=false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Date account_created;
    @Column(nullable = false)
    private Date account_updated;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public List<Assignment> assignments = new ArrayList<>();

    public User(String first_name, String last_name, String email, String password, Date account_created, Date account_updated) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.account_created = account_created;
        this.account_updated = account_updated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
