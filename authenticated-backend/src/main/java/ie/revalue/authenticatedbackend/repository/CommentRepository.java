package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
