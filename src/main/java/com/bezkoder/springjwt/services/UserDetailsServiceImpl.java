package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bezkoder.springjwt.repository.PlayerRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  PlayerRepository playerRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Player player = playerRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

    return UserDetailsImpl.build(player);
  }

}
