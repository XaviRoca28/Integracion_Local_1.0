package com.bezkoder.springjwt.repository;



import com.bezkoder.springjwt.models.EICall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EICallRepository extends JpaRepository<EICall, Long> {

    @Query(value = "SELECT e.round_id, e.function_name " + "FROM EICall e " +
            "WHERE e.player_id = :playerId AND e.function_name NOT LIKE 'undo%' " + "ORDER BY e.request_id DESC " + "LIMIT 1", nativeQuery = true)
    List<Object[]> findTopByPlayerId(@Param("playerId") Long playerId);

    @Query(value = "SELECT e.amount, e.round_id, e.player_id " + "FROM EICall e " +
            "WHERE e.request_id = :requestId ", nativeQuery = true)
    List<Object[]> findRoundByUndoBet(@Param("requestId") Long requestId);

    @Query(value = "SELECT e.function_name FROM EICall e WHERE e.player_id = :playerId " +
            "AND e.function_name NOT LIKE 'undo%' ORDER BY e.request_id DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findfuctionName(@Param("playerId")Long playerId);

    @Query(value = "SELECT e.request_id, e.function_name " + "FROM EICall e " +
            "WHERE e.request_id = :requestId AND e.function_name NOT LIKE 'undo%'", nativeQuery = true)
    List<Object[]> findRequestId(@Param("requestId") Long requestId);

    @Query(value = "SELECT * FROM EICall e WHERE e.undo_request_id = :requestId", nativeQuery = true)
    List<Object[]> findUndoRequestId(@Param("requestId") Long requestId);


}
