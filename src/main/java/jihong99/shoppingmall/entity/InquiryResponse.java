package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InquiryResponse extends BaseEntity {

    /**
     * Primary key for the inquiry response entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_response_id")
    private Long id;

    /**
     * The inquiry that this response is associated with.
     * It is a foreign key referencing the inquiry entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    /**
     * The parent response, if this is a child response.
     * It is a foreign key referencing another inquiry response entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_response_id")
    private InquiryResponse parentResponse;

    /**
     * A list of child responses associated with this response.
     */
    @OneToMany(mappedBy = "parentResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryResponse> childResponses = new ArrayList<>();

    /**
     * The content of the inquiry response.
     */
    private String content;

    /**
     * The date when the response was created.
     */
    @CreatedDate
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * Associates this response with a specific inquiry.
     *
     * @param inquiry The inquiry to associate with.
     */
    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    /**
     * Adds a child response to this response.
     *
     * @param childResponse The child response to add.
     */
    public void addChildResponse(InquiryResponse childResponse) {
        this.childResponses.add(childResponse);
        childResponse.setParentResponse(this);
        childResponse.setInquiry(this.inquiry);
    }

    /**
     * Associates this response with a parent response.
     *
     * @param parentResponse The parent response to associate with.
     */
    public void setParentResponse(InquiryResponse parentResponse) {
        this.parentResponse = parentResponse;
    }

    /**
     * Creates a new response for a specific inquiry.
     *
     * @param inquiry The inquiry to respond to.
     * @param content The content of the response.
     * @return A new InquiryResponse instance.
     */
    public static InquiryResponse createResponse(Inquiry inquiry, String content) {
        InquiryResponse response = InquiryResponse.builder()
                .inquiry(inquiry)
                .content(content)
                .build();
        inquiry.addResponse(response);
        return response;
    }

    /**
     * Creates a new child response associated with a parent response.
     *
     * @param parentResponse The parent response.
     * @param content The content of the child response.
     * @return A new child InquiryResponse instance.
     */
    public static InquiryResponse createChildResponse(InquiryResponse parentResponse, String content) {
        InquiryResponse response = InquiryResponse.builder()
                .content(content)
                .build();
        parentResponse.addChildResponse(response);
        return response;
    }

    /**
     * Updates the content of this inquiry response.
     *
     * @param content The new content.
     */
    public void updateContent(String content){
        this.content = content;
    }
}

