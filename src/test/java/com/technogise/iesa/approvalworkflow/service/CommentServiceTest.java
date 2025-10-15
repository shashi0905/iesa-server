package com.technogise.iesa.approvalworkflow.service;

import com.technogise.iesa.approvalworkflow.domain.Comment;
import com.technogise.iesa.approvalworkflow.dto.ApprovalWorkflowMapper;
import com.technogise.iesa.approvalworkflow.dto.CommentDto;
import com.technogise.iesa.approvalworkflow.dto.CreateCommentRequest;
import com.technogise.iesa.approvalworkflow.repository.CommentRepository;
import com.technogise.iesa.expensemanagement.domain.Expense;
import com.technogise.iesa.expensemanagement.domain.ExpenseStatus;
import com.technogise.iesa.expensemanagement.repository.ExpenseRepository;
import com.technogise.iesa.shared.exception.ResourceNotFoundException;
import com.technogise.iesa.usermanagement.domain.User;
import com.technogise.iesa.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApprovalWorkflowMapper mapper;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;
    private CreateCommentRequest createRequest;
    private UUID commentId;
    private UUID expenseId;
    private UUID userId;
    private User user;
    private Expense expense;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        expenseId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        expense = Expense.builder()
                .id(expenseId)
                .submitter(user)
                .expenseDate(LocalDate.of(2025, 10, 1))
                .vendor("Test Vendor")
                .totalAmount(new BigDecimal("1000.00"))
                .currency("USD")
                .description("Test expense")
                .status(ExpenseStatus.SUBMITTED)
                .build();

        comment = Comment.builder()
                .id(commentId)
                .expense(expense)
                .author(user)
                .content("This is a test comment")
                .isInternal(false)
                .build();

        commentDto = CommentDto.builder()
                .id(commentId)
                .expenseId(expenseId)
                .authorId(userId)
                .authorName("Test User")
                .content("This is a test comment")
                .isInternal(false)
                .createdAt(Instant.now())
                .build();

        createRequest = CreateCommentRequest.builder()
                .expenseId(expenseId)
                .content("This is a test comment")
                .isInternal(false)
                .build();
    }

    @Test
    void getCommentsForExpense_ShouldReturnAllComments() {
        // Arrange
        List<Comment> comments = Arrays.asList(comment);
        List<CommentDto> commentDtos = Arrays.asList(commentDto);

        when(commentRepository.findByExpenseIdOrderByCreatedAtDesc(expenseId)).thenReturn(comments);
        when(mapper.toCommentDtoList(comments)).thenReturn(commentDtos);

        // Act
        List<CommentDto> result = commentService.getCommentsForExpense(expenseId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("This is a test comment");
        verify(commentRepository, times(1)).findByExpenseIdOrderByCreatedAtDesc(expenseId);
        verify(mapper, times(1)).toCommentDtoList(comments);
    }

    @Test
    void getExternalCommentsForExpense_ShouldReturnOnlyExternalComments() {
        // Arrange
        List<Comment> comments = Arrays.asList(comment);
        List<CommentDto> commentDtos = Arrays.asList(commentDto);

        when(commentRepository.findExternalCommentsByExpenseId(expenseId)).thenReturn(comments);
        when(mapper.toCommentDtoList(comments)).thenReturn(commentDtos);

        // Act
        List<CommentDto> result = commentService.getExternalCommentsForExpense(expenseId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsInternal()).isFalse();
        verify(commentRepository, times(1)).findExternalCommentsByExpenseId(expenseId);
    }

    @Test
    void getInternalCommentsForExpense_ShouldReturnOnlyInternalComments() {
        // Arrange
        comment.setIsInternal(true);
        commentDto.setIsInternal(true);
        List<Comment> comments = Arrays.asList(comment);
        List<CommentDto> commentDtos = Arrays.asList(commentDto);

        when(commentRepository.findInternalCommentsByExpenseId(expenseId)).thenReturn(comments);
        when(mapper.toCommentDtoList(comments)).thenReturn(commentDtos);

        // Act
        List<CommentDto> result = commentService.getInternalCommentsForExpense(expenseId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsInternal()).isTrue();
        verify(commentRepository, times(1)).findInternalCommentsByExpenseId(expenseId);
    }

    @Test
    void getCommentById_WhenExists_ShouldReturnComment() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(mapper.toDto(comment)).thenReturn(commentDto);

        // Act
        CommentDto result = commentService.getCommentById(commentId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getContent()).isEqualTo("This is a test comment");
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void getCommentById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.getCommentById(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found with id");

        verify(commentRepository, times(1)).findById(commentId);
        verify(mapper, never()).toDto(any(Comment.class));
    }

    @Test
    void createComment_WithValidData_ShouldCreateSuccessfully() {
        // Arrange
        setupSecurityContext("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(commentDto);

        // Act
        CommentDto result = commentService.createComment(createRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("This is a test comment");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_AsInternalComment_ShouldCreateSuccessfully() {
        // Arrange
        setupSecurityContext("testuser");

        CreateCommentRequest internalRequest = CreateCommentRequest.builder()
                .expenseId(expenseId)
                .content("Internal comment")
                .isInternal(true)
                .build();

        Comment internalComment = Comment.builder()
                .id(commentId)
                .expense(expense)
                .author(user)
                .content("Internal comment")
                .isInternal(true)
                .build();

        CommentDto internalDto = CommentDto.builder()
                .id(commentId)
                .expenseId(expenseId)
                .authorId(userId)
                .content("Internal comment")
                .isInternal(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(commentRepository.save(any(Comment.class))).thenReturn(internalComment);
        when(mapper.toDto(internalComment)).thenReturn(internalDto);

        // Act
        CommentDto result = commentService.createComment(internalRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsInternal()).isTrue();
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_WhenExpenseNotFound_ShouldThrowException() {
        // Arrange
        setupSecurityContext("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        setupSecurityContext("unknownuser");

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Current user not found");

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_WhenExists_ShouldDelete() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment(commentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Comment not found");

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).delete(any());
    }

    private void setupSecurityContext(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
