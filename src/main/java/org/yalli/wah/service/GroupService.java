package org.yalli.wah.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.yalli.wah.dao.entity.GroupEntity;
import org.yalli.wah.dao.repository.GroupRepository;
import org.yalli.wah.mapper.GroupMapper;
import org.yalli.wah.model.dto.GroupDto;
import org.yalli.wah.model.dto.GroupLightDto;
import org.yalli.wah.model.dto.GroupRequest;
import org.yalli.wah.model.dto.GroupSearchRequest;
import org.yalli.wah.model.dto.GroupUpdateDto;
import org.yalli.wah.model.exception.InvalidInputException;
import org.yalli.wah.model.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private static final Locale AZERBAIJANI =  Locale.forLanguageTag("az");
    public Page<GroupLightDto> getAllGroupsLight(Pageable pageable, GroupSearchRequest groupSearchRequest) {
        System.out.println(groupSearchRequest.getCountry());
        Specification<GroupEntity> specification = Specification.where((root, query, criteriaBuilder) -> {

            if (groupSearchRequest != null) {
                List<Predicate> predicates = new ArrayList<>();
                if (groupSearchRequest.getCategory() != null && !groupSearchRequest.getCategory().isEmpty()) {
                    predicates.add(root.get("category").in(groupSearchRequest.getCategory()));
                }
                if (groupSearchRequest.getTitle() != null && !groupSearchRequest.getTitle().isEmpty()) {
                    predicates.add(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                                    groupSearchRequest.getTitle().toLowerCase() + "%")
                    );
                }
                List<Predicate> groupPredicates = new ArrayList<>();

                if (groupSearchRequest.getCountry() != null && !groupSearchRequest.getCountry().isEmpty()) {
                    groupPredicates.add(criteriaBuilder.or(criteriaBuilder.lower(root.get("country"))
                                    .in(groupSearchRequest.getCountry().stream()
                                            .map(city->city.toLowerCase(AZERBAIJANI))
                                            .collect(Collectors.toList()))
                    ));
                }

                if(groupSearchRequest.getCity() != null && !groupSearchRequest.getCity().isEmpty()){
                    groupPredicates.add(criteriaBuilder.or(
                            criteriaBuilder.lower(root.get("city"))
                                    .in(groupSearchRequest.getCity().stream().map(city->city.toLowerCase(AZERBAIJANI)).collect(Collectors.toList()))
                    ));
                }
                if (!groupPredicates.isEmpty()) {
                    predicates.add(criteriaBuilder.or(groupPredicates.toArray(new Predicate[0])));
                }

                if (predicates.isEmpty()) {
                    return criteriaBuilder.conjunction();
                } else {
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            } else {
                return criteriaBuilder.conjunction();
            }
        });
        return groupRepository.findAll(specification, pageable).map(GroupMapper.INSTANCE::mapEntityToGroupLightDto);
    }

    public GroupDto getGroupById(Long id) {
        return GroupMapper.INSTANCE.mapEntityToDto(getGroupEntityById(id));
    }

    public void createGroup(GroupRequest groupDto) {
        log.info("ActionLog.createGroup.start groupDto {}", groupDto);
        groupRepository.save(GroupMapper.INSTANCE.mapDtoToEntity(groupDto));
        log.info("ActionLog.createGroup.start groupDto {}", groupDto);
    }

    public Page<GroupLightDto> getGroupsByUserId(Long userId, Pageable pageable) {
        log.info("ActionLog.getGroupsByUserId.start userId = {}", userId);
        var groups = groupRepository.findByUserEntity_Id(userId, pageable);
        log.info("ActionLog.getGroupsByUserId.end userId = {}", userId);
        return groups.map(GroupMapper.INSTANCE::mapEntityToGroupLightDto);
    }

    public GroupDto getGroupByUserId(Long groupId, Long userId) {
        log.info("ActionLog.getGroupByUserId.start groupId = {}", groupId);
        var group = groupRepository.findByIdAndUserEntity_Id(groupId, userId).orElseThrow(() -> {
            log.error("ActionLog.getGroupByUserId.error group not found with id {} and user id {}", groupId, userId);
            return new ResourceNotFoundException("GROUP_NOT_FOUND");
        });
        log.info("ActionLog.getGroupByUserId.end groupId = {}", groupId);
        return GroupMapper.INSTANCE.mapEntityToDto(group);
    }

    public void updateGroup(Long id, GroupUpdateDto groupUpdateDto) {
        log.info("ActionLog.updateGroup.start groupId = {}", id);
        var group = getGroupEntityById(id);
        log.info("ActionLog.updateGroup.info images {}", group.getGallery());
        if (groupUpdateDto.getTitle() != null && !groupUpdateDto.getTitle().isBlank()) {
            if (group.getRenameCount().intValue() <= 3) {
                group.setRenameCount((short) (group.getRenameCount() + 1));
            }
            if (group.getRenameCount().intValue() > 1000) {
                throw new InvalidInputException("GROUP_RENAME_LIMIT_EXCEEDED");
            }
        }
        var updatedEntity = GroupMapper.INSTANCE.updateEntity(group, groupUpdateDto);
        groupRepository.save(updatedEntity);
        log.info("ActionLog.updateGroup.end updatedEntity {}", updatedEntity);
    }

    @Transactional
    public void deleteGroup(List<Long> groupIds, Long userId) {
        log.info("ActionLog.deleteGroup.start groupId = {} user id {}", groupIds, userId);
        groupRepository.deleteByUserEntity_IdAndIdIn(userId, groupIds);
        log.info("ActionLog.deleteGroup.start groupId = {}", groupIds);
    }

    private GroupEntity getGroupEntityById(Long id) {
        return groupRepository.findById(id).orElseThrow(() ->
        {
            log.error("ActionLog.getGroupById.error group not found with id {}", id);
            return new ResourceNotFoundException("GROUP_NOT_FOUND");
        });
    }
}
