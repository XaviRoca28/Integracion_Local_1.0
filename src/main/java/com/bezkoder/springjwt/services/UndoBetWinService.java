package com.bezkoder.springjwt.services;



import com.bezkoder.springjwt.models.EICall;
import com.bezkoder.springjwt.models.Player;
import com.bezkoder.springjwt.payload.request.RequestEICallRootDTO;
import com.bezkoder.springjwt.payload.response.ResponseDTO;
import com.bezkoder.springjwt.payload.response.ResponseParametersDTO;
import com.bezkoder.springjwt.payload.response.ResponseRootDTO;
import com.bezkoder.springjwt.repository.EICallRepository;
import com.bezkoder.springjwt.repository.PlayerRepository;
import com.bezkoder.springjwt.security.utils.ApiErrorResponse;
import com.bezkoder.springjwt.security.utils.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UndoBetWinService {

    @Autowired
    EICallRepository eiCallRepository;

    @Autowired
    PlayerRepository playerRepository;

    private Long idPlayer;

    public ResponseRootDTO undoBetResponse(String token, String username,@RequestBody RequestEICallRootDTO requestEICallRootDTO){

        Optional<Player> jugador = playerRepository.findByUsername(username);

        idPlayer = jugador.map(Player::getId).orElse(-1L);

        insertUndoBet(requestEICallRootDTO.getRequest().getParameters().getRequestId());

        Integer balance = playerRepository.getReferenceById(idPlayer).getBalance();
        ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(token, balance);
        ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);

        return new ResponseRootDTO(responseDTO);
    }

    public ResponseRootDTO undoWinResponse(String token, String username,@RequestBody RequestEICallRootDTO requestEICallRootDTO){

        Optional<Player> jugador = playerRepository.findByUsername(username);

        idPlayer = jugador.map(Player::getId).orElse(-1L);

        insertUndoWin(requestEICallRootDTO.getRequest().getParameters().getRequestId());

        Integer balance = playerRepository.getReferenceById(idPlayer).getBalance();
        ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(token, balance);
        ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);

        return new ResponseRootDTO(responseDTO);
    }


    private void insertUndoBet(Long requestId){

        if (eiCallRepository.findRequestId(requestId).isEmpty()){

            throw new ApplicationException(ApiErrorResponse.INVALID_REQUEST_ID, HttpStatus.BAD_REQUEST);
        } else if (!eiCallRepository.findRequestId(requestId).get(0)[1].equals("bet") || !eiCallRepository.findUndoRequestId(requestId).isEmpty()) {
            throw new ApplicationException(ApiErrorResponse.GENERAL_TRANSACTION_ERROR, HttpStatus.BAD_REQUEST);
        }


        List<Object[]> result = eiCallRepository.findRoundByUndoBet(requestId);


        Object amount = ((Object[]) result.get(0))[0];
        Integer integerAmount = (Integer) amount;


        Object roundId = ((Object[]) result.get(0))[1];
        Integer integerRoundId = (Integer) roundId;

        Object playerId = ((Object[]) result.get(0))[2];
        BigInteger bigIntegerPlayerId = (BigInteger) playerId;
        Long longPlayerId = bigIntegerPlayerId.longValue();

        Player player = playerRepository.getReferenceById(longPlayerId);



        eiCallRepository.save(new EICall(
                integerRoundId,
                requestId,
                "undoBet",
                integerAmount,
                player
        ));


        updatePlayerBalance(integerAmount, longPlayerId, true);
    }

    private void insertUndoWin(Long requestId){

        if (eiCallRepository.findRequestId(requestId).isEmpty()){
            throw new ApplicationException(ApiErrorResponse.INVALID_REQUEST_ID, HttpStatus.BAD_REQUEST);
        } else if (!eiCallRepository.findRequestId(requestId).get(0)[1].equals("win") || !eiCallRepository.findUndoRequestId(requestId).isEmpty()) {
            throw new ApplicationException(ApiErrorResponse.GENERAL_TRANSACTION_ERROR, HttpStatus.BAD_REQUEST);
        }

        List<Object[]> result = eiCallRepository.findRoundByUndoBet(requestId);


        Object amount = ((Object[]) result.get(0))[0];
        Integer integerAmount = (Integer) amount;


        Object roundId = ((Object[]) result.get(0))[1];
        Integer integerRoundId = (Integer) roundId;

        Object playerId = ((Object[]) result.get(0))[2];
        BigInteger bigIntegerPlayerId = (BigInteger) playerId;
        Long longPlayerId = bigIntegerPlayerId.longValue();

        Player player = playerRepository.getReferenceById(longPlayerId);



        eiCallRepository.save(new EICall(
                integerRoundId,
                requestId,
                "undoWin",
                integerAmount,
                player
        ));


        updatePlayerBalance(integerAmount, longPlayerId, false);


    }

    private void updatePlayerBalance(Integer amount, Long id, boolean isUndoBet){

        Integer balance = playerRepository.getReferenceById(id).getBalance();

        playerRepository.setPlayerBalance(balance, id);

        if (isUndoBet){
            balance += amount;
            playerRepository.setPlayerBalance(balance, id);
            return;
        }
        balance -= amount;
        playerRepository.setPlayerBalance(balance, id);


    }

}
