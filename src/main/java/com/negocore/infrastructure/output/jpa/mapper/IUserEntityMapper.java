package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.User;
import com.negocore.infrastructure.output.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserEntityMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);

}
