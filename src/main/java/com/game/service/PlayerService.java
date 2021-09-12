package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.repository.PlayerFilterCriteria;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private static final long YEAR_2000 = 946684800000L;
    private static final long YEAR_3000 = 32503680000000L;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers(PlayerFilterCriteria playerFilterCriteria,
                                          PlayerOrder order, Integer pageNumber, Integer pageSize) {

        return playerRepository.getAllWithFilters(playerFilterCriteria, order, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public Player findById(Long id) {
        if (id <= 0) throw new IllegalArgumentException("Incorrect input ID value.");

        return playerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Player not found in DB"));
    }

    public void deletePlayer(Long id) {
        Player playerToDelete = this.findById(id);
        playerRepository.delete(playerToDelete);
    }

    @Transactional(readOnly = true)
    public int countPlayerWithFilters(PlayerFilterCriteria playerFilterCriteria) {
        return playerRepository.countPlayersWithFilters(playerFilterCriteria);
    }

    public Player createPlayer(Player player) {
        if (validatePlayer(player)) {
            if (player.getBanned() == null) {
                player.setBanned(false);
            }
            player.setLevel(player.calculateCurrentLevel());
            player.setUntilNextLevel(player.expToNextLevel());
        } else {
            throw new IllegalArgumentException("Incorrect given Player instance.");
        }
        return playerRepository.save(player);
    }

    private boolean validatePlayer(Player player) {
        return player.getName() != null
                && !player.getName().isEmpty()
                && player.getName().length() <= 12
                && player.getTitle() != null
                && player.getTitle().length() <= 30
                && player.getBirthday() != null
                && player.getBirthday().getTime() > YEAR_2000
                && player.getBirthday().getTime() < YEAR_3000
                && player.getRace() != null
                && player.getProfession() != null
                && player.getExperience() != null
                && player.getExperience() >= 0
                && player.getExperience() <= 10_000_000;
    }

    public Player updatePlayer(Long id, Player updatedPlayer) {
        Player playerToUpdate = this.findById(id);
        update(playerToUpdate, updatedPlayer);

        return playerRepository.save(playerToUpdate);
    }

    private void update(Player playerToUpdate, Player updatedPlayer) {
        if (updatedPlayer.getName() != null && updatedPlayer.getName().length() <= 12 && !updatedPlayer.getName().isEmpty()) {
            playerToUpdate.setName(updatedPlayer.getName());
        }
        if (updatedPlayer.getTitle() != null && updatedPlayer.getTitle().length() <= 30) {
            playerToUpdate.setTitle(updatedPlayer.getTitle());
        }
        if (updatedPlayer.getRace() != null) {
            playerToUpdate.setRace(updatedPlayer.getRace());
        }
        if (updatedPlayer.getProfession() != null) {
            playerToUpdate.setProfession(updatedPlayer.getProfession());
        }
        if (updatedPlayer.getBanned() != null) {
            playerToUpdate.setBanned(updatedPlayer.getBanned());
        }
        if (updatedPlayer.getBirthday() != null) {
            if (updatedPlayer.getBirthday().getTime() >= YEAR_2000 && updatedPlayer.getBirthday().getTime() <= YEAR_3000) {
                playerToUpdate.setBirthday(updatedPlayer.getBirthday());
            } else {
                throw new IllegalArgumentException("Updated player birthday out of bounds");
            }
        }
        if (updatedPlayer.getExperience() != null) {
            if (updatedPlayer.getExperience() >= 0 && updatedPlayer.getExperience() <= 10_000_000) {
                playerToUpdate.setExperience(updatedPlayer.getExperience());
                playerToUpdate.setLevel(playerToUpdate.calculateCurrentLevel());
                playerToUpdate.setUntilNextLevel(playerToUpdate.expToNextLevel());
            } else {
                throw new IllegalArgumentException("Updated player experience out of bounds.");
            }
        }
    }
}
