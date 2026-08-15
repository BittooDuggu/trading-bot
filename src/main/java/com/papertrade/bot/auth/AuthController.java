package com.papertrade.bot.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users; private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
  public AuthController(UserRepository users){this.users=users;}
  @PostMapping("/register") public ResponseEntity<?> register(@RequestBody RegisterRequest r){
    String email=r.email()==null?"":r.email().trim().toLowerCase();
    if(email.isBlank()||r.name()==null||r.name().isBlank()||r.password()==null||r.password().length()<8) return ResponseEntity.badRequest().body(Map.of("error","Name, email and password (minimum 8 characters) are required."));
    if(users.findByEmailIgnoreCase(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Email already registered."));
    UserEntity u=new UserEntity(); u.setName(r.name().trim()); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password())); u.setRole(UserRole.USER); users.save(u);
    return ResponseEntity.ok(Map.of("message","Registration successful. Please login."));
  }
  @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest r,HttpSession s){
    UserEntity u=users.findByEmailIgnoreCase(r.email()==null?"":r.email().trim()).orElse(null);
    if(u==null||!u.isEnabled()||!encoder.matches(r.password()==null?"":r.password(),u.getPasswordHash())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid email or password."));
    s.setAttribute("USER_ID",u.getId()); s.setAttribute("ROLE",u.getRole().name());
    return ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole().name()));
  }
  @PostMapping("/logout") public ResponseEntity<?> logout(HttpSession s){s.invalidate();return ResponseEntity.ok(Map.of("message","Logged out"));}
  @GetMapping("/me") public ResponseEntity<?> me(HttpSession s){UserEntity u=current(s); return u==null?ResponseEntity.status(401).body(Map.of("error","Not logged in")):ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole().name()));}
  public UserEntity current(HttpSession s){Object id=s.getAttribute("USER_ID"); return id==null?null:users.findById((Long)id).orElse(null);}
  public record RegisterRequest(String name,String email,String password){} public record LoginRequest(String email,String password){}
}
