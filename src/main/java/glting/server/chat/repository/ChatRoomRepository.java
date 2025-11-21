package glting.server.chat.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.entity.QChatRoomEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 고유 식별자로 채팅방 목록을 페이징하여 조회합니다.
     * JOIN FETCH를 사용하여 userA와 userB를 함께 로드합니다.
     *
     * @param userSeq  사용자 고유 식별자(PK)
     * @param pageable 페이징 정보
     * @return 채팅방 엔티티 페이지 (생성일시 내림차순 정렬)
     */
    public Page<ChatRoomEntity> findAllByUserSeq(Long userSeq, Pageable pageable) {
        QChatRoomEntity chatRoom = QChatRoomEntity.chatRoomEntity;

        BooleanExpression condition = (chatRoom.userA.userSeq.eq(userSeq)
                .or(chatRoom.userB.userSeq.eq(userSeq)))
                .and(chatRoom.deleted.eq(false));

        List<ChatRoomEntity> content = queryFactory
                .selectFrom(chatRoom)
                .distinct()
                .join(chatRoom.userA).fetchJoin()
                .join(chatRoom.userB).fetchJoin()
                .where(condition)
                .orderBy(chatRoom.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(chatRoom.countDistinct())
                .from(chatRoom)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 채팅방 고유 식별자로 채팅방을 조회합니다.
     * JOIN FETCH를 사용하여 guestEntity와 hostEntity를 함께 로드합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @return 채팅방 엔티티 (존재하지 않으면 Optional.empty())
     */
    public Optional<ChatRoomEntity> findByChatRoomSeq(String chatRoomSeq) {
        return chatRoomJpaRepository.findByChatRoomSeq(chatRoomSeq);
    }

    /**
     * 채팅방 엔티티를 저장합니다.
     *
     * @param chatRoomEntity 저장할 채팅방 엔티티
     * @return 저장된 채팅방 엔티티
     */
    public ChatRoomEntity save(ChatRoomEntity chatRoomEntity) {
        return chatRoomJpaRepository.save(chatRoomEntity);
    }
}
