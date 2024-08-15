package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents the relationship between a user and a notice in the shopping mall system.
 *
 * <p>The UserNotice entity stores information about the association between a user and a notice.</p>
 */
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserNotice extends BaseEntity {

    /**
     * Unique identifier for the user-notice relationship.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notice_id")
    private Long id;

    /**
     * The user associated with the notice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * The notice associated with the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public static UserNotice createUserNotice(Users user, Notice notice){
        return UserNotice.builder()
                .users(user)
                .notice(notice)
                .build();
    }

}
