package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.Ads;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.mapper.AdMapper;
import com.skypro.Marketplace.repository.AdRepository;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing advertisements.
 */
@Service
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(AdService.class);

    private final Environment environment;

    public AdService(AdRepository adRepository, AdMapper adMapper, UserRepository userRepository, Environment environment) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;

        this.environment = environment;
    }

    /**
     * Get all advertisements.
     *
     * @return List of advertisements.
     */
    public Ads getAds() {
        List<AdDTO> adsList;
        List<Ad> ads = adRepository.findAll();
        adsList = ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());

        int count = adsList.size();
        return new Ads(count, adsList);
    }

    /**
     * Create a new advertisement.
     *
     * @param createOrUpdateAd Advertisement data.
     * @param userId           User ID of the advertisement owner.
     * @param imageFile        Image file for the advertisement.
     * @return Created advertisement data.
     */
    @Transactional
    public AdDTO createAd(CreateOrUpdateAd createOrUpdateAd, Integer userId, MultipartFile imageFile) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            Ad ad = new Ad();
            ad.setTitle(createOrUpdateAd.getTitle());
            ad.setPrice(createOrUpdateAd.getPrice());
            ad.setDescription(createOrUpdateAd.getDescription());
            ad.setUser(user);

            ad = adRepository.save(ad);

            if (!imageFile.isEmpty()) {
            String imageName = userId + "_" + imageFile.getOriginalFilename();
            String imagePath = environment.getProperty("image.upload.path");

            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Path filePath = Paths.get(imagePath, imageName);
                    Files.write(filePath, imageFile.getBytes());
                    ad.setImage(imageName);
                    adRepository.save(ad);
                } catch (IOException e) {
                    logger.error("An error occurred while processing the image: {}", e.getMessage());
                }
            } else {
                logger.error("Image upload path is not configured.");
            }
        }

            return adMapper.adToAdDTO(ad);
    }

    /**
     * Get an extended advertisement by ID.
     *
     * @param adId Advertisement ID.
     * @return Extended advertisement information.
     */
    @Transactional
    public ExtendedAd getExtendedAdById(Integer adId) {

            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

            User user = ad.getUser();

            return new ExtendedAd(
                    ad.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    ad.getDescription(),
                    user.getEmail(),
                    ad.getImage(),
                    user.getPhone(),
                    ad.getPrice(),
                    ad.getTitle()
            );
    }

    /**
     * Delete an advertisement by ID.
     *
     * @param adId Advertisement ID.
     */
    @Transactional
    public void deleteAd(Integer adId) {

            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

            adRepository.deleteById(adId);
    }

    /**
     * Update an advertisement by ID.
     *
     * @param adId             Advertisement ID.
     * @param createOrUpdateAd Updated advertisement data.
     * @return Updated advertisement data.
     */
    @Transactional
    public AdDTO updateAd(Integer adId, CreateOrUpdateAd createOrUpdateAd) {

            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

            ad.setTitle(createOrUpdateAd.getTitle());
            ad.setPrice(createOrUpdateAd.getPrice());
            ad.setDescription(createOrUpdateAd.getDescription());

            ad = adRepository.save(ad);

            return adMapper.adToAdDTO(ad);
    }

    /**
     * Get advertisements for the currently authenticated user.
     *
     * @param authentication Information about the current user's authentication.
     * @return List of advertisements for the current user.
     */
    @Transactional
    public List<AdDTO> getAdsForCurrentUser(Authentication authentication) {

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            List<Ad> ads = adRepository.findByUserId(securityUser.getId());
            return ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());
    }

    /**
     * Update the image of an advertisement by ID.
     *
     * @param adId      Advertisement ID.
     * @param imageData New image data for the advertisement.
     */
    @Transactional
    public void updateAdImage(Integer adId, MultipartFile imageData) {


            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

            if (imageData != null && !imageData.isEmpty()) {
            try {
                String imagePath = environment.getProperty("image.upload.path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    String imageName = adId + "_" + imageData.getOriginalFilename();
                    Path filePath = Paths.get(imagePath, imageName);

                    Files.write(filePath, imageData.getBytes());
                    ad.setImage(imageName);
                    adRepository.save(ad);
                    logger.info("Image for Ad ID {} has been successfully updated.", adId);
                } else {
                    logger.error("Image upload path is not configured.");
                }
            } catch (IOException e) {
                logger.error("An error occurred while processing the image: {}", e.getMessage());
            }
        } else {
            logger.warn("No image data provided for Ad ID {}. Image not updated.", adId);
        }
    }

    /**
     * Check if the authenticated user is the owner of an advertisement.
     *
     * @param authentication Information about the current user's authentication.
     * @param adId           Advertisement ID.
     * @return True if the user is the owner, false otherwise.
     */
    private boolean isAdOwner(Authentication authentication, Integer adId) {
        if (authentication != null && authentication.isAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            return adRepository.existsByIdAndUser_Id(adId, securityUser.getId());
        }
        return false;
    }

    /**
     * Check if the authenticated user has the admin role.
     *
     * @param authentication Information about the current user's authentication.
     * @return True if the user has the admin role, false otherwise.
     */
    private boolean hasAdminRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    /**
     * Get the image of an advertisement by ID.
     *
     * @param adId Advertisement ID.
     * @return Byte array representing the image.
     */
    public byte[] getAdImage(Integer adId) {

        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        String imageString = ad.getImage();
        if (imageString != null && !imageString.isEmpty()) {
            return imageString.getBytes(StandardCharsets.UTF_8);
        } else {
            return new byte[0];
        }
    }

    /**
     * Retrieves a ResponseEntity containing an advertisement's image as a byte array along with appropriate HTTP headers.
     *
     * @param adId The ID of the advertisement for which to retrieve the image.
     * @return ResponseEntity containing the image as a byte array and appropriate HTTP headers.
     */
    public ResponseEntity<byte[]> getAdImageResponse(Integer adId) {
        byte[] adImage = getAdImage(adId);

        HttpHeaders headers = new HttpHeaders();
        if (adImage.length > 0) {
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(adImage, headers, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(getDefaultImageBytes(), headers, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the default image as a byte array.
     *
     * @return The default image as a byte array.
     */
    public byte[] getDefaultImageBytes() {
        return new byte[0];
    }
}
