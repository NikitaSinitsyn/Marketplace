package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Ad} entities and {@link AdDTO} data transfer objects.
 */
@Mapper(componentModel = "spring", uses = {CommentMapper.class, UserMapper.class})
public interface AdMapper {

    /**
     * Converts an {@link Ad} entity to an {@link AdDTO} data transfer object.
     *
     * @param ad The {@link Ad} entity to be converted.
     * @return The corresponding {@link AdDTO}.
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user.id", target = "author")
    AdDTO adToAdDTO(Ad ad);

    /**
     * Converts an {@link AdDTO} data transfer object to an {@link Ad} entity.
     *
     * @param adDTO The {@link AdDTO} to be converted.
     * @return The corresponding {@link Ad} entity.
     */
    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user.id")
    Ad adDTOToAd(AdDTO adDTO);


}