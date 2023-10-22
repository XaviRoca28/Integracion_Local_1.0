package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.RequestEICallDTO;
import com.bezkoder.springjwt.payload.request.RequestEICallRootDTO;
import com.bezkoder.springjwt.payload.request.RequestSignupRootDTO;
import com.bezkoder.springjwt.payload.response.ResponseRootDTO;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.services.EICallService;
import com.bezkoder.springjwt.services.PlayerService;
import com.bezkoder.springjwt.services.UndoBetWinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/swtest/api")
public class MainController {

    @Autowired
    PlayerService playerService;

    @Autowired
    EICallService eiCallService;

    @Autowired
    UndoBetWinService undoBetWinService;

    @Autowired
     JwtUtils jwtUtils;

    @GetMapping("/getbalance")
    public ResponseRootDTO getBalance(Long id){
        return  playerService.getBalance(id);

    }

    @PostMapping("/bet")
    public ResponseRootDTO bet(@RequestHeader("Authorization") String authorizationHeader,@RequestBody RequestEICallRootDTO requestEICallRootDTO){
       String token = authorizationHeader.substring(7);
       String username = jwtUtils.getUserNameFromJwtToken(token);
        return eiCallService.betResponse(token,username,requestEICallRootDTO);
    }


    @PostMapping("/win")
    public ResponseRootDTO win(@RequestHeader("Authorization") String authorizationHeader,@RequestBody RequestEICallRootDTO requestEICallRootDTO){
        String token = authorizationHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return eiCallService.winResponse(token,username,requestEICallRootDTO);
    }

    @PostMapping("/undoBet")
    public ResponseRootDTO undoBet(@RequestHeader("Authorization") String authorizationHeader,@RequestBody RequestEICallRootDTO requestEICallRootDTO){
        String token = authorizationHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return undoBetWinService.undoBetResponse(token,username,requestEICallRootDTO);
    }

    @PostMapping("/undoWin")
    public ResponseRootDTO undoWin(@RequestHeader("Authorization") String authorizationHeader,@RequestBody RequestEICallRootDTO requestEICallRootDTO){
        String token = authorizationHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return undoBetWinService.undoWinResponse(token,username,requestEICallRootDTO);
    }





}
