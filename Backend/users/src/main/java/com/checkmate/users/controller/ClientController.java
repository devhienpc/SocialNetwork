package com.checkmate.users.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.checkmate.users.model.dto.requests.ChangeDisplayNameRequestDTO;
import com.checkmate.users.model.dto.responses.UserContextResponseDTO;
import com.checkmate.users.model.entity.User;
import com.checkmate.users.repository.UserRepository;
import com.checkmate.users.security.PermissionRequired;
import com.checkmate.users.security.RequiresJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class ClientController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUserContext")
    @PermissionRequired({})
    public ResponseEntity<?> getUserContext(@RequestHeader("Authorization") String authToken, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return  ResponseEntity.status(401).build();
        }

        User user = optionalUser.get();

        UserContextResponseDTO userContextResponseDTO = new UserContextResponseDTO();

        userContextResponseDTO.setUserId(user.getUserId());
        userContextResponseDTO.setUsername(user.getUsername());
        userContextResponseDTO.setDisplayName(user.getDisplayName());
        userContextResponseDTO.setCoins(user.getCoinTotal());

        return ResponseEntity.ok().body(userContextResponseDTO);
    }


    @PostMapping("/changeDisplayName")
    @RequiresJWT
    public ResponseEntity<?> changeDisplayName(@RequestBody ChangeDisplayNameRequestDTO newDisplayName, HttpServletRequest request) {
        DecodedJWT decodedJWT = (DecodedJWT) request.getAttribute("decodedJWT");

        if (decodedJWT == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<User> optionalUser = userRepository.findByCredentialId(decodedJWT.getClaim("userId").asLong());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = optionalUser.get();

        user.setDisplayName(newDisplayName.getDisplayName());

        userRepository.save(user);

        return ResponseEntity.ok().body(newDisplayName.getDisplayName());
    }

}
