package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Player;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.payload.request.RequestLoginRootDTO;
import com.bezkoder.springjwt.payload.request.RequestSignupRootDTO;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.payload.response.ResponseDTO;
import com.bezkoder.springjwt.payload.response.ResponseParametersDTO;
import com.bezkoder.springjwt.payload.response.ResponseRootDTO;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.PlayerRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/swtest/api/login")
public class LoginController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  PlayerRepository playerRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody RequestLoginRootDTO requestLoginRootDTO) {



    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(requestLoginRootDTO.getRequest().getParameters().getUsername(), requestLoginRootDTO.getRequest().getParameters().getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(jwt);
    ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);
    ResponseRootDTO responseRootDTO = new ResponseRootDTO(responseDTO);

    return ResponseEntity.ok(responseRootDTO);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RequestSignupRootDTO requestSignupRootDTO) {
    if (playerRepository.existsByUsername(requestSignupRootDTO.getRequest().getParameters().getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (playerRepository.existsByEmail(requestSignupRootDTO.getRequest().getParameters().getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    Player player = new Player(requestSignupRootDTO.getRequest().getParameters().getUsername(),
            requestSignupRootDTO.getRequest().getParameters().getEmail(),
               encoder.encode(requestSignupRootDTO.getRequest().getParameters().getPassword()),
            requestSignupRootDTO.getRequest().getParameters().getBalance(),
            requestSignupRootDTO.getRequest().getParameters().getCountry());

    Set<String> strRoles = requestSignupRootDTO.getRequest().getParameters().getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    player.setRoles(roles);
    playerRepository.save(player);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
