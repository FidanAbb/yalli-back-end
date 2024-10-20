package org.yalli.wah.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yalli.wah.dao.entity.CommentEntity;
import org.yalli.wah.model.dto.CommentAddDto;

@Mapper(componentModel ="spring", uses = {MapperService.class})
public interface  CommentMapper {


    @Mapping(target = "user", source = "userId")
    @Mapping(source = "mentorId", target = "mentor")
    CommentEntity mapCommentAddDtoToComment(CommentAddDto commentAddDto);


}
