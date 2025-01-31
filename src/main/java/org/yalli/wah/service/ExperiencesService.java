package org.yalli.wah.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.yalli.wah.dao.entity.ExperiencesEntity;
import org.yalli.wah.dao.repository.ExperiencesRepository;
import org.yalli.wah.dao.repository.UserRepository;
import org.yalli.wah.mapper.ExperiencesMapper;
import org.yalli.wah.model.dto.ExperienceDto;
import org.yalli.wah.model.dto.ExperiencePostDto;
import org.yalli.wah.model.dto.ExperiencesSearchDto;
import org.yalli.wah.model.exception.ResourceNotFoundException;
import org.yalli.wah.util.TranslateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperiencesService {

    private final ExperiencesRepository experiencesRepository;
    private final UserRepository userRepository;

    public void postExperience(ExperiencePostDto experiencePostDto, Authentication authentication){
        log.info("ActionLog.postExperience.start experiencePostDto {}", experiencePostDto);
        ExperiencesEntity experiencesEntity = ExperiencesMapper.INSTANCE.toEntity(experiencePostDto, userRepository.findByEmail(authentication.getName()).get());
        String link = experiencesEntity.getContent().substring(0, 11);
        experiencesEntity.setLink(TranslateUtil.getLink(link));
        experiencesRepository.save(experiencesEntity);
        log.info("ActionLog.postExperience.end experiencePostDto {}", experiencePostDto);
    }

    public Page<ExperienceDto> getExperiences(Pageable pageable,ExperiencesSearchDto experiencesSearchDto){
        log.info("ActionLog.getExperience.start");
        Specification<ExperiencesEntity> specification = Specification.where(((root, query, criteriaBuilder) -> {


            List<Predicate> predicates = new ArrayList<>();
            if(experiencesSearchDto != null){

                List<Predicate> experiencePredicates = new ArrayList<>();
                if(experiencesSearchDto.getKeyWord() != null && !experiencesSearchDto.getKeyWord().isEmpty()){
                    predicates.add(criteriaBuilder.like(root.get("content"),
                            "%" + experiencesSearchDto.getKeyWord() + "%"));
                }

                if(experiencesSearchDto.getCountry() != null && !experiencesSearchDto.getCountry().isEmpty()){
                    experiencePredicates.add(criteriaBuilder.lower(root.get("userEntity").get("country")).in(experiencesSearchDto.getCountry().stream().map(String::toLowerCase).collect(Collectors.toList()) ));
                }

                if(experiencesSearchDto.getCity() != null && !experiencesSearchDto.getCity().isEmpty()){
                    experiencePredicates.add(criteriaBuilder.lower(root.get("userEntity").get("city")).in(experiencesSearchDto.getCity().stream().map(String::toLowerCase).collect(Collectors.toList())));
                }
                predicates.add(criteriaBuilder.or(experiencePredicates.toArray(Predicate[]::new)));

            }
            if(predicates.isEmpty()){
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        }));
        return ExperiencesMapper.INSTANCE.toExperienceDto(experiencesRepository.findAll(specification, pageable));
    }

    public ExperienceDto getExperience(String link){
        log.info("ActionLog.getExperience.start link {}", link);
        return ExperiencesMapper.INSTANCE.toDto(experiencesRepository.findByLink(link).orElseThrow(()->
                new ResourceNotFoundException("EXPERIENCE NOT FOUND FOR LINK"+ link)));
    }
}
