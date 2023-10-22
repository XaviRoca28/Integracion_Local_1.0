package com.bezkoder.springjwt.services;


import com.bezkoder.springjwt.models.EICall;
import com.bezkoder.springjwt.models.Player;
import com.bezkoder.springjwt.payload.request.RequestEICallRootDTO;
import com.bezkoder.springjwt.payload.request.RequestSignupRootDTO;
import com.bezkoder.springjwt.payload.response.JwtResponse;
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
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EICallService {

    @Autowired
    EICallRepository eiCallRepository;

    @Autowired
    PlayerRepository playerRepository;

    private Long idPlayer;

    public ResponseRootDTO betResponse(String token, String username,@RequestBody RequestEICallRootDTO requestEICallRootDTO) {

       Optional<Player> jugador = playerRepository.findByUsername(username);

        idPlayer = jugador.map(Player::getId).orElse(-1L);

        Player player = playerRepository.getReferenceById(idPlayer);

        if (requestEICallRootDTO.getRequest().getParameters().getBalance() < 20) {
            throw new ApplicationException(ApiErrorResponse.INVALID_AMOUNT, HttpStatus.BAD_REQUEST);
        } else if (playerRepository.getReferenceById(idPlayer).getBalance() < requestEICallRootDTO.getRequest().getParameters().getBalance()) {
            throw new ApplicationException(ApiErrorResponse.INSUFFICIENT_FUNDS, HttpStatus.NOT_ACCEPTABLE);
        }


        saveCall(new EICall(
                getRoundId(getPlayerId()),
                null,
                "bet",
                requestEICallRootDTO.getRequest().getParameters().getBalance(), player));

        updatePlayerBalance(requestEICallRootDTO.getRequest().getParameters().getBalance(), idPlayer, true);
        Integer balance = playerRepository.getReferenceById(idPlayer).getBalance();
        ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(token,balance);
        ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);
        ResponseRootDTO responseRootDTO = new ResponseRootDTO(responseDTO);

        return responseRootDTO;
    }

    public ResponseRootDTO winResponse(String token, String username,@RequestBody RequestEICallRootDTO requestEICallRootDTO) {

        Optional<Player> jugador = playerRepository.findByUsername(username);

        idPlayer = jugador.map(Player::getId).orElse(-1L);

        Player player = playerRepository.getReferenceById(idPlayer);

        if (requestEICallRootDTO.getRequest().getParameters().getBalance() < 0) {
            throw new ApplicationException(ApiErrorResponse.INVALID_AMOUNT, HttpStatus.BAD_REQUEST);
        } else if (eiCallRepository.findfuctionName(idPlayer).get(0)[0].toString().equals("win")) {
            throw new ApplicationException(ApiErrorResponse.GENERAL_TRANSACTION_ERROR, HttpStatus.BAD_REQUEST);
        }


        saveCall(new EICall(
                getRoundId(getPlayerId()),
                null,
                "win",
                requestEICallRootDTO.getRequest().getParameters().getBalance(), player));

        updatePlayerBalance(requestEICallRootDTO.getRequest().getParameters().getBalance(), idPlayer, false);
        Integer balance = playerRepository.getReferenceById(idPlayer).getBalance();
        ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(token,balance);
        ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);
        ResponseRootDTO responseRootDTO = new ResponseRootDTO(responseDTO);

        return responseRootDTO;
    }


    private void saveCall(EICall EICall) {
        eiCallRepository.save(EICall);
    }


    private Integer getRoundId(Long id) {

        List<Object[]> result = eiCallRepository.findTopByPlayerId(id);
        Integer roundId = 1;
        String functionName = "";
        for (Object[] data : result) {
            roundId = (Integer) data[0];
            functionName = (String) data[1];
        }

        if (functionName.equals("win")) {
            roundId++;
        }


        return roundId;
    }

    private Long getPlayerId() {
        Player a = playerRepository.getReferenceById(idPlayer);

        return a.getId();
    }


    private void updatePlayerBalance(Integer amount, Long id, boolean isBet) {
        Integer balance = playerRepository.getReferenceById(id).getBalance();
        if (isBet) {
            balance -= amount;
            playerRepository.setPlayerBalance(balance, id);
            return;
        }
        balance += amount;
        playerRepository.setPlayerBalance(balance, id);

    }

}
