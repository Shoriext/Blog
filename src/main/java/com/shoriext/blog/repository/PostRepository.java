package com.shoriext.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shoriext.blog.model.Post;
import com.shoriext.blog.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Найдем все посты, отсортированные по дате создания (от новых к старым)
    List<Post> findAllByOrderByCreatedAtDesc();

    // Найдем все посты конкретного автора
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    // Метод с пагинацией
    Page<Post> findAll(Pageable pageable);

    // Метод с пагинацией и сортировкой по дате (новые сначала)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);

}
