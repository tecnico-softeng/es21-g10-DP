package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import spock.lang.Unroll

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

@DataJpaTest
class CreateQuestionTest extends SpockTest {

    def setup() {
        createExternalCourseAndExecution()
    }

    def "create multiple selection question with 2 correct answers and 4 options"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QuestionTitle)
        questionDto.setContent(QuestionContent)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(optionContent)
        optionDto1.setCorrect(true)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(optionContent)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(optionContent)
        optionDto3.setCorrect(false)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(optionContent)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QuestionTitle
        result.getContent() == QuestionContent
        result.getImage() == null
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == CourseName
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 2

        where:
        optionContent     | QuestionTitle    | QuestionContent    | CourseName
        OPTION_1_CONTENT  | QUESTION_1_TITLE | QUESTION_1_CONTENT | COURSE_1_NAME
        OPTION_2_CONTENT  | QUESTION_2_TITLE | QUESTION_2_CONTENT | COURSE_1_NAME
    }

    def "create multiple selection question with 3 correct answers and 4 options"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_1_CONTENT)
        optionDto3.setCorrect(true)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_1_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 3
    }

    def "create multiple selection question with 2 correct ordered answers and 4 options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setOrder(1)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(true)
        optionDto2.setOrder(2)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_3_CONTENT)
        optionDto3.setCorrect(false)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_4_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 2
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isOrdered()).count() == 2

    }

    def "create multiple selection question with an image and 2 correct ordered answers and 4 options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setOrder(1)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(true)
        optionDto2.setOrder(2)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_1_CONTENT)
        optionDto3.setCorrect(false)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_1_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 2
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isOrdered()).count() == 2
    }

    def "create multiple selection question with 3 correct ordered answers and 4 options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setOrder(1)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(true)
        optionDto2.setOrder(2)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_3_CONTENT)
        optionDto3.setCorrect(false)
        optionDto3.setOrder(3)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_4_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 2
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isOrdered()).count() == 3

    }

    def "create multiple selection question with image and 2 correct answers and 4 options"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_1_CONTENT)
        optionDto3.setCorrect(false)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_1_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetailsDto().getOptions().size() == 4
        result.getCourse().getName() == COURSE_1_NAME
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getOptions().stream().filter(x -> x.isCorrect() == true).count() == 2
    }

    def "cannot create multiple selection question without correct answers"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "4 optionDto"
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(false)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(false)
        optionDto2.setOrder(2)
        options.add(optionDto2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_1_CONTENT)
        optionDto3.setCorrect(false)
        options.add(optionDto3)
        def optionDto4 = new OptionDto()
        optionDto4.setContent(OPTION_1_CONTENT)
        optionDto4.setCorrect(false)
        options.add(optionDto4)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ONE_CORRECT_OPTION_NEEDED
    }

    def "create a multiple choice question with no image and one option"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getOptions().size() == 1
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()

    }

    def "create a multiple choice question with image and two options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)
        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        result.getQuestionDetails().getOptions().size() == 2
    }

    def "create two multiple choice questions"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: 'are created two questions'
        questionService.createQuestion(externalCourse.getId(), questionDto)
        questionDto.setKey(null)
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the two questions are created with the correct numbers"
        questionRepository.count() == 2L
        def resultOne = questionRepository.findAll().get(0)
        def resultTwo = questionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
    }


    def "create a code fill in question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeFillInQuestionDto()
        codeQuestionDto.setCode(CODE_QUESTION_1_CODE)
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        OptionDto optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        fillInSpotDto.getOptions().add(optionDto)
        fillInSpotDto.setSequence(1)

        codeQuestionDto.getFillInSpots().add(fillInSpotDto)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getFillInSpots().size() == 1
        result.getQuestionDetailsDto().getFillInSpots().get(0).getOptions().size() == 1

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoCode = (CodeFillInQuestion) repoResult.getQuestionDetails()
        repoCode.getFillInSpots().size() == 1
        repoCode.getCode() == CODE_QUESTION_1_CODE
        repoCode.getLanguage() == CODE_QUESTION_1_LANGUAGE
        def resOption = repoCode.getFillInSpots().get(0).getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()

    }

    def "cannot create a code fill in question without fillin spots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED
    }

    def "cannot create a code fill in question with fillin spots without options"() {
        given: "a questionDto with 1 fill in spot without options"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        questionDto.getQuestionDetailsDto().getFillInSpots().add(fillInSpotDto)


        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }

    def "cannot create a code fill in question with fillin spots without correct options"() {
        given: "a questionDto with 1 fill in spot without options"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        OptionDto optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        questionDto.getQuestionDetailsDto().getFillInSpots().add(fillInSpotDto)


        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }


    def "create a code order question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        CodeOrderSlotDto slotDto3 = new CodeOrderSlotDto()
        slotDto3.content = OPTION_1_CONTENT;
        slotDto3.order = 3;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)
        codeQuestionDto.getCodeOrderSlots().add(slotDto3)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getCodeOrderSlots().size() == 3
        result.getQuestionDetailsDto().getCodeOrderSlots().get(0).getContent() == OPTION_1_CONTENT

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoCode = (CodeOrderQuestion) repoResult.getQuestionDetails()
        repoCode.getCodeOrderSlots().size() == 3
        repoCode.getLanguage() == CODE_QUESTION_1_LANGUAGE
        def resOption = repoCode.getCodeOrderSlots().get(0)
        resOption.getContent() == OPTION_1_CONTENT
    }

    def "cannot create a code order question without CodeOrderSlots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }

    def "cannot create a code order question without 3 CodeOrderSlots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)

        questionDto.setQuestionDetailsDto(codeQuestionDto)
        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }

    def "cannot create a code order question without 3 CodeOrderSlots with order"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        CodeOrderSlotDto slotDto3 = new CodeOrderSlotDto()
        slotDto3.content = OPTION_1_CONTENT;
        slotDto3.order = null;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)
        codeQuestionDto.getCodeOrderSlots().add(slotDto3)

        questionDto.setQuestionDetailsDto(codeQuestionDto)
        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }

    def "Create an item combination question"() {
      
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())


        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)
        items.add(itemOneDto)
        items.add(itemTwoDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())
    }

    def "Create an item combination question where one item connects to two others"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)
        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_3)
        itemThreeDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)
        items.add(itemOneDto)
        items.add(itemTwoDto)
        items.add(itemThreeDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        def resItemThree = result.getQuestionDetails().getItems().get(2)

        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())
        resItemOne.checkConnection(resItemThree.getContent())
        resItemThree.checkConnection(resItemOne.getContent())
    }
      
    def "Create an item combination question where two items connects to two others"() {
      
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemFourDto = new ItemDto()
        itemFourDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_4)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_4)
        itemFourDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        itemFourDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_4)

        itemThreeDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_3)

        items.add(itemOneDto)
        items.add(itemTwoDto)
        items.add(itemThreeDto)
        items.add(itemFourDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        def resItemThree = result.getQuestionDetails().getItems().get(2)
        def resItemFour = result.getQuestionDetails().getItems().get(3)

        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())

        resItemOne.checkConnection(resItemFour.getContent())
        resItemFour.checkConnection(resItemOne.getContent())

        resItemFour.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemFour.getContent())

        resItemThree.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemThree.getContent())
    }

    def "Create an item combination question where one item connects with all items"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemFourDto = new ItemDto()
        itemFourDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_4)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_4)
        itemFourDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_3)
        itemThreeDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        items.add(itemOneDto)
        items.add(itemTwoDto)
        items.add(itemThreeDto)
        items.add(itemFourDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        def resItemThree = result.getQuestionDetails().getItems().get(2)
        def resItemFour = result.getQuestionDetails().getItems().get(3)

        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())

        resItemOne.checkConnection(resItemFour.getContent())
        resItemFour.checkConnection(resItemOne.getContent())

        resItemOne.checkConnection(resItemThree.getContent())
        resItemThree.checkConnection(resItemOne.getContent())
    }

    def "Create an item combination where it checks for not created connections"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemFourDto = new ItemDto()
        itemFourDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_4)

        itemOneDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_2)
        itemTwoDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_1)

        items.add(itemOneDto)
        items.add(itemTwoDto)
        items.add(itemThreeDto)
        items.add(itemFourDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        def resItemOne = result.getQuestionDetails().getItems().get(0)
        def resItemTwo = result.getQuestionDetails().getItems().get(1)
        def resItemThree = result.getQuestionDetails().getItems().get(2)
        def resItemFour = result.getQuestionDetails().getItems().get(3)

        resItemOne.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemOne.getContent())

        !resItemOne.checkConnection(resItemFour.getContent())
        !resItemFour.checkConnection(resItemOne.getContent())

        !resItemOne.checkConnection(resItemThree.getContent())
        !resItemThree.checkConnection(resItemOne.getContent())

        !resItemTwo.checkConnection(resItemFour.getContent())
        !resItemFour.checkConnection(resItemTwo.getContent())

        !resItemTwo.checkConnection(resItemThree.getContent())
        !resItemThree.checkConnection(resItemTwo.getContent())

        !resItemFour.checkConnection(resItemThree.getContent())
        !resItemThree.checkConnection(resItemFour.getContent())
    }

    def "Can't create an item combination question with one item"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'One itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        items.add(itemOneDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEMS_NEEDED
    }

    def "Can't create an item combination question without items"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEMS_NEEDED
    }

    def "Can't create an item combination question without a connection between two items"() {

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        def itemTwoDto = new ItemDto()
        itemTwoDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_2)

        items.add(itemOneDto)
        items.add(itemTwoDto)
        
        questionDto.getQuestionDetailsDto().setItems(items)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_EXISTING_CONNECTION
    }

    @Unroll
    def "fail to create any question for invalid/non-existent course (#nonExistentId)"(Integer nonExistentId) {
        given: "any multiple choice question dto"
        def questionDto = new QuestionDto()
        when:
        questionService.createQuestion(nonExistentId, questionDto)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_NOT_FOUND
        where:
        nonExistentId << [-1, 0, 200]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
