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

    // ==== 온보딩 ====
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

    // ==== 정보 업데이트 ====
    @Column(name = "bio", nullable = true, unique = false, columnDefinition = "LONGTEXT")
    private String bio;

    @Column(name = "height", nullable = true, unique = false)
    private Integer height;

    @Column(name = "job_title", nullable = true, unique = false)
    private String job;

    @Column(name = "company", nullable = true, unique = false)
    private String company;

    @Column(name = "school", nullable = true, unique = false)
    private String school;

    @Column(name = "city", nullable = true, unique = false)
    private String city;

    // ===== 라이프스타일 =====
    @Column(name = "smoking", nullable = true, unique = false)
    private String smoking; // 예: NONE, OCCASIONAL, REGULAR

    @Column(name = "drinking", nullable = true, unique = false)
    private String drinking; // 예: NONE, OCCASIONAL, REGULAR

    @Column(name = "religion", nullable = true, unique = false)
    private String religion; // 예: NONE, CHRISTIAN, CATHOLIC, BUDDHIST, ISLAM, HINDU, OTHER

    // ==== 소셜 로그인 ====
    @Column(name = "kakao_id", nullable = true, unique = true)
    private String kakaoId;

    @Column(name = "naver_id", nullable = true, unique = true)
    private String naverId;

    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId;

    // ==== TODO 추후 유료화 예정 ====
    @Column(name = "plan", nullable = true, unique = false)
    private String plan; // 예: NONE, ----

    @Builder.Default
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    public void updateSocialId(String type, String socialId) {
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

    /**
     * 사용자 정보를 업데이트합니다.
     *
     * @param bio      자기소개
     * @param height   키
     * @param job      직업
     * @param company  회사
     * @param school   학교
     * @param city     도시
     * @param smoking  흡연 여부 (NONE, OCCASIONAL, REGULAR)
     * @param drinking 음주 여부 (NONE, OCCASIONAL, REGULAR)
     * @param religion 종교 (NONE, CHRISTIAN, CATHOLIC, BUDDHIST, ISLAM, HINDU, OTHER)
     */
    public void updateUser(String bio, Integer height, String job, String company,
                           String school, String city, String smoking, String drinking, String religion) {
        this.bio = bio;
        this.height = height;
        this.job = job;
        this.company = company;
        this.school = school;
        this.city = city;
        this.smoking = smoking;
        this.drinking = drinking;
        this.religion = religion;
    }
}
