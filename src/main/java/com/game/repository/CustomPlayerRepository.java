package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;

import java.util.List;

public interface CustomPlayerRepository {
    List<Player> getAllWithFilters(PlayerFilterCriteria playerFilterCriteria,
                                              PlayerOrder order, Integer pageNumber, Integer pageSize);

    Integer countPlayersWithFilters(PlayerFilterCriteria filter);
}
