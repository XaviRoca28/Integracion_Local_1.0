package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.Player;
import com.bezkoder.springjwt.payload.response.ResponseDTO;
import com.bezkoder.springjwt.payload.response.ResponseParametersDTO;
import com.bezkoder.springjwt.payload.response.ResponseRootDTO;
import com.bezkoder.springjwt.repository.PlayerRepository;
import com.bezkoder.springjwt.security.utils.ApiErrorResponse;
import com.bezkoder.springjwt.security.utils.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    public List<Player> findAll() {

        return playerRepository.findAll();
    }


    public Optional<Player> findById(Long id) {

        return playerRepository.findById(id);
    }


    public ResponseRootDTO getBalance(Long id) {

        if (playerRepository.findById(id).isEmpty()) {
            throw new ApplicationException(ApiErrorResponse.INVALID_USER_ID, HttpStatus.UNAUTHORIZED);
        }

        Integer balance = playerRepository.getReferenceById(id).getBalance();
        ResponseParametersDTO responseParametersDTO = new ResponseParametersDTO(balance);
        ResponseDTO responseDTO = new ResponseDTO(responseParametersDTO);
        return new ResponseRootDTO(responseDTO);
    }


}
