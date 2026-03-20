package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.Block;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Block b WHERE b.blockerUsername = :blocker AND b.blockedUsername = :blocked")
    void forceUnblockUser(@Param("blocker") String blocker, @Param("blocked") String blocked);
    /**
     * Finds a block entry by the blocker and blocked usernames.
     *
     * @param blockerUsername The username of the user who initiated the block.
     * @param blockedUsername The username of the user who is blocked.
     * @return An Optional containing the Block if found, or empty otherwise.
     */
    Optional<Block> findByBlockerUsernameAndBlockedUsername(String blockerUsername, String blockedUsername);
@Transactional
    boolean existsByBlockerUsernameAndBlockedUsername(String blockerUsername, String blockedUsername);
@Transactional
    void deleteByBlockerUsernameAndBlockedUsername(String blockerUsername, String blockedUsername);
    /**
     * Finds all users blocked by a specific user.
     *
     * @param blockerUsername The username of the user who initiated the blocks.
     * @return A list of Block entities where 'blockerUsername' is the blocker.
     */
    List<Block> findByBlockerUsername(String blockerUsername);

    /**
     * Finds all users who have blocked a specific user.
     *
     * @param blockedUsername The username of the user who is blocked.
     * @return A list of Block entities where 'blockedUsername' is the blocked user.
     */
    List<Block> findByBlockedUsername(String blockedUsername);

}
