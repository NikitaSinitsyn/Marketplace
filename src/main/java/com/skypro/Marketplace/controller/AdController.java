package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.Ads;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.service.impl.AdService;
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

    public AdController(AdService adService) {
        this.adService = adService;

    }

    @GetMapping("/")
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = adService.getAds();
        return ResponseEntity.ok(ads);
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or @adService.hasRole('ADMIN')")
    public ResponseEntity<?> addAd(
            @RequestPart("image") MultipartFile imageFile,
            @RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication
    ) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        AdDTO adDTO = adService.createAd(createOrUpdateAd, securityUser.getId(), imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(adDTO);
    }


    @GetMapping("/{adId}")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or @adService.hasRole('ADMIN')")
    public ResponseEntity<?> getAds(@PathVariable Integer adId) {

        ExtendedAd extendedAd = adService.getExtendedAdById(adId);
        return ResponseEntity.status(HttpStatus.OK).body(extendedAd);


    }

    @DeleteMapping("/{adId}")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or @adService.hasRole('ADMIN')")
    public ResponseEntity<Void> removeAd(@PathVariable Integer adId) {

        adService.deleteAd(adId);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{adId}")
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<AdDTO> updateAds(
            @PathVariable Integer adId,
            @RequestBody CreateOrUpdateAd createOrUpdateAd
    ) {

        AdDTO updatedAd = adService.updateAd(adId, createOrUpdateAd);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAd);

    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<List<AdDTO>> getAdsMe(Authentication authentication) {

        List<AdDTO> ads = adService.getAdsForCurrentUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ads);

    }

    @PatchMapping(value = "/{adId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') and (@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN'))")
    public ResponseEntity<String> updateImage(
            @PathVariable Integer adId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        adService.updateAdImage(adId, imageFile);
        return ResponseEntity.status(HttpStatus.OK).body("Image updated successfully.");

    }
}