package glting.server.chat.repository;

import glting.server.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomEntity, String> {
    @Query("""
            SELECT cr
            FROM ChatRoomEntity cr
            JOIN FETCH cr.guestEntity g
            JOIN FETCH cr.hostEntity h
            WHERE cr.hostEntity.userSeq = :hostSeq
            ORDER BY cr.createdAt DESC
            """)
    List<ChatRoomEntity> findAllByHostSeq(@Param("hostSeq") Long hostSeq);
}
