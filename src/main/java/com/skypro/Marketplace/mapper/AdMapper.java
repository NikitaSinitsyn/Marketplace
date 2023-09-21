package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, UserMapper.class})
public interface AdMapper {
    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user", target = "author", qualifiedByName = "mapUserToInteger")
    @Mapping(source = "image", target = "image", qualifiedByName = "mapImage")
    @Mapping(source = "comments", target = "comments")
    AdDTO adToAdDTO(Ad ad);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user", qualifiedByName = "mapIntegerToUser")
    @Mapping(source = "image", target = "image", qualifiedByName = "mapImage")
    @Mapping(source = "comments", target = "comments")
    Ad adDTOToAd(AdDTO adDTO);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user", target = "author", qualifiedByName = "mapUserToInteger")
    @Mapping(source = "image", target = "image", qualifiedByName = "mapImage")
    @Mapping(source = "comments", target = "comments")
    List<AdDTO> adsToAdDTOs(List<Ad> ads);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user", qualifiedByName = "mapIntegerToUser")
    @Mapping(source = "image", target = "image", qualifiedByName = "mapImage")
    @Mapping(source = "comments", target = "comments")
    List<Ad> adDTOsToAds(List<AdDTO> adDTOs);

    @Named("mapImage")
    default String map(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    @Named("mapImage")
    default byte[] map(String value) {
        return Base64.getDecoder().decode(value);
    }

    @Named("mapUserToInteger")
    default Integer mapUserToInteger(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("mapIntegerToUser")
    default User mapIntegerToUser(Integer userId) {
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            return user;
        }
        return null;
    }
}