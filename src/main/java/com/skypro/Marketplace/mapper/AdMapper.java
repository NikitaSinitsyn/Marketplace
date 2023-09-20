package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.entity.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Base64;

@Mapper

public interface AdMapper {
    AdMapper INSTANCE = Mappers.getMapper(AdMapper.class);

    AdDTO adToAdDTO(Ad ad);

    Ad adDTOToAd(AdDTO adDTO);

    default String map(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    default byte[] map(String value) {
        return Base64.getDecoder().decode(value);
    }
}