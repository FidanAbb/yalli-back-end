package org.yalli.wah.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.yalli.wah.model.dto.ExperienceDto;
import org.yalli.wah.model.dto.ExperiencePostDto;
import org.yalli.wah.model.dto.ExperiencesSearchDto;
import org.yalli.wah.service.ExperiencesService;
import org.yalli.wah.util.TranslateUtil;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;

import static org.yalli.wah.controller.EventController.removeCountryOfCity;

@RestController
@RequestMapping("v1/experience")

public class ExperiencesController {

    private final ExperiencesService experiencesService;

    public ExperiencesController(ExperiencesService experiencesService) {
        this.experiencesService = experiencesService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postExperience(@RequestBody ExperiencePostDto experiencePostDto, Authentication authentication) {
        experiencesService.postExperience(experiencePostDto, authentication);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ExperienceDto> getExperiences(Pageable pageable, @ModelAttribute ExperiencesSearchDto experiencesSearchDto) throws IOException, InterruptedException {
        if (experiencesSearchDto.getCity() != null && !experiencesSearchDto.getCity().isEmpty())
            removeCountryOfCity(experiencesSearchDto);

        return experiencesService.getExperiences(pageable, experiencesSearchDto);
    }

    @GetMapping("/{link}")
    @ResponseStatus(HttpStatus.OK)
    public ExperienceDto getExperience(@PathVariable String link) {
        return experiencesService.getExperience(link);
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ExperienceDto> getUserExperiences() throws AuthenticationException {
        return experiencesService.getUserExperience();
    }

    @DeleteMapping("/{link}")
    public void deleteExperience(@PathVariable String link) {
        experiencesService.deleteExperience(link);
    }
}
