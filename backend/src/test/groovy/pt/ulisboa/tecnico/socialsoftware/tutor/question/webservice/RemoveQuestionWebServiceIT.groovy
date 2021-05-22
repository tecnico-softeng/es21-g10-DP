package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port
    def course
    def response
    def courseExecution
    def teacher

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
        teacher = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
    }

    def "remove multiple choice question from course execution"(){
        given: "a teacher"
        createdUserLogin(USER_1_USERNAME,USER_1_PASSWORD)
        and: 'a multiple choice question'
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        and: 'options'
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(true)
        optionDto1.setOrder(1)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(true)
        optionDto1.setOrder(2)
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
        questionService.createQuestion(course.getId(), questionDto)
        def result = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                    path: '/questions/'+result.getId(),
                    requestContentType: 'application/json')
        then: "response status must be ok"
        response != null
        response.status == 200 //OK
        and: "the question isn't in the database"
        questionRepository.findById(result.getId()).isEmpty()
    }

    def "remove an open-ended question"() {
        given: "a techaer"
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        and: "an open-ended question dto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.getQuestionDetailsDto().setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)
        questionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)
        questionService.createQuestion(course.getId(), questionDto)
        def result = questionRepository.findAll().get(0)

        when: "a DELETE request is made"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: "application/json"
        )

        then: "check the response status"
        response != null
        response.status == 200 // OK
        and: "the question isn't in the database"
        questionRepository.findById(result.getId()).isEmpty()
    }

    def "remove a non existing question"() {
        given: "a techaer"
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
        and: "an open-ended question dto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.getQuestionDetailsDto().setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)
        questionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)
        questionService.createQuestion(course.getId(), questionDto)
        def result = questionRepository.findAll().get(0)
        and: "a DELETE request is made"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: "application/json"
        )

        when: "the same DELETE request is made for the same ID"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: "application/json"
        )

        then: "expect an exception"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "remove item combination question"() {
        given: "a teacher"
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        and: "an item combination question"
        def questionDto = new QuestionDto()
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
        questionService.createQuestion(course.getId(), questionDto)
        def result = questionRepository.findAll().get(0)

        when: "a Delete request is made"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "the question isn't in the database"
        questionRepository.findById(result.getId()).isEmpty()
    }

    def "can't remove a non existing item combination question"() {
        given: "a teacher"
        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        and: "an item combination question"
        def questionDto = new QuestionDto()
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
        questionService.createQuestion(course.getId(), questionDto)
        def result = questionRepository.findAll().get(0)

        when: "a Delete request is made"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: 'application/json'
        )

        and: "the same DELETE request is made for the same ID"
        response = restClient.delete(
                path: "/questions/" + result.getId(),
                requestContentType: 'application/json'
        )

        then: "an exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_FORBIDDEN
    }


    def cleanup() {
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
