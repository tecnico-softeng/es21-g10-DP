package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportItemCombinationQuestionsTest extends SpockTest {

    def questionId

    def setup() {

        createExternalCourseAndExecution()

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

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
        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()
    }

    def 'export and import questions to xml' () {

        given: "a xml with questions"
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: "a clean database"
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def questionResult = questionService.findQuestions(externalCourse.getId()).get(0)
        questionResult.getKey() == null
        questionResult.getTitle() == QUESTION_1_TITLE
        questionResult.getContent() == QUESTION_1_CONTENT
        questionResult.getStatus() == Question.Status.AVAILABLE.name()
        def imageResult = questionResult.getImage()
        imageResult.getWidth() == 20
        imageResult.getUrl() == IMAGE_1_URL

        def resItemOne = questionResult.getQuestionDetailsDto().getItems().get(0)
        def resItemTwo = questionResult.getQuestionDetailsDto().getItems().get(1)
        def resItemThree = questionResult.getQuestionDetailsDto().getItems().get(2)
        def resItemFour = questionResult.getQuestionDetailsDto().getItems().get(3)

        resItemOne.checkConnection(resItemTwo.getContent())
        resItemFour.checkConnection(resItemTwo.getContent())
        resItemThree.checkConnection(resItemTwo.getContent())
        resItemTwo.checkConnection(resItemThree.getContent())

    }

    def "export to latex"() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        questionsLatex != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}