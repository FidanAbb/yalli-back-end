package org.yalli.wah.dao.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.yalli.wah.model.enums.MentorCategory;
import org.yalli.wah.model.enums.MentorExperienceYear;
import org.yalli.wah.model.enums.MentorStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "mentors")
@NoArgsConstructor
@AllArgsConstructor
public class MentorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private MentorCategory mentorCategory;
    @OneToMany(mappedBy = "mentor")
    private List<CommentEntity> comments;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String link;
    @Enumerated(EnumType.STRING)
    private MentorStatus status;
    private MentorExperienceYear experienceLevel;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> skills;
}
