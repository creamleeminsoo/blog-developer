package me.leeminsoo.blogdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.leeminsoo.blogdeveloper.config.error.ErrorCode;
import me.leeminsoo.blogdeveloper.domain.Article;
import me.leeminsoo.blogdeveloper.domain.Comment;
import me.leeminsoo.blogdeveloper.domain.User;
import me.leeminsoo.blogdeveloper.dto.AddArticleRequest;
import me.leeminsoo.blogdeveloper.dto.AddCommentRequest;
import me.leeminsoo.blogdeveloper.dto.UpdateArticleRequest;
import me.leeminsoo.blogdeveloper.dto.UpdateCommentRequest;
import me.leeminsoo.blogdeveloper.repository.BlogRepository;

import me.leeminsoo.blogdeveloper.repository.CommentRepository;
import me.leeminsoo.blogdeveloper.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;





@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
        commentRepository.deleteAll();
    }


    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }


    @DisplayName("addArticle: 아티클 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 아티클 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    @DisplayName("findArticle: 아티클 단건 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }


    @DisplayName("deleteArticle: 아티클 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        // then
        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }


    @DisplayName("updateArticle: 아티클 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }


    @DisplayName("findArticle: 잘못된 Http 메서드로 아티클을 조회할려고 하면 조회에 실패한다")
    @Test
    public void invalidHttpMethod() throws Exception {
        final String url = "/api/articles/{id}";

        final ResultActions resultActions = mockMvc.perform(post(url, 1));

        resultActions.andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @DisplayName("findArticle: 존재하지 않는 아티클을 조회할려고 하면 조회에 실패한다")
    @Test
    public void findArticleInvalidArticle() throws Exception {
        final String url = "/api/articles/{id}";
        final long invalidId = 1;
        final ResultActions resultActions = mockMvc.perform(get(url, invalidId));

        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()));
    }

    @DisplayName("addComment: 댓글 추가에 성공한다.")
    @Test
    public void addComment() throws Exception {
        final String url = "/api/comments";

        Article savedArticle = createDefaultArticle();
        final Long articleId = savedArticle.getId();
        final String content = "content";
        final AddCommentRequest userRequest = new AddCommentRequest(articleId, content);
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(principal)
                .content(requestBody));

        result.andExpect(status().isCreated());

        List<Comment> comments = commentRepository.findAll();

        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getArticle().getId()).isEqualTo(articleId);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("deleteComment: 댓글 삭제에 성공한다.")
    @Test
    public void deleteComment() throws Exception {
        // given
        final String url ="/api/comments/{commentId}";
        Comment comment = createDefaultComment();
        Comment comment1 = createDefaultComment();
        Comment comment2 = createDefaultComment();

        long commentId = comment2.getId();

        mockMvc.perform(delete(url,commentId))
                .andExpect(status().isOk());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(2);

    }

    @DisplayName("updateComment: 댓글 수정에 성공한다.")
    @Test
    public void updateComment() throws Exception {
        final String url = "/api/comments/{commentId}";
        Comment savedComment = createDefaultComment();
        long commentId = savedComment.getId();

        final String newContent = "new comment";
        UpdateCommentRequest request = new UpdateCommentRequest(newContent);

        ResultActions result = mockMvc.perform(put(url,commentId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Comment comment = commentRepository.findById(commentId).get();

        assertThat(comment.getContent()).isEqualTo(newContent);


    }

    private Comment createDefaultComment() {
        return commentRepository.save(Comment.builder()
                .article(createDefaultArticle())
                .content("content")
                .author(user.getUsername())
                .build());
    }

}




