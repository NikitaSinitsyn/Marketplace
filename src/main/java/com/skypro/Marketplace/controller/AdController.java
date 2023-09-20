package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.Ads;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.exception.UnauthorizedException;
import com.skypro.Marketplace.mapper.AdMapper;
import com.skypro.Marketplace.service.impl.AdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;
    private static final Logger logger = LoggerFactory.getLogger(AdController.class);

    @Autowired
    public AdController(AdService adService, AdMapper adMapper) {
        this.adService = adService;

    }

    @GetMapping("/")
    public ResponseEntity<Ads> getAllAds() {
        try {
            List<AdDTO> adsList = adService.getAllAds();
            int count = adsList.size();
            Ads ads = new Ads(count, adsList);
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addAd(
            @RequestPart("image") MultipartFile imageFile,
            @RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication
    ) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            AdDTO adDTO = adService.createAd(createOrUpdateAd);

            if (!imageFile.isEmpty()) {
                byte[] imageData = imageFile.getBytes();
                adService.saveAdImage(adDTO.getPk(), imageData);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(adDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid request data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while creating ad", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating ad: " + e.getMessage());
        }
    }


    @GetMapping("/{adId}")
    public ResponseEntity<?> getAds(@PathVariable Integer adId) {
        try {
            ExtendedAd extendedAd = adService.getExtendedAdById(adId);

            if (extendedAd != null) {
                return ResponseEntity.ok(extendedAd);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ad not found");
            }
        } catch (Exception e) {
            logger.error("An error occurred while fetching ad by ID: " + adId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @DeleteMapping("/{adId}")
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<Void> removeAd(@PathVariable Integer adId) {
        try {
            adService.deleteAd(adId);
            return ResponseEntity.noContent().build();
        } catch (AdNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("An error occurred while deleting ad with ID: " + adId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{adId}")
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<AdDTO> updateAds(
            @PathVariable Integer adId,
            @RequestBody CreateOrUpdateAd createOrUpdateAd
    ) {
        try {
            AdDTO updatedAd = adService.updateAd(adId, createOrUpdateAd);
            return ResponseEntity.ok(updatedAd);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (AdNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("An error occurred while updating ad with ID: " + adId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<AdDTO>> getAdsMe(Authentication authentication) {
        try {
            List<AdDTO> ads = adService.getAdsForCurrentUser(authentication);
            return ResponseEntity.ok(ads);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            logger.error("An error occurred while fetching ads for the current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{adId}/image")
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<byte[]> updateImage(
            @PathVariable Integer adId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            byte[] imageData = imageFile.getBytes();
            boolean success = adService.updateAdImage(adId, imageData);

            if (success) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(imageData);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (AdNotFoundException e) {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // HTTP 401 Unauthorized
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // HTTP 403 Forbidden
        } catch (Exception e) {
            logger.error("An error occurred while updating ad image for ID: " + adId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 Internal Server Error
        }
    }


}