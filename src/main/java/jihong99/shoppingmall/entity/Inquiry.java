package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.InquiryType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * Item related to the inquiry, if applicable.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Parent inquiry if this is a response to another inquiry.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_inquiry_id")
    private Inquiry parent;

    /**
     * Type of the inquiry.
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
    @Column(name = "is_resolved")
    private boolean isResolved;

    /**
     * Indicates if this inquiry is a question.
     */
    @Column(name = "is_question")
    private boolean isQuestion;

    /**
     * Updates the title of the inquiry.
     *
     * @param title new title of the inquiry
     */
    public void updateTitle(String title){
        this.title = title;
    }

    /**
     * Updates the content of the inquiry.
     *
     * @param content new content of the inquiry
     */
    public void updateContent(String content){
        this.content = content;
    }

    /**
     * Updates the resolved status of the inquiry.
     *
     * @param isResolved new resolved status of the inquiry
     */
    public void updateIsResolved(boolean isResolved){
        this.isResolved = isResolved;
    }

    /**
     * Creates a new item-related inquiry.
     *
     * @param users the user making the inquiry
     * @param item the item related to the inquiry
     * @param title the title of the inquiry
     * @param content the content of the inquiry
     * @return a new Inquiry instance
     */
    public static Inquiry createItemInquiry(Users users, Item item, String title, String content){
        return Inquiry.builder()
                .users(users)
                .item(item)
                .title(title)
                .content(content)
                .type(InquiryType.ITEM)
                .isQuestion(true)
                .isResolved(false)
                .build();
    }

    /**
     * Creates a new customer service inquiry.
     *
     * @param users the user making the inquiry
     * @param item the item related to the inquiry
     * @param title the title of the inquiry
     * @param content the content of the inquiry
     * @return a new Inquiry instance
     */
    public static Inquiry createCustomerInquiry(Users users, Item item, String title, String content){
        return Inquiry.builder()
                .users(users)
                .item(item)
                .title(title)
                .content(content)
                .type(InquiryType.CUSTOMER)
                .isQuestion(true)
                .isResolved(false)
                .build();
    }

    /**
     * Adds a response to an existing inquiry.
     *
     * @param parent the parent inquiry to which this is a response
     * @param title the title of the response
     * @param content the content of the response
     * @return a new Inquiry instance representing the response
     */
    public static Inquiry addResponse(Inquiry parent, String title, String content){
        return Inquiry.builder()
                .parent(parent)
                .title(title)
                .content(content)
                .isQuestion(false)
                .isResolved(true)
                .build();
    }
}
