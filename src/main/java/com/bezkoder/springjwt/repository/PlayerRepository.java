package com.bezkoder.springjwt.repository;

import java.util.Optional;

import com.bezkoder.springjwt.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
  Optional<Player> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);


  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = "update player set balance =:balance where id =:id", nativeQuery = true)
  void setPlayerBalance(@Param("balance") Integer balance, @Param("id") Long id);

}
