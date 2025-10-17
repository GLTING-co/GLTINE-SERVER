package glting.server.users.entity;

import glting.server.base.BaseTimeEntity;
import glting.server.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.http.HttpStatus;

import static glting.server.exception.code.ExceptionCodeMapper.ExceptionType.BAD_REQUEST;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;

@Entity
@Table(name = "USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE USER SET deleted = true WHERE user_seq = ?")
@Where(clause = "deleted = false")
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long userSeq;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "sexual_type", nullable = false)
    private String sexualType;

    @Column(name = "relationship", nullable = false)
    private String relationship;

    @Column(name = "kakao_id", nullable = true, unique = true)
    private Long kakaoId;

    @Column(name = "naver_id", nullable = true, unique = true)
    private Long naverId;

    @Column(name = "google_id", nullable = true, unique = true)
    private Long googleId;

    @Builder.Default
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    public void updateSocialId(String type, Long socialId) {
        switch (type.toUpperCase()) {
            case "GOOGLE" -> this.googleId = socialId;
            case "NAVER" -> this.naverId = socialId;
            case "KAKAO" -> this.kakaoId = socialId;
            default -> throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "type 종류가 잘못됐습니다.",
                    getCode("type 종류가 잘못됐습니다.", BAD_REQUEST)
            );
        }
    }
}
