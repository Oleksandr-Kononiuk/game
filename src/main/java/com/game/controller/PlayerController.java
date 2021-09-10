package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerFilterCriteria;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    //0.5h
    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    //0.5h
    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    //~2.5+4,5+3
    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Player> getAllPlayers(
            //, defaultValue = ""
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        PlayerFilterCriteria playerFilterCriteria = new PlayerFilterCriteria(name, title, race,
                profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        return playerService.getAllPlayers(playerFilterCriteria, order, pageNumber, pageSize);
    }

    @GetMapping("/players/count")
    //@ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Integer> countPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {

        PlayerFilterCriteria playerFilterCriteria = new PlayerFilterCriteria(name, title, race,
                profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);

        return new ResponseEntity<>(playerService.countPlayerWithFilters(title), HttpStatus.OK);
    }

    //~3h
    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Player findById(@PathVariable Long id) {
        return playerService.findById(id);
    }

    //~1h
    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return playerService.updatePlayer(id, player);
    }
}
