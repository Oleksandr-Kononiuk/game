package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.repository.CustomPlayerRepositoryImpl;
import com.game.repository.PlayerFilterCriteria;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers(PlayerFilterCriteria playerFilterCriteria,
                                          PlayerOrder order, Integer pageNumber, Integer pageSize) {

//        List<Player> players = customPlayerRepository.getAllWithFilter(playerFilterCriteria);
//        Sort sort = Sort.by(Sort.Direction.ASC, order.getFieldName());
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

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
    public int countPlayerWithFilters(String title) {
        return playerRepository.countPlayersWithFilters(title);
    }

    public Player createPlayer(Player player) {
        if (player.getName() == null || player.getName().isEmpty() || player.getName().length() > 12)
            throw new IllegalArgumentException("Player name null or length greater then 12 or empty.");
        if (player.getTitle() == null || player.getTitle().length() > 30)
            throw new IllegalArgumentException("Player title null or length greater then 30.");
        if (player.getBirthday() == null || player.getBirthday().getTime() <= 946684800000L || player.getBirthday().getTime() >= 32503680000000L)
            throw new IllegalArgumentException("Player birthday out of bounds or null.");
        if (player.getRace() == null || player.getProfession() == null)
            throw new IllegalArgumentException("Player race or profession not setup.");
        if (player.getBanned() == null)
            player.setBanned(false);
        if (player.getExperience() == null || player.getExperience() < 0 || player.getExperience() > 10_000_000) {
            throw new IllegalArgumentException("Player experience out of bounds or null.");
        } else {
            player.setLevel(player.calculateCurrentLevel());
            player.setUntilNextLevel(player.expToNextLevel());
        }
        return playerRepository.save(player);
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
            if (updatedPlayer.getBirthday().getTime() >= 946684800000L
                    && updatedPlayer.getBirthday().getTime() <= 32503680000000L) {
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
