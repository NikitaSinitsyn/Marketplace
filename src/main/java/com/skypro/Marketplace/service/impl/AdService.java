package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.ad.CreateOrUpdateAd;
import com.skypro.Marketplace.dto.ad.ExtendedAd;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.mapper.AdMapper;
import com.skypro.Marketplace.repository.AdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AdService.class);

    @Autowired
    public AdService(AdRepository adRepository, AdMapper adMapper, UserService userService) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userService = userService;
    }

    public List<AdDTO> getAllAds() {
        try {
            List<Ad> ads = adRepository.findAll();
            return ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("An error occurred while getting all ads: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve ads.", e);
        }
    }

    public AdDTO createAd(CreateOrUpdateAd createOrUpdateAd) {
        try {
            Ad ad = new Ad();
            ad.setTitle(createOrUpdateAd.getTitle());
            ad.setPrice(createOrUpdateAd.getPrice());
            ad.setDescription(createOrUpdateAd.getDescription());

            ad = adRepository.save(ad);

            return adMapper.adToAdDTO(ad);
        } catch (Exception e) {
            logger.error("An error occurred while creating ad: {}", e.getMessage());
            throw new RuntimeException("Failed to create ad.", e);
        }
    }

    public void saveAdImage(Integer adId, byte[] imageData) {
        try {
            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new IllegalArgumentException("Ad not found with id: " + adId));

            ad.setImage(imageData);

            adRepository.save(ad);
        } catch (IllegalArgumentException e) {
            logger.error("Ad not found with id: {}", adId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while saving ad image for ad id {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to save ad image.", e);
        }
    }

    public ExtendedAd getExtendedAdById(Integer adId) {
        try {
            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new IllegalArgumentException("Ad not found with id: " + adId));
            String imageAsString = Base64.getEncoder().encodeToString(ad.getImage());

            User user = ad.getUser();

            return new ExtendedAd(
                    ad.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    ad.getDescription(),
                    user.getEmail(),
                    imageAsString,
                    user.getPhone(),
                    ad.getPrice(),
                    ad.getTitle()
            );
        } catch (IllegalArgumentException e) {
            logger.error("Ad not found with id: {}", adId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while getting extended ad by id {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to retrieve extended ad.", e);
        }
    }

    public void deleteAd(Integer adId) {
        try {
            adRepository.deleteById(adId);
        } catch (Exception e) {
            logger.error("An error occurred while deleting ad with id {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to delete ad.", e);
        }
    }

    public AdDTO updateAd(Integer adId, CreateOrUpdateAd createOrUpdateAd) {
        try {
            Ad ad = adRepository.findById(adId)
                    .orElseThrow(() -> new IllegalArgumentException("Ad not found with id: " + adId));

            ad.setTitle(createOrUpdateAd.getTitle());
            ad.setPrice(createOrUpdateAd.getPrice());
            ad.setDescription(createOrUpdateAd.getDescription());

            ad = adRepository.save(ad);

            return adMapper.adToAdDTO(ad);
        } catch (IllegalArgumentException e) {
            logger.error("Ad not found with id: {}", adId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating ad with id {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to update ad.", e);
        }
    }

    public List<AdDTO> getAdsForCurrentUser(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<Ad> ads = adRepository.findByUserId(currentUser.getId());
            return ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("An error occurred while fetching ads for the current user", e);
            throw new RuntimeException("Failed to retrieve ads for the current user.", e);
        }
    }

    public AdDTO getAdById(Integer adId) {
        try {
            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new IllegalArgumentException("Ad not found with id: " + adId));
            return adMapper.adToAdDTO(ad);
        } catch (IllegalArgumentException e) {
            logger.error("Ad not found with id: {}", adId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while getting ad by id {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to retrieve ad.", e);
        }
    }

    public boolean updateAdImage(Integer adId, byte[] imageData) {
        try {
            AdDTO adDTO = getAdById(adId);
            Ad ad = adMapper.adDTOToAd(adDTO);
            ad.setImage(imageData);
            adRepository.save(ad);
            logger.info("Image for Ad ID {} has been successfully updated.", adId);
            return true;
        } catch (AdNotFoundException e) {
            logger.error("Ad not found while trying to update image for Ad ID {}: {}", adId, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("An error occurred while updating ad image for ID {}: {}", adId, e.getMessage());
            return false;
        }
    }

    public boolean isAdOwner(Authentication authentication, Integer adId) {

        User currentUser = (User) authentication.getPrincipal();


        Optional<Ad> optionalAd = adRepository.findById(adId);
        if (optionalAd.isEmpty()) {
            throw new IllegalArgumentException("Ad not found with id: " + adId);
        }

        Ad ad = optionalAd.get();
        return ad.getUser().getId().equals(currentUser.getId());
    }
}
