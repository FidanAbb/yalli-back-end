package org.yalli.wah.mapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.yalli.wah.dao.entity.CommentEntity;
import org.yalli.wah.dao.entity.MentorEntity;
import org.yalli.wah.dao.entity.UserEntity;
import org.yalli.wah.dao.repository.CommentRepository;
import org.yalli.wah.dao.repository.MentorRepository;
import org.yalli.wah.dao.repository.UserRepository;
import org.yalli.wah.model.dto.CommentDto;
import org.yalli.wah.model.dto.MentorDetailDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapperService {
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final CommentRepository commentRepository;

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("USER ENTITY NOT FOUND"));
    }


    public MentorEntity findMentorById(Long id) {
        return mentorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("MENTOR ENTITY NOT FOUND"));
    }
    //commentRepository.findAllByMentorId(id).size()
//        Pageable pageable = PageRequest.of(number, size);
//        Page<List<CommentDto>>  commentEntities= new PageImpl<>(commentRepository.findAllByMentorId(id, pageable).stream().map(x->commentMapper.mapCommentEntityToCommentDto(x.stream().findAny().get())).collect(Collectors.toList()));
//        new PageImpl<>(commentRepository.findAllByMentorId(Math.toIntExact(id)), pageable, commentRepository.findAllByMentorId(Math.toIntExact(id)).size() );
//        MentorEntity mentorEntity =  mentorRepository.findById(id).orElseThrow();
//         MentorDetailDto mentorDetailDto = mentorMapper.mapMentorToMentorDetailDto(mentorEntity);
//         mentorDetailDto.setComments( new PageImpl<>(commentRepository.findAllByMentorId(Math.toIntExact(id)), pageable, commentRepository.findAllByMentorId(Math.toIntExact(id)).size() ););
//        System.out.println(mentorRepository.findAllByIdGroupByComments(id));
    //return mentorEntities.map(MentorMapper.INSTANCE::mapMentorToMentorDetailDto);
//    return commentRepository.findAllByMentorId(id);;
//
    public Page<CommentDto> ToCommentDtoPage(List<CommentDto> commentDtoList, Pageable pageable) {
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), commentDtoList.size());
        return new PageImpl<>(commentDtoList.subList(start, end), pageable, commentDtoList.size());
    }
}
