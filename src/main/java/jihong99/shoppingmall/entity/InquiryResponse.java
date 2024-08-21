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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_response_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_response_id")
    private InquiryResponse parentResponse;

    @OneToMany(mappedBy = "parentResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryResponse> childResponses = new ArrayList<>();

    private String content;

    @CreatedDate
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    public void addChildResponse(InquiryResponse childResponse) {
        this.childResponses.add(childResponse);
        childResponse.setParentResponse(this);
        childResponse.setInquiry(this.inquiry);
    }

    public void setParentResponse(InquiryResponse parentResponse) {
        this.parentResponse = parentResponse;
    }

    public static InquiryResponse createResponse(Inquiry inquiry, String content) {
        InquiryResponse response = InquiryResponse.builder()
                .inquiry(inquiry)
                .content(content)
                .build();
        inquiry.addResponse(response);
        return response;
    }

    public static InquiryResponse createChildResponse(InquiryResponse parentResponse, String content) {
        InquiryResponse response = InquiryResponse.builder()
                .content(content)
                .build();
        parentResponse.addChildResponse(response);
        return response;
    }
}
