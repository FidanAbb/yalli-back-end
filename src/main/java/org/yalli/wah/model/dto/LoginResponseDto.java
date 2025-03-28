package org.yalli.wah.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDto {
    private String accessToken;
    private String fullName;
    private String country;
    private String image;
    private Boolean isGoogleLogin;
    private Long id;
}
