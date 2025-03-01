package org.yalli.wah.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yalli.wah.model.dto.MentorDetailDto;
import org.yalli.wah.model.dto.MentorSearchRequest;
import org.yalli.wah.model.dto.MentorSearchDto;
import org.yalli.wah.model.dto.MentorshipDto;
import org.yalli.wah.model.enums.MentorStatus;
import org.yalli.wah.model.exception.InvalidInputException;
import org.yalli.wah.service.MentorService;
import org.yalli.wah.service.PermissionService;

import java.io.IOException;

import static org.yalli.wah.controller.EventController.removeCountryOfCity;


@RestController
@RequestMapping("/v1/mentors")
@CrossOrigin
@RequiredArgsConstructor
public class MentorController {
    private final MentorService mentorService;
    private final PermissionService permissionService;

    @GetMapping("/search")
    @Operation(summary = "search mentors")
    @ResponseStatus(HttpStatus.OK)
    public Page<MentorSearchDto> search(@ModelAttribute MentorSearchRequest mentorSearchRequest, Pageable pageable) throws IOException, InterruptedException {
        if (mentorSearchRequest.getCity() != null && !mentorSearchRequest.getCity().isEmpty()) {

            removeCountryOfCity(mentorSearchRequest);
        }
        return mentorService.searchMentors(mentorSearchRequest, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "get mentor with its info and comments")
    public MentorDetailDto getMentor(@PathVariable Long id) {
        return mentorService.getMentorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "apply to mentorship")
    public void applyMentorShip(@RequestBody MentorshipDto mentorshipDto) {
        mentorService.applyToMentorship(mentorshipDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "change request status")
    public void changeRequestStatus(@PathVariable Long id, @RequestParam MentorStatus operation,
                                    @RequestHeader("user-id") Long userId) {
        if (permissionService.hasPermission(userId, "mentorshipApply")) {
            switch (operation) {
                case REVIEW -> mentorService.assignRequest(id);
                case REJECTED -> mentorService.rejectMentorship(id);
                case ACCEPTED -> mentorService.acceptMentorship(id);
                default -> throw new InvalidInputException("Unexpected value: " + operation);
            }
        }
    }
}

