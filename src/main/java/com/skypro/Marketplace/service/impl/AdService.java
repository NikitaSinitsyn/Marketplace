package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.Ads;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.mapper.AdMapper;
import com.skypro.Marketplace.repository.AdRepository;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
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

    @Value("${image.upload.path}")
    private String imagePath;

    public AdService(AdRepository adRepository, AdMapper adMapper, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;
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
    public ExtendedAd getExtendedAdById(Integer adId, Authentication authentication) {

        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        if (!isAdOwner(authentication, adId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }
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
    public void deleteAd(Integer adId, Authentication authentication) {

        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
        if (!isAdOwner(authentication, adId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }
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
    public AdDTO updateAd(Integer adId, CreateOrUpdateAd createOrUpdateAd, Authentication authentication) {

        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        if (!isAdOwner(authentication, adId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }

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
    public void updateAdImage(Integer adId, MultipartFile imageData, Authentication authentication) {
        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
        if (!isAdOwner(authentication, adId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }

        if (imageData != null && !imageData.isEmpty()) {
            try {
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
            return null;
        }
    }


    /**
     * Returns a byte array representing the default image.
     *
     * @return A byte array containing image data or {@code null} if no image is available.
     */
    public byte[] getDefaultImageBytes() {
        return null;
    }
}
