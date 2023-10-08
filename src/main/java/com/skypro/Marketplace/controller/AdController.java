package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.Ads;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.service.impl.AdService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing advertisements.
 */
@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;

    }

    /**
     * Get all advertisements.
     *
     * @return List of advertisements.
     */
    @GetMapping("/")
    public ResponseEntity<Ads> getAllAds() {
        Ads ads = adService.getAds();
        return ResponseEntity.ok(ads);
    }

    /**
     * Add a new advertisement.
     *
     * @param imageFile        Image file for the advertisement.
     * @param createOrUpdateAd Advertisement data.
     * @param authentication   Information about the current user's authentication.
     * @return Response about the created advertisement.
     */
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAd(
            @RequestPart("image") MultipartFile imageFile,
            @RequestPart("properties") CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication
    ) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        AdDTO adDTO = adService.createAd(createOrUpdateAd, securityUser.getId(), imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(adDTO);
    }

    /**
     * Get an extended advertisement by ID.
     *
     * @param adId Advertisement ID.
     * @return Extended advertisement information.
     */
    @GetMapping("/{adId}")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN')")
    public ResponseEntity<?> getAds(@PathVariable Integer adId) {

        ExtendedAd extendedAd = adService.getExtendedAdById(adId);
        return ResponseEntity.status(HttpStatus.OK).body(extendedAd);
    }

    /**
     * Remove an advertisement by ID.
     *
     * @param adId Advertisement ID.
     * @return HTTP response indicating success.
     */
    @DeleteMapping("/{adId}")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN')")
    public ResponseEntity<Void> removeAd(@PathVariable Integer adId) {

        adService.deleteAd(adId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update an advertisement by ID.
     *
     * @param adId             Advertisement ID.
     * @param createOrUpdateAd Updated advertisement data.
     * @return Updated advertisement information.
     */
    @PatchMapping("/{adId}")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN')")
    public ResponseEntity<AdDTO> updateAds(
            @PathVariable Integer adId,
            @RequestBody CreateOrUpdateAd createOrUpdateAd
    ) {

        AdDTO updatedAd = adService.updateAd(adId, createOrUpdateAd);
        return ResponseEntity.status(HttpStatus.OK).body(updatedAd);
    }

    /**
     * Get advertisements for the currently authenticated user.
     *
     * @param authentication Information about the current user's authentication.
     * @return List of advertisements for the current user.
     */
    @GetMapping("/me")
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN')")
    public ResponseEntity<List<AdDTO>> getAdsMe(Authentication authentication) {

        List<AdDTO> ads = adService.getAdsForCurrentUser(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ads);
    }

    /**
     * Update the image of an advertisement by ID.
     *
     * @param adId Advertisement ID.
     * @param imageFile New image file for the advertisement.
     * @return HTTP response indicating success.
     */
    @PatchMapping(value = "/{adId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@adService.isAdOwner(authentication, #adId) or hasRole('ADMIN')")
    public ResponseEntity<String> updateImage(
            @PathVariable Integer adId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        adService.updateAdImage(adId, imageFile);
        return ResponseEntity.status(HttpStatus.OK).body("Image updated successfully.");
    }

    /**
     * Retrieves an advertisement's image as a byte array and returns it as a ResponseEntity with appropriate HTTP headers.
     *
     * @param adId The ID of the advertisement for which to retrieve the image.
     * @return ResponseEntity containing the image as a byte array and appropriate HTTP headers.
     */
    @GetMapping("/ads/{adId}/image")
    public ResponseEntity<byte[]> getAdImage(@PathVariable Integer adId) {
        byte[] adImage = adService.getAdImage(adId);

        HttpHeaders headers = new HttpHeaders();
        if (adImage != null && adImage.length > 0) {
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(adImage, headers, HttpStatus.OK);
        } else {
            byte[] defaultImage = adService.getDefaultImageBytes();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(defaultImage, headers, HttpStatus.NOT_FOUND);
        }
    }
}