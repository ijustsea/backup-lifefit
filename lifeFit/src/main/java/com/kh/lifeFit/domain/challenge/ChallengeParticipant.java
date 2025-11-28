package com.kh.lifeFit.domain.challenge;

import com.kh.lifeFit.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "challenge_participant",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_challenge_user",
                        columnNames = {"challenge_id", "user_id"}
                )
        }
)
public class ChallengeParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private  Challenge challenge;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime appliedDate;

}
