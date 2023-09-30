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
import com.skypro.Marketplace.exception.UnauthorizedException;
import com.skypro.Marketplace.mapper.AdMapper;
import com.skypro.Marketplace.repository.AdRepository;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(AdService.class);

    public AdService(AdRepository adRepository, AdMapper adMapper, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;

    }

    public Ads getAds() {
        List<AdDTO> adsList;
        List<Ad> ads = adRepository.findAll();
        adsList = ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());

        int count = adsList.size();
        return new Ads(count, adsList);
    }

    //@Transactional
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
                try {
                    String imageData = new String(imageFile.getBytes(), StandardCharsets.UTF_8);
                    ad.setImage(imageData);
                    adRepository.save(ad);
                } catch (IOException e) {
                    logger.error("An error occurred while processing the image: {}", e.getMessage());
                }
            }

            return adMapper.adToAdDTO(ad);

    }


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
    @Transactional
    public void deleteAd(Integer adId) {



            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

            adRepository.deleteById(adId);

    }
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

    public List<AdDTO> getAdsForCurrentUser(Authentication authentication) {

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            List<Ad> ads = adRepository.findByUserId(securityUser.getId());
            return ads.stream().map(adMapper::adToAdDTO).collect(Collectors.toList());

    }

    @Transactional
    public void updateAdImage(Integer adId, MultipartFile imageData) {


            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
        if (imageData != null && !imageData.isEmpty()) {
            try {
                String imageFile = new String(imageData.getBytes(), StandardCharsets.UTF_8);

                ad.setImage(imageFile);
                adRepository.save(ad);
                logger.info("Image for Ad ID {} has been successfully updated.", adId);
            }catch (IOException e){
                logger.error("An error occurred while processing the image: {}", e.getMessage());
            }
        } else {
            logger.warn("No image data provided for Ad ID {}. Image not updated.", adId);
        }

    }

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
}
