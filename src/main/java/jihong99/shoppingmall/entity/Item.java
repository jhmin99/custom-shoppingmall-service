package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private Integer price;

    private Integer inventory;

    private String keyword;

    @CreatedDate
    private LocalDate registrationDate;

    public void updateName(String name){ this.name = name;}
    public void updatePrice(Integer price){this.price = price;}
    public void updateInventory(Integer inventory){this.inventory = inventory;}
    public void updateKeyword(String keyword){this.keyword = keyword;}
}
