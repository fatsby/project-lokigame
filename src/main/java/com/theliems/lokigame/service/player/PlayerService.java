package com.theliems.lokigame.service.player;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.PlayerError;
import com.theliems.lokigame.mapper.player.PlayerMapper;
import com.theliems.lokigame.model.dto.player.PlayerResponseDTO;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PlayerService implements PlayerServiceInterface{
    PlayerRepository playerRepository;
    PlayerMapper playerMapper;
    ExceptionFactory exceptionFactory;

    @Override
    public PlayerResponseDTO getMe() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Player", "username", username, PlayerError.PLAYER_NOT_FOUND));
        return playerMapper.toDTO(player);
    }
}
