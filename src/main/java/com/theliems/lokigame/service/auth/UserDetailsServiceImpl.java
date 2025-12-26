package com.theliems.lokigame.service.auth;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDetailsServiceImpl implements UserDetailsService {
    PlayerRepository playerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return new User(
                player.getUsername(),
                player.getPasswordHash(),
                getAuthority(player)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthority(Player player) {
        GrantedAuthority authority = new SimpleGrantedAuthority(player.getRole().name());
        return List.of(authority);
    }
}
