package com.negocore.application.mapper;

import com.negocore.application.dto.UserDTO;
import com.negocore.application.dto.UserResponseDTO;
import com.negocore.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserMapper {

    User toDomain(UserDTO userDTO);

    UserResponseDTO toResponseDTO(User user);

}
