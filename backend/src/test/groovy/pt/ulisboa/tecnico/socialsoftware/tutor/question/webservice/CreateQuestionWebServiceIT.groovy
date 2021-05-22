package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_USERNAME, USER_1_PASSWORD)
    }

    def "create an open-ended question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.getQuestionDetailsDto().setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)
        questionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)

        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "check the response status"
        response != null
        response.status == 200 // OK
        and: "if it responds with the correct questionDto"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.proposedAnswer == OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER
        question.questionDetailsDto.maxCharacters == OPEN_ENDED_QUESTION_1_MAX_CHARS
    }

    def "create open-ended question with invalid data"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_2_MAX_CHARS)
        // no proposed answer

        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "an exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "create an item combination question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)

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

        when: "POST request is created"
        def mapper = new ObjectMapper()

        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "response status is checked"
        response != null
        response.status == 200

        and: "correct questionDto"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()

        def resItemOne = question.questionDetailsDto.items.get(0)
        def resItemTwo = question.questionDetailsDto.items.get(1)

        resItemOne.connections.itemTwo == [resItemTwo.content]
        resItemTwo.connections.itemTwo == [resItemOne.content]
    }

    def "can't create item combination question without connecting items"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)

        and: 'One itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemOneDto = new ItemDto()
        itemOneDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_1)
        items.add(itemOneDto)
        questionDto.getQuestionDetailsDto().setItems(items)

        when: "POST request is created"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "an exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_BAD_REQUEST
    }



    def "create an multiple selection question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)

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


        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "check the response status"
        response != null
        response.status == 200 // OK
        and: "if it responds with the correct questionDto"
        def question = response.data
        question.id != null
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        for (Object option : question.questionDetailsDto.options) {
            if (option.content == OPTION_1_CONTENT) {
                option.order == 1
                option.correct
            }

            if (option.content == OPTION_2_CONTENT) {
                option.order == 2
                option.correct
            }

            if (option.content == OPTION_3_CONTENT) {
                option.order == 3
                !option.correct
            }

        }

    }

    def "create multiple selection question with invalid data"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)


        and: 'no correct options'
        def options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_1_CONTENT)
        optionDto1.setCorrect(false)
        optionDto1.setOrder(1)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_1_CONTENT)
        optionDto2.setCorrect(false)
        optionDto1.setOrder(2)
        options.add(optionDto2)

        questionDto.getQuestionDetailsDto().setOptions(options)



        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "an exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_BAD_REQUEST
    }



    def cleanup() {
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}