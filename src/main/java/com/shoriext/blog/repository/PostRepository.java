package com.shoriext.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shoriext.blog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Найдем все посты, отсортированные по дате создания (от новых к старым)
    List<Post> findAllByOrderByCreatedAtDesc();

    // Метод с пагинацией
    Page<Post> findAll(Pageable pageable);

    // Метод с пагинацией и сортировкой по дате (новые сначала)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByUser_UsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content,
            Pageable pageable);

}
