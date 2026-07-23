package com.encasa.controllers;

import com.encasa.models.Professional;
import com.encasa.services.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<Professional>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getFavorites(userDetails.getUsername()));
    }

    @PostMapping("/{professionalId}")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long professionalId) {
        favoriteService.addFavorite(userDetails.getUsername(), professionalId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{professionalId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long professionalId) {
        favoriteService.removeFavorite(userDetails.getUsername(), professionalId);
        return ResponseEntity.noContent().build();
    }
}
