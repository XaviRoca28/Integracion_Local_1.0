package com.bezkoder.springjwt.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "eicall")
public class EICall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @Column(name = "round_id")
    private Integer roundId;
    @Column(name = "undo_request_id")
    private Long undoRequestID = null;
    @Column(name = "function_name")
    private String functionName;
    private Integer amount;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player playerId;

    public EICall(Integer roundId, Long undoRequestID, String functionName, Integer amount, Player playerId) {
        this.roundId = roundId;
        this.undoRequestID = undoRequestID;
        this.functionName = functionName;
        this.amount = amount;
        this.playerId = playerId;
    }
}
