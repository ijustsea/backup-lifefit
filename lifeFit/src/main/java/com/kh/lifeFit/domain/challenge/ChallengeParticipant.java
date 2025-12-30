package com.kh.lifeFit.domain.challenge;

import com.kh.lifeFit.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
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
    @Column(name = "challenge_participant_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime appliedDate;

    public static ChallengeParticipant create(User user, Challenge challenge) {
        if (user == null || challenge == null) {
            throw new IllegalArgumentException("user and challenge must not be null");
        }
        ChallengeParticipant participant = new ChallengeParticipant();
        participant.user = user;
        participant.challenge = challenge;
        return participant;
    }

}
