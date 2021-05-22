package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def response
    def courseExecution
    def teacher
    def result
    def student
    def pemQuestionDto
    def questionId
    def itemQuestionDto
    def itemResult
    def itemStudent

    def setup(){
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL, User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        itemStudent = new User(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        itemStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        itemStudent.addCourse(courseExecution)
        courseExecution.addUser(itemStudent)
        userRepository.save(itemStudent)

        pemQuestionDto = new QuestionDto()
        pemQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        pemQuestionDto.setTitle(QUESTION_3_TITLE)
        pemQuestionDto.setContent(QUESTION_3_CONTENT)
        pemQuestionDto.setStatus(Question.Status.AVAILABLE.name())

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

        pemQuestionDto.getQuestionDetailsDto().setOptions(options)
        questionService.createQuestion(course.getId(), pemQuestionDto)
        result = questionRepository.findAll().get(0)

        //--
        itemQuestionDto = new QuestionDto()
        itemQuestionDto.setTitle(QUESTION_1_TITLE)
        itemQuestionDto.setContent(QUESTION_1_CONTENT)
        itemQuestionDto.setStatus(Question.Status.AVAILABLE.name())

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
        itemQuestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        itemQuestionDto.getQuestionDetailsDto().setItems(items)

        questionService.createQuestion(course.getId(), itemQuestionDto)
        itemResult = questionRepository.findAll().get(1)
        //--

        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.getQuestionDetailsDto().setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)
        questionDto.getQuestionDetailsDto().setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )
        questionId = response.data.id
    }

    def "update the proposed answer of an open ended question"() {
        given: 'a changed question'
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())
        questionDto.setTitle(title)
        questionDto.setContent(content)
        questionDto.getQuestionDetailsDto().setProposedAnswer(proposedAnswer)
        questionDto.getQuestionDetailsDto().setMaxCharacters(maxChars)
        when: "it's updated by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.put(
                path: "/questions/" + questionId,
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )

        then: "check the response status"
        response != null
        response.status == 200 // OK
        and: "if it responds with the correct questionDto"
        def question = response.data
        question.id != null
        question.title == title
        question.content == content
        question.questionDetailsDto.proposedAnswer == proposedAnswer
        question.questionDetailsDto.maxCharacters == maxChars

        where:
        idx | title            | content            | proposedAnswer                        | maxChars
        0   | QUESTION_1_TITLE | QUESTION_1_CONTENT | OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_2_MAX_CHARS
        1   | QUESTION_1_TITLE | QUESTION_1_CONTENT | OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_1_MAX_CHARS
        2   | QUESTION_1_TITLE | QUESTION_2_CONTENT | OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_1_MAX_CHARS
        3   | QUESTION_2_TITLE | QUESTION_1_CONTENT | OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_1_MAX_CHARS
        4   | QUESTION_2_TITLE | QUESTION_2_CONTENT | OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_2_MAX_CHARS
    }

    def "update multiple choice question from course execution"() {
        given: "a teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        and: "a changed question"
        def newQuestionDto = pemQuestionDto
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        newQuestionDto.getQuestionDetailsDto().setOptions(options)
        when:
        def mapper = new ObjectMapper()
        response = restClient.put(
                path: "/questions/" + result.getId(),
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: "application/json")

        then: "response status must be ok"
        response != null
        response.status == 200 //OK
        and: "the question is updated"
        def question = response.data
        question.id != null
        question.title == newQuestionDto.getTitle()
        question.content == newQuestionDto.getContent()
        and: "the options are updated"
        for (int i = 0; i < question.questionDetailsDto.options.size(); i++) {
            if(question.questionDetailsDto.options[i].content == OPTION_2_CONTENT)
                question.questionDetailsDto.options[i].isCorrect == false
            if(question.questionDetailsDto.options[i].content == OPTION_3_CONTENT)
                question.questionDetailsDto.options[i].isCorrect == true
        }
    }

    def "can't update multiple choice question from course execution"() {
        given: "a student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)
        and: "an update to the question"
        def newQuestionDto = pemQuestionDto
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        and: '2 updated options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        newQuestionDto.getQuestionDetailsDto().setOptions(options)
        when:
        def mapper = new ObjectMapper()
        response = restClient.put(
                path: "/questions/" + result.getId(),
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: "application/json")
        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "update an item combination question"() {

        createdUserLogin(USER_3_USERNAME, USER_2_PASSWORD)

        given: "a questionSubmissionDto"
        def newQuestionDto = itemQuestionDto
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemFourDto = new ItemDto()
        itemFourDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_4)
        itemThreeDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_4)
        itemFourDto.addConnections(ITEM_COMBINATION_QUESTION_CONTENT_3)
        items.add(itemThreeDto)
        items.add(itemFourDto)
        newQuestionDto.getQuestionDetailsDto().setItems(items)

        when:
        def mapper = new ObjectMapper()
        response = restClient.put(
                path: "/questions/" + itemResult.getId(),
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: "application/json"
        )

        then: "check the response status"
        response != null
        response.status == 200

        and: "if it responds with the updated question"
        def question = response.data
        question.id != null
        question.title == newQuestionDto.getTitle()
        question.content == newQuestionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()

        def resItemThree = question.questionDetailsDto.items.get(0)
        def resItemFour = question.questionDetailsDto.items.get(1)

        resItemThree.content == ITEM_COMBINATION_QUESTION_CONTENT_1
        resItemFour.content == ITEM_COMBINATION_QUESTION_CONTENT_2
    }

    def "can't update an item combination question without connecting items"() {

        createdUserLogin(USER_3_USERNAME, USER_2_PASSWORD)

        given: "a questionSubmissionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)

        and: 'Two itemId to connect'
        def items = new ArrayList<ItemDto>()
        def itemThreeDto = new ItemDto()
        itemThreeDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_3)
        def itemFourDto = new ItemDto()
        itemFourDto.setContent(ITEM_COMBINATION_QUESTION_CONTENT_4)
        items.add(itemThreeDto)
        items.add(itemFourDto)
        newQuestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        newQuestionDto.getQuestionDetailsDto().setItems(items)

        when:
        def mapper = new ObjectMapper()
        response = restClient.put(
                path: "/questions/" + itemResult.getId(),
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: "application/json"
        )

        then: "an exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.status == HttpStatus.SC_BAD_REQUEST
    }

    def cleanup() {
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        userRepository.deleteById(itemStudent.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}