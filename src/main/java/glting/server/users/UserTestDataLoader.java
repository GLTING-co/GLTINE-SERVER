package glting.server.users;

import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserJpaRepository;
import glting.server.users.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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

        users.add(UserEntity.builder().name("김도현").birth("1995-09-12").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000001L").deleted(false).build());
        users.add(UserEntity.builder().name("이서준").birth("1996-03-21").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000002L").deleted(false).build());
        users.add(UserEntity.builder().name("박지훈").birth("1994-12-02").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000003L").deleted(false).build());
        users.add(UserEntity.builder().name("최현우").birth("1997-07-18").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000004L").deleted(false).build());
        users.add(UserEntity.builder().name("정민재").birth("1998-11-25").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000005L").deleted(false).build());
        users.add(UserEntity.builder().name("한지호").birth("1993-08-10").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000006L").deleted(false).build());
        users.add(UserEntity.builder().name("서민석").birth("1992-05-09").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000007L").deleted(false).build());
        users.add(UserEntity.builder().name("장우진").birth("1995-03-13").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000008L").deleted(false).build());
        users.add(UserEntity.builder().name("강현우").birth("1996-09-22").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000009L").deleted(false).build());
        users.add(UserEntity.builder().name("윤태현").birth("1997-11-01").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000010L").deleted(false).build());
        users.add(UserEntity.builder().name("임재현").birth("1994-02-14").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000011L").deleted(false).build());
        users.add(UserEntity.builder().name("배성민").birth("1993-07-25").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000012L").deleted(false).build());
        users.add(UserEntity.builder().name("오지훈").birth("1995-12-19").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000013L").deleted(false).build());
        users.add(UserEntity.builder().name("홍유진").birth("1998-10-03").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000014L").deleted(false).build());
        users.add(UserEntity.builder().name("서진우").birth("1997-04-06").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000015L").deleted(false).build());
        users.add(UserEntity.builder().name("김영훈").birth("1996-08-08").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000016L").deleted(false).build());
        users.add(UserEntity.builder().name("이승현").birth("1995-01-27").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000017L").deleted(false).build());
        users.add(UserEntity.builder().name("박현서").birth("1999-06-02").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000018L").deleted(false).build());
        users.add(UserEntity.builder().name("최민호").birth("1998-03-28").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000019L").deleted(false).build());
        users.add(UserEntity.builder().name("정우성").birth("1997-10-10").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000020L").deleted(false).build());
        users.add(UserEntity.builder().name("김준호").birth("1996-02-11").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000021L").deleted(false).build());
        users.add(UserEntity.builder().name("이현수").birth("1994-09-14").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000022L").deleted(false).build());
        users.add(UserEntity.builder().name("박도윤").birth("1995-05-17").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000023L").deleted(false).build());
        users.add(UserEntity.builder().name("최서준").birth("1993-01-09").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000024L").deleted(false).build());
        users.add(UserEntity.builder().name("정태민").birth("1997-06-20").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000025L").deleted(false).build());
        users.add(UserEntity.builder().name("한지성").birth("1998-02-13").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000026L").deleted(false).build());
        users.add(UserEntity.builder().name("서도현").birth("1996-12-23").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000027L").deleted(false).build());
        users.add(UserEntity.builder().name("장태훈").birth("1995-07-11").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000028L").deleted(false).build());
        users.add(UserEntity.builder().name("강성민").birth("1994-03-30").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000029L").deleted(false).build());
        users.add(UserEntity.builder().name("윤민수").birth("1993-11-05").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000030L").deleted(false).build());
        users.add(UserEntity.builder().name("임지훈").birth("1996-06-16").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000031L").deleted(false).build());
        users.add(UserEntity.builder().name("배정훈").birth("1998-09-08").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000032L").deleted(false).build());
        users.add(UserEntity.builder().name("오태양").birth("1997-05-25").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000033L").deleted(false).build());
        users.add(UserEntity.builder().name("홍승현").birth("1999-03-07").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000034L").deleted(false).build());
        users.add(UserEntity.builder().name("서민우").birth("1998-01-19").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000035L").deleted(false).build());
        users.add(UserEntity.builder().name("김도윤").birth("1996-08-29").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000036L").deleted(false).build());
        users.add(UserEntity.builder().name("이태민").birth("1995-04-12").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000037L").deleted(false).build());
        users.add(UserEntity.builder().name("박지성").birth("1997-02-03").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000038L").deleted(false).build());
        users.add(UserEntity.builder().name("최수현").birth("1999-09-17").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000039L").deleted(false).build());
        users.add(UserEntity.builder().name("정재호").birth("1994-11-21").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000040L").deleted(false).build());
        users.add(UserEntity.builder().name("한승우").birth("1993-10-04").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000041L").deleted(false).build());
        users.add(UserEntity.builder().name("서준호").birth("1996-12-14").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000042L").deleted(false).build());
        users.add(UserEntity.builder().name("장민재").birth("1998-06-26").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000043L").deleted(false).build());
        users.add(UserEntity.builder().name("강우진").birth("1997-07-05").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000044L").deleted(false).build());
        users.add(UserEntity.builder().name("윤현우").birth("1994-05-28").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000045L").deleted(false).build());
        users.add(UserEntity.builder().name("임태훈").birth("1995-08-18").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000046L").deleted(false).build());
        users.add(UserEntity.builder().name("배민수").birth("1999-02-10").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000047L").deleted(false).build());
        users.add(UserEntity.builder().name("오정현").birth("1993-09-09").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000048L").deleted(false).build());
        users.add(UserEntity.builder().name("홍지훈").birth("1996-01-03").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000049L").deleted(false).build());
        users.add(UserEntity.builder().name("서재민").birth("1998-04-15").gender("MALE").sexualType("GAY").relationship("SINGLE").kakaoId("100000050L").deleted(false).build());


        users.add(UserEntity.builder().name("김지은").birth("1996-03-14").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000001L").deleted(false).build());
        users.add(UserEntity.builder().name("이서연").birth("1997-05-22").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000002L").deleted(false).build());
        users.add(UserEntity.builder().name("박민지").birth("1995-10-11").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000003L").deleted(false).build());
        users.add(UserEntity.builder().name("최유진").birth("1998-02-28").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000004L").deleted(false).build());
        users.add(UserEntity.builder().name("정예은").birth("1999-07-07").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000005L").deleted(false).build());
        users.add(UserEntity.builder().name("한서현").birth("1996-11-19").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000006L").deleted(false).build());
        users.add(UserEntity.builder().name("서민정").birth("1995-09-03").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000007L").deleted(false).build());
        users.add(UserEntity.builder().name("장지수").birth("1997-01-16").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000008L").deleted(false).build());
        users.add(UserEntity.builder().name("강유나").birth("1998-04-12").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000009L").deleted(false).build());
        users.add(UserEntity.builder().name("윤소희").birth("1999-12-20").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000010L").deleted(false).build());
        users.add(UserEntity.builder().name("임하은").birth("1995-06-09").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000011L").deleted(false).build());
        users.add(UserEntity.builder().name("배지연").birth("1996-02-07").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000012L").deleted(false).build());
        users.add(UserEntity.builder().name("오유리").birth("1997-08-18").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000013L").deleted(false).build());
        users.add(UserEntity.builder().name("홍지민").birth("1999-03-23").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000014L").deleted(false).build());
        users.add(UserEntity.builder().name("서은지").birth("1998-05-01").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000015L").deleted(false).build());
        users.add(UserEntity.builder().name("김하린").birth("1996-09-29").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000016L").deleted(false).build());
        users.add(UserEntity.builder().name("이소연").birth("1995-12-15").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000017L").deleted(false).build());
        users.add(UserEntity.builder().name("박수진").birth("1997-07-04").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000018L").deleted(false).build());
        users.add(UserEntity.builder().name("최지은").birth("1999-01-08").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000019L").deleted(false).build());
        users.add(UserEntity.builder().name("정하윤").birth("1998-06-14").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000020L").deleted(false).build());
        users.add(UserEntity.builder().name("한지수").birth("1996-10-27").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000021L").deleted(false).build());
        users.add(UserEntity.builder().name("서다인").birth("1995-02-05").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000022L").deleted(false).build());
        users.add(UserEntity.builder().name("장유정").birth("1999-09-13").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000023L").deleted(false).build());
        users.add(UserEntity.builder().name("강지현").birth("1998-03-11").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000024L").deleted(false).build());
        users.add(UserEntity.builder().name("윤혜원").birth("1997-05-28").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000025L").deleted(false).build());
        users.add(UserEntity.builder().name("임수아").birth("1996-01-20").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000026L").deleted(false).build());
        users.add(UserEntity.builder().name("배은서").birth("1995-08-30").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000027L").deleted(false).build());
        users.add(UserEntity.builder().name("오다혜").birth("1999-02-24").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000028L").deleted(false).build());
        users.add(UserEntity.builder().name("홍지수").birth("1998-11-02").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000029L").deleted(false).build());
        users.add(UserEntity.builder().name("서유리").birth("1997-07-09").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000030L").deleted(false).build());
        users.add(UserEntity.builder().name("김소민").birth("1996-04-25").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000031L").deleted(false).build());
        users.add(UserEntity.builder().name("이하윤").birth("1995-10-18").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000032L").deleted(false).build());
        users.add(UserEntity.builder().name("박유나").birth("1999-01-05").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000033L").deleted(false).build());
        users.add(UserEntity.builder().name("최지혜").birth("1998-03-15").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000034L").deleted(false).build());
        users.add(UserEntity.builder().name("정서윤").birth("1997-09-22").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000035L").deleted(false).build());
        users.add(UserEntity.builder().name("한민정").birth("1996-06-03").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000036L").deleted(false).build());
        users.add(UserEntity.builder().name("서혜림").birth("1995-11-14").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000037L").deleted(false).build());
        users.add(UserEntity.builder().name("장하연").birth("1998-02-10").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000038L").deleted(false).build());
        users.add(UserEntity.builder().name("강하늘").birth("1997-05-07").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000039L").deleted(false).build());
        users.add(UserEntity.builder().name("윤지혜").birth("1999-08-17").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000040L").deleted(false).build());
        users.add(UserEntity.builder().name("임다연").birth("1996-12-22").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000041L").deleted(false).build());
        users.add(UserEntity.builder().name("배소연").birth("1995-01-26").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000042L").deleted(false).build());
        users.add(UserEntity.builder().name("오하늘").birth("1998-09-05").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000043L").deleted(false).build());
        users.add(UserEntity.builder().name("홍수빈").birth("1997-11-29").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000044L").deleted(false).build());
        users.add(UserEntity.builder().name("서유정").birth("1996-07-02").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000045L").deleted(false).build());
        users.add(UserEntity.builder().name("김다인").birth("1995-05-20").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000046L").deleted(false).build());
        users.add(UserEntity.builder().name("이민서").birth("1999-02-02").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000047L").deleted(false).build());
        users.add(UserEntity.builder().name("박하영").birth("1998-06-11").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000048L").deleted(false).build());
        users.add(UserEntity.builder().name("최유나").birth("1997-03-09").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000049L").deleted(false).build());
        users.add(UserEntity.builder().name("정민서").birth("1996-09-27").gender("FEMALE").sexualType("LESBIAN").relationship("SINGLE").kakaoId("200000050L").deleted(false).build());

        userJpaRepository.saveAll(users);
    }
}
