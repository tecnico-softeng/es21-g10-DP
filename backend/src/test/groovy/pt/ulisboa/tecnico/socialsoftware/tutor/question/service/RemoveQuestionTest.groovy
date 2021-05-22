package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class RemoveQuestionTest extends SpockTest {
    def question
    def optionOK
    def optionKO
    def teacher

    def itemCombinationQuestion, itemOne, itemTwo

    def setup() {
        createExternalCourseAndExecution()

        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        question.setImage(image)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setOrderPos(1)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setOrderPos(2)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        given: "create a question"
        itemCombinationQuestion = new Question()
        itemCombinationQuestion.setKey(1)
        itemCombinationQuestion.setTitle(QUESTION_2_TITLE)
        itemCombinationQuestion.setContent(QUESTION_2_CONTENT)
        itemCombinationQuestion.setStatus(Question.Status.AVAILABLE)
        itemCombinationQuestion.setNumberOfAnswers(1)
        itemCombinationQuestion.setNumberOfCorrect(1)
        itemCombinationQuestion.setCourse(externalCourse)

        def itemQuestionDetails = new ItemCombinationQuestion()
        itemCombinationQuestion.setQuestionDetails(itemQuestionDetails)
        questionDetailsRepository.save(itemQuestionDetails)
        questionRepository.save(itemCombinationQuestion)

        and: "two items for one combination"
        itemOne = new Item()
        itemOne.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        itemOne.setSequence(0)
        itemOne.setQuestionDetails(itemQuestionDetails)

        itemTwo = new Item()
        itemTwo.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwo.setSequence(1)
        itemTwo.setQuestionDetails(itemQuestionDetails)

        itemOne.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwo.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        itemRepository.save(itemOne)
        itemRepository.save(itemTwo)
    }

    def "remove a question"() {
        when:
        questionService.removeQuestion(question.getId())

        then: "the question is removeQuestion"
        questionRepository.count() == 1L
        imageRepository.count() == 0L
        optionRepository.count() == 0L
    }

    def "remove a question used in a quiz"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        when:
        questionService.removeQuestion(question.getId())

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_IS_USED_IN_QUIZ
    }

    def "remove a question that has topics"() {
        given: 'a question with topics'
        def topicDto = new TopicDto()
        topicDto.setName("name1")
        def topicOne = new Topic(externalCourse, topicDto)
        topicDto.setName("name2")
        def topicTwo = new Topic(externalCourse, topicDto)
        question.getTopics().add(topicOne)
        topicOne.getQuestions().add(question)
        question.getTopics().add(topicTwo)
        topicTwo.getQuestions().add(question)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        when:
        questionService.removeQuestion(question.getId())

        then:
        questionRepository.count() == 1L
        imageRepository.count() == 0L
        optionRepository.count() == 0L
        topicRepository.count() == 2L
        topicOne.getQuestions().size() == 0
        topicTwo.getQuestions().size() == 0
    }

    def "remove a question that was submitted"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)

        and: "a questionSubmission"
        def questionSubmission = new QuestionSubmission()
        questionSubmission.setQuestion(question)
        questionSubmission.setSubmitter(student)
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmissionRepository.save(questionSubmission)

        when:
        questionService.removeQuestion(question.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_DELETE_SUBMITTED_QUESTION
    }

    def "remove an item combination question"() {

        when:
        questionService.removeQuestion(itemCombinationQuestion.getId())

        then: "the question is removed"
        questionRepository.count() == 1L
        itemRepository.count() == 0L
    }

    def "remove an open ended question"() {
        given: "create an open ended question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_2_TITLE)
        question.setContent(QUESTION_2_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def openEndedQuestionDetails = new OpenEndedQuestion()
        openEndedQuestionDetails.setMaxCharacters(19)
        openEndedQuestionDetails.setProposedAnswer("This is a placeholder line")
        question.setQuestionDetails(openEndedQuestionDetails)
        questionDetailsRepository.save(openEndedQuestionDetails)
        questionRepository.save(question)

        when:
        questionService.deleteQuestion(question)
        then:
        openEndedQuestionDetails.getProposedAnswer() == null
        openEndedQuestionDetails.getMaxCharacters() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
