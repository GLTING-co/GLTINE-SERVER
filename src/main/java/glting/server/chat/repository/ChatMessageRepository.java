package glting.server.chat.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.chat.entity.ChatMessageEntity;
import glting.server.chat.entity.QChatMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                .join(chatMessage.chatRoomEntity).fetchJoin()
                .join(chatMessage.senderEntity).fetchJoin()
                .join(chatMessage.receiverEntity).fetchJoin()
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

    /**
     * 여러 채팅방의 가장 최근 메시지를 일괄 조회합니다.
     *
     * @param chatRoomSeqs 채팅방 고유 식별자 목록
     * @return 채팅방 SEQ를 키로 하는 최근 메시지 Map (메시지가 없으면 포함되지 않음)
     */
    public Map<String, String> findRecentMessagesByChatRoomSeqs(List<String> chatRoomSeqs) {
        if (chatRoomSeqs.isEmpty()) {
            return Map.of();
        }
        
        QChatMessageEntity chatMessage = QChatMessageEntity.chatMessageEntity;
        
        // 각 채팅방별 최근 메시지를 한 번에 조회
        List<ChatMessageEntity> recentMessages = queryFactory
                .selectFrom(chatMessage)
                .where(chatMessage.chatRoomEntity.chatRoomSeq.in(chatRoomSeqs)
                        .and(chatMessage.deleted.eq(false)))
                .orderBy(
                        chatMessage.chatRoomEntity.chatRoomSeq.asc(),
                        chatMessage.createdAt.desc()
                )
                .fetch();
        
        // 각 채팅방별로 첫 번째(가장 최근) 메시지만 Map에 저장
        Map<String, String> messageMap = new java.util.HashMap<>();
        String currentChatRoomSeq = null;
        
        for (ChatMessageEntity message : recentMessages) {
            String chatRoomSeq = message.getChatRoomEntity().getChatRoomSeq();
            if (!chatRoomSeq.equals(currentChatRoomSeq)) {
                messageMap.put(chatRoomSeq, message.getMessage());
                currentChatRoomSeq = chatRoomSeq;
            }
        }
        
        return messageMap;
    }

    /**
     * 채팅방의 가장 최근 메시지 엔티티를 조회합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @return 가장 최근 메시지 엔티티 (메시지가 없으면 null)
     */
    public Optional<ChatMessageEntity> findRecentMessageEntityByChatRoomSeq(String chatRoomSeq) {
        QChatMessageEntity chatMessage = QChatMessageEntity.chatMessageEntity;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(chatMessage)
                        .where(chatMessage.chatRoomEntity.chatRoomSeq.eq(chatRoomSeq)
                                .and(chatMessage.deleted.eq(false)))
                        .orderBy(chatMessage.createdAt.desc())
                        .limit(1)
                        .fetchFirst()
        );
    }

    /**
     * 메시지 고유 식별자로 채팅 메시지 엔티티를 조회합니다.
     *
     * @param messageSeq 메시지 고유 식별자(PK)
     * @return 채팅 메시지 엔티티 (메시지가 없으면 null)
     */
    public Optional<ChatMessageEntity> findByMessageSeq(String messageSeq) {
        return chatMessageJpaRepository.findById(messageSeq);
    }

    /**
     * 채팅방에서 특정 메시지 이후의 안 읽은 메시지 개수를 조회합니다.
     *
     * @param chatRoomSeq 채팅방 고유 식별자(PK)
     * @param lastReadMessageSeq 마지막으로 읽은 메시지 고유 식별자 (null이면 모든 메시지)
     * @param receiverSeq 수신자 고유 식별자(PK)
     * @return 안 읽은 메시지 개수
     */
    public Long countUnreadMessages(String chatRoomSeq, String lastReadMessageSeq, Long receiverSeq) {
        QChatMessageEntity chatMessage = QChatMessageEntity.chatMessageEntity;
        QChatMessageEntity lastReadMessage = QChatMessageEntity.chatMessageEntity;

        var whereCondition = chatMessage.chatRoomEntity.chatRoomSeq.eq(chatRoomSeq)
                .and(chatMessage.receiverEntity.userSeq.eq(receiverSeq))
                .and(chatMessage.deleted.eq(false));

        // 마지막 읽은 메시지가 있으면 그 이후의 메시지만 카운트 (서브쿼리로 최적화)
        if (lastReadMessageSeq != null) {
            whereCondition = whereCondition.and(
                    chatMessage.createdAt.gt(
                            JPAExpressions
                                    .select(lastReadMessage.createdAt)
                                    .from(lastReadMessage)
                                    .where(lastReadMessage.messageSeq.eq(lastReadMessageSeq))
                    )
            );
        }

        Long count = queryFactory
                .select(chatMessage.count())
                .from(chatMessage)
                .where(whereCondition)
                .fetchOne();

        return count != null ? count : 0L;
    }
}
