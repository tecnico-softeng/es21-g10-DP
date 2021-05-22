package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportMultipleChoiceQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def questionId
    def response

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)

        def optionDto = new OptionDto()
        optionDto.setSequence(0)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setOrder(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setSequence(1)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)


        when: "it's created by a POST request"
        def mapper = new ObjectMapper()
        response = restClient.post(
                path: "/courses/" + course.getId() + "/questions",
                body: mapper.writeValueAsString(questionDto),
                requestContentType: "application/json"
        )
        questionId = response.data.id
    }

    def "export a multiple selection question"() {
        when:
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        def map = restClient.get(
                path: "/courses/" + course.getId() + "/questions/export",
                requestContentType: "application/zip"
        )

        then: "the response status is OK"
        assert map['response'].status == 200
    }

    def cleanup() {

        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())

        courseRepository.deleteById(course.getId())
    }
}