package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, UserMapper.class})
public interface AdMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user", target = "author", qualifiedByName = "mapUserToInteger")

    AdDTO adToAdDTO(Ad ad);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user", qualifiedByName = "mapIntegerToUser")

    Ad adDTOToAd(AdDTO adDTO);

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