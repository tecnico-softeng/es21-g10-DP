package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Association;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

public interface Visitor {
    default void visitQuestion(Question question) {}

    default void visitImage(Image image) {}

    default void visitOption(Option option) {}

    default void visitItem(Item item) {}

    default void visitAssociation(Association association) {}

    default void visitQuiz(Quiz quiz) {}

    default void visitQuizQuestion(QuizQuestion quizQuestion) {}

    default void visitUser(User user) {}

    default void visitAuthUser(AuthUser authUser) {}

    default void visitQuizAnswer(QuizAnswer quizAnswer) {}

    default void visitQuestionAnswer(QuestionAnswer questionAnswer) {}

    default void visitTopic(Topic topic) {}

    default void visitCourse(Course course) {}

    default void visitAssessment(Assessment assessment) {}

    default void visitCourseExecution(CourseExecution courseExecution) {}

    default void visitAnswerDetails(MultipleChoiceAnswer answer){}

    default void visitQuestionDetails(MultipleChoiceQuestion question) {}

    default void visitQuestionDetails(ItemCombinationQuestion question) {}

    default void visitAnswerDetails(CodeFillInAnswer answer){}

    default void visitQuestionDetails(CodeFillInQuestion question) {}

    default void visitFillInSpot(CodeFillInSpot spot) {}

    default void visitFillInOption(CodeFillInOption spot) {}

    default void visitDiscussion(Discussion discussion) {}

    default void visitReply(Reply reply) {}

    default void visitAnswerDetails(CodeOrderAnswer answer){}

    default void visitQuestionDetails(CodeOrderQuestion codeOrderQuestion) {}

    default void visitCodeOrderSlot(CodeOrderSlot codeOrderSlot) {}

    default void visitQuestionDetails(OpenEndedQuestion openEndedQuestion) {}
}
