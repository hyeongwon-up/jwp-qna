package qna.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AnswerRepositoryTest {
    public static final Answer A1 = new Answer(UserRepositoryTest.JAVAJIGI, QuestionRepositoryTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserRepositoryTest.SANJIGI, QuestionRepositoryTest.Q1, "Answers Contents2");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void 댓글_등록() {
        final User user = createTestUser();
        final Question question = createTestQuestion();

        final Answer saved = answerRepository.save(new Answer(user, question, "댓글 내용"));

        assertThat(saved).isNotNull();
    }

    @Test
    void 댓글_조회() {
        final User user = createTestUser();
        final Question question = createTestQuestion();
        final Answer expected = answerRepository.save(new Answer(user, question, "댓글 내용"));

        final Answer actual = answerRepository.findById(expected.getId()).get();

        assertThat(actual).isNotNull();
    }

    @Test
    void 질문으로_댓글_조회() {
        final User user = createTestUser();
        final Question question = createTestQuestion();
        answerRepository.save(new Answer(user, question, "댓글 내용"));

        List<Answer> answers = answerRepository.findByQuestionId(question.getId());

        assertThat(answers).hasSize(1);
    }

    @Test
    void 사용자로_댓글_조회() {
        final User user = createTestUser();
        final Question question = createTestQuestion();
        answerRepository.save(new Answer(user, question, "댓글 내용"));

        List<Answer> answers = answerRepository.findByWriterId(user.getId());

        assertThat(answers).hasSize(1);
    }

    @Test
    void 댓글_수정() {
        final User user = createTestUser();
        final Question question = createTestQuestion();
        final Answer answer = answerRepository.save(new Answer(user, question, "댓글 내용"));

        answer.updateContents("댓글 수정");

        assertThat(answer.getContents()).isEqualTo("댓글 수정");
    }

    @Test
    void 댓글_삭제() {
        final User user = createTestUser();
        final Question question = createTestQuestion();
        final Answer answer = answerRepository.save(new Answer(user, question, "댓글 내용"));

        answerRepository.delete(answer);

        final Optional<Answer> actual = answerRepository.findById(answer.getId());
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void 댓글_삭제_DELETED() {
        final User user = userRepository.save(createTestUser());
        final Question question = questionRepository.save(new Question("제목", "내용").writeBy(user));
        final Answer answer = new Answer(user, question, "댓글 추가");
        answerRepository.save(answer);

        answer.delete(user);

        final Answer actual = answerRepository.findById(answer.getId()).get();
        assertThat(actual.isDeleted()).isTrue();
    }

    private User createTestUser() {
        return userRepository.save(new User(1L, "donghee.han", "password", "donghee", "donghee.han@slipp.net"));
    }

    private Question createTestQuestion() {
        return questionRepository.save(new Question(2L, "제목", "내용"));
    }
}
