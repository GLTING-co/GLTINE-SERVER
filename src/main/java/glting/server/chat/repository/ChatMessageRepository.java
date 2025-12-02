package glting.server.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.chat.entity.ChatMessageEntity;
import glting.server.chat.entity.QChatMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository {
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 채팅방 고유 식별자로 채팅 메시지 목록을 페이징하여 조회합니다.
     * JOIN FETCH를 사용하여 senderEntity를 함께 로드합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @param pageable    페이징 정보
     * @return 채팅 메시지 엔티티 페이지
     */
    public Page<ChatMessageEntity> findAllByChatRoomSeq(String chatRoomSeq, Pageable pageable) {
        QChatMessageEntity chatMessage = QChatMessageEntity.chatMessageEntity;

        List<ChatMessageEntity> content = queryFactory
                .selectFrom(chatMessage)
                .join(chatMessage.senderEntity).fetchJoin()
                .where(chatMessage.chatRoomEntity.chatRoomSeq.eq(chatRoomSeq)
                        .and(chatMessage.deleted.eq(false)))
                .orderBy(chatMessage.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .where(chatMessage.chatRoomEntity.chatRoomSeq.eq(chatRoomSeq)
                        .and(chatMessage.deleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 채팅 메시지 엔티티를 저장합니다.
     *
     * @param chatMessageEntity 저장할 채팅 메시지 엔티티
     * @return 저장된 채팅 메시지 엔티티
     */
    public ChatMessageEntity save(ChatMessageEntity chatMessageEntity) {
        return chatMessageJpaRepository.save(chatMessageEntity);
    }

    /**
     * 채팅방의 가장 최근 메시지를 조회합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @return 가장 최근 메시지 내용 (메시지가 없으면 null)
     */
    public String findRecentMessageByChatRoomSeq(String chatRoomSeq) {
        return chatMessageJpaRepository.findRecentMessageByChatRoomSeq(chatRoomSeq);
    }
}
