package org.yalli.wah.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.yalli.wah.dao.entity.UserEntity;
import org.yalli.wah.model.dto.*;

import java.util.List;

@Mapper
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "socialMediaAccounts", source = "socialMediaLinks")
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract UserEntity mapRegisterDtoToUser(RegisterDto registerDto, @MappingTarget UserEntity userEntity);


    @Mapping(source = "profilePictureUrl", target = "profilePicture")
    @Mapping(source = "socialMediaAccounts", target = "socialMediaLinks")
    public abstract MemberDto mapUserEntityToMemberDto(UserEntity userEntity);


    //    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "fields", target = "notCompletedFields")
    public abstract MemberInfoDto mapUserEntityToMemberInfoDto(UserEntity userEntity, List<String> fields, Float completionPercent);


    public abstract UserEntity updateMember(@MappingTarget UserEntity userEntity, MemberUpdateDto memberInfoDto);

    public abstract LoginResponseDto loginResponseDto(UserEntity user);
}
