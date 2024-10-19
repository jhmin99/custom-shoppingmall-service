package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.dto.request.category.CategoryRequestDto;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a category in the shopping mall.
 *
 * <p>The Category entity stores information about a specific category,
 * including the category name.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category extends BaseEntity {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /**
     * Name of the category.
     */
    private String name;

    /**
     * Updates the name of the category.
     *
     * @param name the new name of the category
     */
    public void updateName(String name) {
        this.name = name;
    }

    public static Category of(String name) {
        return Category.builder()
                .name(name)
                .build();
    }
}
