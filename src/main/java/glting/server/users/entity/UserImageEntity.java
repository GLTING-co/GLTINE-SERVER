package glting.server.users.entity;

import glting.server.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "USER_IMAGE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE USER_IMAGE SET deleted = true WHERE user_image_seq = ?")
@Where(clause = "deleted = false")
public class UserImageEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_image_seq")
    private Long userImageSeq;

    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;
}
