package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    private PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/rest/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllPlayers(@RequestParam(name = "name", required = false) String name) {
        return playerService.getAllPlayers();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Player> findById(@PathVariable Long id) {
        if (id <= 0) throw new IllegalArgumentException("Incorrect input ID value.");

        Player player = playerService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Player not found in DB"));

        return new ResponseEntity<>(player, HttpStatus.OK);
    }
}
