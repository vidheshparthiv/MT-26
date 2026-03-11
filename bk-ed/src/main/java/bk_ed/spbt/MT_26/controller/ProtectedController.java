package bk_ed.spbt.MT_26.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted!");
        response.put("username", username);
        response.put("status", "authenticated");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("roles", auth.getAuthorities());
        response.put("authenticated", auth.isAuthenticated());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello")
    public ResponseEntity<?> helloUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return ResponseEntity.ok("Hello " + username + "! Your JWT token is working! ✅");
    }
}
