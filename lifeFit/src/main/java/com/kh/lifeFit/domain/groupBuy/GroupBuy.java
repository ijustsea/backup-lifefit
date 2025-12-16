package com.kh.lifeFit.domain.groupBuy;

import com.kh.lifeFit.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "group_buy",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_group_buy_user_info",
                        columnNames = {"user_id", "group_buy_info_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class GroupBuy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="group_buy_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_buy_info_id", nullable = false)
    private GroupBuyInfo groupBuyInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupBuyStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public GroupBuy(User user, GroupBuyInfo groupBuyInfo, GroupBuyStatus status) {
        this.user = user;
        this.groupBuyInfo = groupBuyInfo;
        this.status = status;
    }

    public void changeStatus(GroupBuyStatus status) {
        this.status = status;
    }
}
