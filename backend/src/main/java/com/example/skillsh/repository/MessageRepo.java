package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    @Query("SELECT DISTINCT CASE WHEN m.sender = :username THEN m.receiver ELSE m.sender END " +
            "FROM Message m " +
            "WHERE m.sender = :username OR m.receiver = :username")
    List<String> findRecentContactsForUser(@Param("username") String username);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.id DESC")
    List<Message> findRecentChatHistory(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);

    List<Message> findBySenderAndReceiverOrReceiverAndSender(String sender1, String receiver1, String receiver2, String sender2);
}
