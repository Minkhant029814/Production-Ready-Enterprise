package com.security.jwtdemo.entity.mysql;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "_user") // 'user' သည် SQL Keyword ဖြစ်နိုင်သောကြောင့် table name ကို _user ဟု ပေးထားခြင်းဖြစ်သည်

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Default Constructor (JPA အတွက် လိုအပ်သည်)
    public User() {}

    public User(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- UserDetails Interface Implementation Methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role ကို Spring Security နားလည်သော GrantedAuthority အဖြစ် ပြောင်းလဲခြင်း (e.g. ROLE_USER)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Username အဖြစ် email ကို သုံးထားသည်
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


    // Getters & Setters ...
    public Long getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}