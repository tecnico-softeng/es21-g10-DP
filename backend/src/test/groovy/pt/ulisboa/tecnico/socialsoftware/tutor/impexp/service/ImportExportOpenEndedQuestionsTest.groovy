package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportOpenEndedQuestionsTest extends SpockTest {
    static Integer questionId_1, questionId_2;
    static ImageDto imageDto;

    def setup() {
        createExternalCourseAndExecution()

        def questionDto_1 = new QuestionDto()
        // Question 1, with image
        questionDto_1.setKey(1)
        questionDto_1.setTitle(QUESTION_1_TITLE)
        questionDto_1.setContent(QUESTION_1_CONTENT)
        questionDto_1.setStatus(Question.Status.AVAILABLE.name())

        def openQuestionDto_1 = new OpenEndedQuestionDto()
        openQuestionDto_1.setMaxCharacters(OPEN_ENDED_QUESTION_1_MAX_CHARS)
        openQuestionDto_1.setProposedAnswer(OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER)

        imageDto = new ImageDto()
        imageDto.setUrl(IMAGE_1_URL)
        imageDto.setWidth(20)
        questionDto_1.setImage(imageDto)

        questionDto_1.setQuestionDetailsDto(openQuestionDto_1)

        // Question 2, w/o image
        def questionDto_2 = new QuestionDto()
        questionDto_2.setKey(2)
        questionDto_2.setTitle(QUESTION_2_TITLE)
        questionDto_2.setContent(QUESTION_2_CONTENT)
        questionDto_2.setStatus(Question.Status.AVAILABLE.name())

        def openQuestionDto_2 = new OpenEndedQuestionDto()
        openQuestionDto_2.setMaxCharacters(OPEN_ENDED_QUESTION_2_MAX_CHARS)
        openQuestionDto_2.setProposedAnswer(OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER)

        questionDto_2.setQuestionDetailsDto(openQuestionDto_2)

        questionId_1 = questionService.createQuestion(externalCourse.getId(), questionDto_1).getId()
        questionId_2 = questionService.createQuestion(externalCourse.getId(), questionDto_2).getId()
    }

    def 'export and import open ended questions to xml'() {
        given: 'a xml with open ended questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: 'a clean database'
        questionService.removeQuestion(questionId_1)
        questionService.removeQuestion(questionId_2)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 2
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(idx)
        questionResult.getKey() == null
        questionResult.getTitle() == title
        questionResult.getContent() == content
        questionResult.getStatus() == Question.Status.AVAILABLE.name()
        def imageResult = questionResult.getImage()
        if(imageResult) {
            imageResult == imageDto
            imageResult.getWidth() == 20
            imageResult.getUrl() == IMAGE_1_URL
        }
        questionResult.getQuestionDetailsDto().getMaxCharacters() == maxChars
        questionResult.getQuestionDetailsDto().getProposedAnswer() == proposedAnswer

        where:
        idx | questionId   | title            | content            | proposedAnswer                        | maxChars
        0   | questionId_1 | QUESTION_1_TITLE | QUESTION_1_CONTENT | OPEN_ENDED_QUESTION_1_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_1_MAX_CHARS
        1   | questionId_2 | QUESTION_2_TITLE | QUESTION_2_CONTENT | OPEN_ENDED_QUESTION_2_PROPOSED_ANSWER | OPEN_ENDED_QUESTION_2_MAX_CHARS

    }

    def 'export to latex'() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        questionsLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
