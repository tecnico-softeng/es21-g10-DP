package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class UpdateQuestionTest extends SpockTest {
    def question
    def openEndedQuestion
    def optionOK
    def optionKO
    def user

    def itemCombinationQuestion, itemOne, itemTwo

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setImage(image)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: 'two options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionOK.setOrderPos(1)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionKO.setOrderPos(2)
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
  
        given: "create an open ended question"
        openEndedQuestion = new Question()
        openEndedQuestion.setCourse(externalCourse)
        openEndedQuestion.setKey(1)
        openEndedQuestion.setTitle(QUESTION_2_TITLE)
        openEndedQuestion.setContent(QUESTION_2_CONTENT)
        openEndedQuestion.setStatus(Question.Status.AVAILABLE)
        def openEndedQuestionDetails = new OpenEndedQuestion()
        openEndedQuestionDetails.setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)
        openEndedQuestionDetails.setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)
        openEndedQuestion.setQuestionDetails(openEndedQuestionDetails)
        questionDetailsRepository.save(openEndedQuestionDetails)
        questionRepository.save(openEndedQuestion)

    }



    def "update a question"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"

        questionRepository.count() == 3L

        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.isCorrect()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_2_CONTENT
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> !option.isCorrect()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        and: 'there are two questions in the database'
        optionRepository.findAll().size() == 2
    }

    def "update question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "update question with zero options true"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ONE_CORRECT_OPTION_NEEDED
    }

    def "update correct option in a question with answers"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)


        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CHANGE_ANSWERED_QUESTION
    }

    def "update MultipleChoiceQuestion remove old option add new one"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        def multipleChoiceQuestionDto = new MultipleChoiceQuestionDto()
        questionDto.setQuestionDetailsDto(multipleChoiceQuestionDto)
        and: 'a the old correct option'
        def newOptionOK = new OptionDto(optionOK)
        and: 'a new option'
        def newOptionKO = new OptionDto()
        newOptionKO.setContent(OPTION_1_CONTENT)
        newOptionKO.setCorrect(false)
        and: 'add options to dto'
        def newOptions = new ArrayList<OptionDto>()
        newOptions.add(newOptionOK)
        newOptions.add(newOptionKO)
        multipleChoiceQuestionDto.setOptions(newOptions)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is there"
        questionRepository.count() == 3L
        def result = questionRepository.findAll().get(0)
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.isCorrect()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_1_CONTENT
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> !option.isCorrect()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        and: 'there are two questions in the database'
        optionRepository.findAll().size() == 2
    }


    def "update an item combination question"() {

        given: "a changed question"
        def questionDto = new QuestionDto(itemCombinationQuestion)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'the first item is changed'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto(itemOne)
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemTwoDto = new ItemDto(itemTwo)
        items.add(itemOneDto)
        items.add(itemTwoDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"

        questionRepository.count() == 3L

        def result = questionRepository.findAll().get(1)
        result.getId() == itemCombinationQuestion.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT

        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 1
        result.getNumberOfCorrect() == 1
        result.getImage() == null

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())
    }

    def "update an open ended question"() {
        given: 'an open ended question'
        def openEndedQuestionDto = new QuestionDto(openEndedQuestion)
        openEndedQuestionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        openEndedQuestionDto.getQuestionDetailsDto().setProposedAnswer(OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER)
        openEndedQuestionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_2_MAX_CHARS)

        when:
        questionService.updateQuestion(openEndedQuestion.getId(), openEndedQuestionDto)

        then: "the question has changed"
        questionRepository.count() == 3L
        def result = questionRepository.findAll().get(2)
        result.getId() == openEndedQuestion.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        result
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getImage() == null
        and: 'the proposed answer has changed'
        result.getQuestionDetails().getProposedAnswer() == OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER
        result.getQuestionDetails().getMaxCharacters() == OPEN_ENDED_QUESTION_2_MAX_CHARS
    }

    def "update an open ended question without data"() {
        given: 'an open ended question'
        def openEndedQuestionDto = new QuestionDto(openEndedQuestion)
        openEndedQuestionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        when:
        questionService.updateQuestion(openEndedQuestion.getId(), openEndedQuestionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_PROPOSED_ANSWER
    }
  
    def "update the order relevance of an option in a question"(){
        given: "a question"
        def questionDto = new QuestionDto(question)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto(optionOK)
        optionDto1.setOrder(5)
        options.add(optionDto1)
        def optionDto2 = new OptionDto(optionKO)
        optionDto2.setOrder(10)
        options.add(optionDto2)
        questionDto.getQuestionDetailsDto().setOptions(options)
        and: 'a count to load options to memory due to in memory database flaw'
        optionRepository.count();
        when:
        questionService.updateQuestion(question.getId(), questionDto)
        then: "we have the question"
        questionRepository.count() == 3L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getContent().equals(optionOK.getContent())}).findAny().orElse(null)
        resOptionOne.getOrderPos() == 5
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getContent().equals(optionKO.getContent())}).findAny().orElse(null)
        resOptionTwo.getOrderPos() == 10
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
