package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jihong99.shoppingmall.entity.enums.InquiryStatus.*;
/**
 * Represents an inquiry made by a user regarding an item or general customer service.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inquiry extends BaseEntity {

    /**
     * Unique identifier for the inquiry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    /**
     * User who made the inquiry.
     * It is a foreign key referencing the Users entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * Item related to the inquiry, if applicable.
     * It is a foreign key referencing the Item entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = true)
    private Item item;

    /**
     * Type of the inquiry (e.g., ITEM, CUSTOMER).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type")
    private InquiryType type;

    /**
     * Title of the inquiry.
     */
    private String title;

    /**
     * Content of the inquiry.
     */
    private String content;

    /**
     * Indicates if the inquiry is resolved.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_status")
    private InquiryStatus status;

    /**
     * The date when the inquiry was created.
     */
    @CreatedDate
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * A list of responses associated with this inquiry.
     */
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryResponse> responses = new ArrayList<>();

    /**
     * Updates the title of the inquiry.
     *
     * @param title The new title of the inquiry.
     */
    public void updateTitle(String title){
        this.title = title;
    }

    /**
     * Updates the content of the inquiry.
     *
     * @param content The new content of the inquiry.
     */
    public void updateContent(String content){
        this.content = content;
    }

    /**
     * Updates the status of the inquiry.
     *
     * @param status The new status of the inquiry.
     */
    public void updateInquiryStatus(InquiryStatus status){
        this.status = status;
    }

    /**
     * Adds a response to this inquiry.
     *
     * @param response The response to add.
     */
    public void addResponse(InquiryResponse response) {
        this.responses.add(response);
        response.setInquiry(this);
    }

    /**
     * Creates a new item-related inquiry.
     *
     * @param users The user making the inquiry.
     * @param item The item related to the inquiry.
     * @param title The title of the inquiry.
     * @param content The content of the inquiry.
     * @return A new Inquiry instance.
     */
    public static Inquiry createItemInquiry(Users users, Item item, String title, String content){
        return Inquiry.builder()
                .users(users)
                .item(item)
                .title(title)
                .content(content)
                .type(InquiryType.ITEM)
                .status(UNRESOLVED)
                .build();
    }

    /**
     * Creates a new customer service inquiry.
     *
     * @param users The user making the inquiry.
     * @param title The title of the inquiry.
     * @param content The content of the inquiry.
     * @return A new Inquiry instance.
     */
    public static Inquiry createCustomerInquiry(Users users, String title, String content){
        return Inquiry.builder()
                .users(users)
                .item(null)
                .title(title)
                .content(content)
                .type(InquiryType.CUSTOMER)
                .status(UNRESOLVED)
                .build();
    }

}
