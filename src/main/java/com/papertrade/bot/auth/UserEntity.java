package com.papertrade.bot.auth;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class UserEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private UserRole role=UserRole.USER;
    @Column(nullable=false) private boolean enabled=true;
    @Column(nullable=false) private Instant createdAt=Instant.now();
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public UserRole getRole(){return role;} public boolean isEnabled(){return enabled;} public Instant getCreatedAt(){return createdAt;}
    public void setName(String v){name=v;} public void setEmail(String v){email=v;} public void setPasswordHash(String v){passwordHash=v;} public void setRole(UserRole v){role=v;} public void setEnabled(boolean v){enabled=v;}
}
