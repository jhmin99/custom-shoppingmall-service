package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Represents a notice in the shopping mall system.
 *
 * <p>The Notice entity stores information about notices that are shown to users.
 * It includes the title and content of the notice.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notice extends BaseEntity {

    /**
     * Unique identifier for the notice.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    /**
     * Title of the notice.
     */
    private String title;

    /**
     * Content of the notice.
     */
    private String content;

    @CreatedDate
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * Creates a new notice with the provided title and content.
     *
     * @param title   The title of the notice.
     * @param content The content of the notice.
     * @return A new Notice instance.
     */
    public static Notice of(String title, String content) {
        return Notice.builder()
                .title(title)
                .content(content)
                .build();
    }

    /**
     * Updates the title of the notice.
     *
     * @param title The new title of the notice.
     */
    public void updateTitle(String title) {
        this.title = title;
    }

    /**
     * Updates the content of the notice.
     *
     * @param content The new content of the notice.
     */
    public void updateContent(String content) {
        this.content = content;
    }

}
