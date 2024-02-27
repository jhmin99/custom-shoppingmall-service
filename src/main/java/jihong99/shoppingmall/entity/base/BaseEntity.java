package jihong99.shoppingmall.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // 생성 시간
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp creationTime;

    // 마지막 업데이트 시간
    @UpdateTimestamp
    @Column(insertable = false)
    private Timestamp lastModifiedTime;
}
