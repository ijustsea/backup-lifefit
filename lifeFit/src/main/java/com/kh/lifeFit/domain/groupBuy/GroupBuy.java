package com.kh.lifeFit.domain.groupBuy;

import com.kh.lifeFit.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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

}
