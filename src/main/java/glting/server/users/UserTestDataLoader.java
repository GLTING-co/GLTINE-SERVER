package glting.server.users;

import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserJpaRepository;
import glting.server.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserTestDataLoader {

    private final UserJpaRepository userJpaRepository;

    @PostConstruct
    public void init() {
        if (userJpaRepository.count() > 0) return; // 이미 있으면 생략

        List<UserEntity> users = new ArrayList<>();

        // MALE (GAY)
        users.add(UserEntity.builder().name("김도현").birth(LocalDate.of(1995, 9, 12)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000001L").deleted(false).build());
        users.add(UserEntity.builder().name("이서준").birth(LocalDate.of(1996, 3, 21)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000002L").deleted(false).build());
        users.add(UserEntity.builder().name("박지훈").birth(LocalDate.of(1994, 12, 2)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000003L").deleted(false).build());
        users.add(UserEntity.builder().name("최현우").birth(LocalDate.of(1997, 7, 18)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000004L").deleted(false).build());
        users.add(UserEntity.builder().name("정민재").birth(LocalDate.of(1998, 11, 25)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000005L").deleted(false).build());
        users.add(UserEntity.builder().name("한지호").birth(LocalDate.of(1993, 8, 10)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000006L").deleted(false).build());
        users.add(UserEntity.builder().name("서민석").birth(LocalDate.of(1992, 5, 9)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000007L").deleted(false).build());
        users.add(UserEntity.builder().name("장우진").birth(LocalDate.of(1995, 3, 13)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000008L").deleted(false).build());
        users.add(UserEntity.builder().name("강현우").birth(LocalDate.of(1996, 9, 22)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000009L").deleted(false).build());
        users.add(UserEntity.builder().name("윤태현").birth(LocalDate.of(1997, 11, 1)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000010L").deleted(false).build());
        users.add(UserEntity.builder().name("임재현").birth(LocalDate.of(1994, 2, 14)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000011L").deleted(false).build());
        users.add(UserEntity.builder().name("배성민").birth(LocalDate.of(1993, 7, 25)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000012L").deleted(false).build());
        users.add(UserEntity.builder().name("오지훈").birth(LocalDate.of(1995, 12, 19)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000013L").deleted(false).build());
        users.add(UserEntity.builder().name("홍유진").birth(LocalDate.of(1998, 10, 3)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000014L").deleted(false).build());
        users.add(UserEntity.builder().name("서진우").birth(LocalDate.of(1997, 4, 6)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000015L").deleted(false).build());
        users.add(UserEntity.builder().name("김영훈").birth(LocalDate.of(1996, 8, 8)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000016L").deleted(false).build());
        users.add(UserEntity.builder().name("이승현").birth(LocalDate.of(1995, 1, 27)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000017L").deleted(false).build());
        users.add(UserEntity.builder().name("박현서").birth(LocalDate.of(1999, 6, 2)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000018L").deleted(false).build());
        users.add(UserEntity.builder().name("최민호").birth(LocalDate.of(1998, 3, 28)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000019L").deleted(false).build());
        users.add(UserEntity.builder().name("정우성").birth(LocalDate.of(1997, 10, 10)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000020L").deleted(false).build());
        users.add(UserEntity.builder().name("김준호").birth(LocalDate.of(1996, 2, 11)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000021L").deleted(false).build());
        users.add(UserEntity.builder().name("이현수").birth(LocalDate.of(1994, 9, 14)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000022L").deleted(false).build());
        users.add(UserEntity.builder().name("박도윤").birth(LocalDate.of(1995, 5, 17)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000023L").deleted(false).build());
        users.add(UserEntity.builder().name("최서준").birth(LocalDate.of(1993, 1, 9)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000024L").deleted(false).build());
        users.add(UserEntity.builder().name("정태민").birth(LocalDate.of(1997, 6, 20)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000025L").deleted(false).build());
        users.add(UserEntity.builder().name("한지성").birth(LocalDate.of(1998, 2, 13)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000026L").deleted(false).build());
        users.add(UserEntity.builder().name("서도현").birth(LocalDate.of(1996, 12, 23)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000027L").deleted(false).build());
        users.add(UserEntity.builder().name("장태훈").birth(LocalDate.of(1995, 7, 11)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000028L").deleted(false).build());
        users.add(UserEntity.builder().name("강성민").birth(LocalDate.of(1994, 3, 30)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000029L").deleted(false).build());
        users.add(UserEntity.builder().name("윤민수").birth(LocalDate.of(1993, 11, 5)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000030L").deleted(false).build());
        users.add(UserEntity.builder().name("임지훈").birth(LocalDate.of(1996, 6, 16)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000031L").deleted(false).build());
        users.add(UserEntity.builder().name("배정훈").birth(LocalDate.of(1998, 9, 8)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000032L").deleted(false).build());
        users.add(UserEntity.builder().name("오태양").birth(LocalDate.of(1997, 5, 25)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000033L").deleted(false).build());
        users.add(UserEntity.builder().name("홍승현").birth(LocalDate.of(1999, 3, 7)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000034L").deleted(false).build());
        users.add(UserEntity.builder().name("서민우").birth(LocalDate.of(1998, 1, 19)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000035L").deleted(false).build());
        users.add(UserEntity.builder().name("김도윤").birth(LocalDate.of(1996, 8, 29)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000036L").deleted(false).build());
        users.add(UserEntity.builder().name("이태민").birth(LocalDate.of(1995, 4, 12)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000037L").deleted(false).build());
        users.add(UserEntity.builder().name("박지성").birth(LocalDate.of(1997, 2, 3)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000038L").deleted(false).build());
        users.add(UserEntity.builder().name("최수현").birth(LocalDate.of(1999, 9, 17)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000039L").deleted(false).build());
        users.add(UserEntity.builder().name("정재호").birth(LocalDate.of(1994, 11, 21)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000040L").deleted(false).build());
        users.add(UserEntity.builder().name("한승우").birth(LocalDate.of(1993, 10, 4)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000041L").deleted(false).build());
        users.add(UserEntity.builder().name("서준호").birth(LocalDate.of(1996, 12, 14)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000042L").deleted(false).build());
        users.add(UserEntity.builder().name("장민재").birth(LocalDate.of(1998, 6, 26)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000043L").deleted(false).build());
        users.add(UserEntity.builder().name("강우진").birth(LocalDate.of(1997, 7, 5)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000044L").deleted(false).build());
        users.add(UserEntity.builder().name("윤현우").birth(LocalDate.of(1994, 5, 28)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000045L").deleted(false).build());
        users.add(UserEntity.builder().name("임태훈").birth(LocalDate.of(1995, 8, 18)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000046L").deleted(false).build());
        users.add(UserEntity.builder().name("배민수").birth(LocalDate.of(1999, 2, 10)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000047L").deleted(false).build());
        users.add(UserEntity.builder().name("오정현").birth(LocalDate.of(1993, 9, 9)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000048L").deleted(false).build());
        users.add(UserEntity.builder().name("홍지훈").birth(LocalDate.of(1996, 1, 3)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000049L").deleted(false).build());
        users.add(UserEntity.builder().name("서재민").birth(LocalDate.of(1998, 4, 15)).gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000050L").deleted(false).build());

        // FEMALE (LESBIAN)
        users.add(UserEntity.builder().name("김지은").birth(LocalDate.of(1996, 3, 14)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000001L").deleted(false).build());
        users.add(UserEntity.builder().name("이서연").birth(LocalDate.of(1997, 5, 22)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000002L").deleted(false).build());
        users.add(UserEntity.builder().name("박민지").birth(LocalDate.of(1995, 10, 11)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000003L").deleted(false).build());
        users.add(UserEntity.builder().name("최유진").birth(LocalDate.of(1998, 2, 28)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000004L").deleted(false).build());
        users.add(UserEntity.builder().name("정예은").birth(LocalDate.of(1999, 7, 7)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000005L").deleted(false).build());
        users.add(UserEntity.builder().name("한서현").birth(LocalDate.of(1996, 11, 19)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000006L").deleted(false).build());
        users.add(UserEntity.builder().name("서민정").birth(LocalDate.of(1995, 9, 3)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000007L").deleted(false).build());
        users.add(UserEntity.builder().name("장지수").birth(LocalDate.of(1997, 1, 16)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000008L").deleted(false).build());
        users.add(UserEntity.builder().name("강유나").birth(LocalDate.of(1998, 4, 12)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000009L").deleted(false).build());
        users.add(UserEntity.builder().name("윤소희").birth(LocalDate.of(1999, 12, 20)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000010L").deleted(false).build());
        users.add(UserEntity.builder().name("임하은").birth(LocalDate.of(1995, 6, 9)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000011L").deleted(false).build());
        users.add(UserEntity.builder().name("배지연").birth(LocalDate.of(1996, 2, 7)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000012L").deleted(false).build());
        users.add(UserEntity.builder().name("오유리").birth(LocalDate.of(1997, 8, 18)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000013L").deleted(false).build());
        users.add(UserEntity.builder().name("홍지민").birth(LocalDate.of(1999, 3, 23)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000014L").deleted(false).build());
        users.add(UserEntity.builder().name("서은지").birth(LocalDate.of(1998, 5, 1)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000015L").deleted(false).build());
        users.add(UserEntity.builder().name("김하린").birth(LocalDate.of(1996, 9, 29)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000016L").deleted(false).build());
        users.add(UserEntity.builder().name("이소연").birth(LocalDate.of(1995, 12, 15)).gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000017L").deleted(false).build());
        userJpaRepository.saveAll(users);
    }
}
