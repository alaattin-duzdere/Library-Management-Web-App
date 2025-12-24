package com.example.library_management.comment.repository;

import com.example.library_management.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long>, QueryByExampleExecutor<Comment> {

    Page<Comment> findByBookId(Pageable pageable, Long bookId);

    Page<Comment> findByUserId(Pageable pageable,Long userId);
}
