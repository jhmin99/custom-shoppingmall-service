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

    /**
     * creation time
     */
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp creationTime;

    /**
     * last modified time
     */
    @UpdateTimestamp
    private Timestamp lastModifiedTime;
}
