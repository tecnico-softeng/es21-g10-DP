describe('Manage Multiple Choice Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    optionPrefix,
    correctIndex,
    correctOptionOrder
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);
        cy.get('li').each(($el, index, $list) => {
          cy.get($el).should('contain', optionPrefix + index);
          if(correctIndex.includes(index,0)) {
            order = (correctOptionOrder[correctIndex.indexOf(index)])
            if (order === null)
              cy.get($el).should('contain', '**');
            else
              cy.get($el).should('contain', '#' + order.toString());
          }
          else
          cy.get($el).should('not.contain', '[★]');
        });
      });
  }

  function validateQuestionFull(
    title,
    content,
    optionPrefix,
    correctIndex,
    correctOptionOrder
  ) {
    cy.log('Validate question with show dialog. ' + correctIndex);

    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion(title, content, optionPrefix, correctIndex, correctOptionOrder);

    cy.get('button').contains('close').click();
  }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
    cy.cleanOpenEndedQuestionsByName('Cypress Question Example');
    cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/courses/*/questions').as('getQuestions');
    cy.route('GET', '/courses/*/topics').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    cy.wait('@getQuestions').its('status').should('eq', 200);

    cy.wait('@getTopics').its('status').should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });



  it('Creates a new multiple choice question', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionQuestionTextArea"]'
    ).type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          if (index === 2 || index === 1) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            cy.get(`[data-cy="Order${index + 1}"]`).type(index.toString())
          }
          cy.get(`[data-cy="Option${index + 1}"]`).type('Option ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
        'Option ',
        [1,2],
        [1,2]
    );
  });

  it('Can view question (with button)', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('visibility').click();
      });

    validateQuestion(
        'Cypress Question Example - 01',
        'Cypress Question Example - Content - 01',
        'Option ',
        [1,2],
        [1,2]
    );

    cy.get('button').contains('close').click();
  });

  it('Can view question (with click)', function () {
    cy.get('[data-cy="questionTitleGrid"]').first().click();

    validateQuestion(
        'Cypress Question Example - 01',
        'Cypress Question Example - Content - 01',
        'Option ',
        [1,2],
        [1,2]
    );

    cy.get('button').contains('close').click();
  });

  it('Can update title (with right-click)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionTitleTextArea"]')
          .clear({ force: true })
          .type('Cypress Question Example - 01 - Edited', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited');

    validateQuestionFull(
        'Cypress Question Example - 01 - Edited',
        'Cypress Question Example - Content - 01',
        'Option ',
        [1,2],
        [1,2]
    );
  });

  it('Can update content (with button)', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('edit').click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible')
      .within(($list) => {
        cy.get('span.headline').should('contain', 'Edit Question');

        cy.get('[data-cy="questionQuestionTextArea"]')
          .clear({ force: true })
          .type('Cypress New Content For Question!', { force: true });

        cy.get('button').contains('Save').click();
      });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
      'Cypress Question Example - 01 - Edited',
      'Cypress New Content For Question!',
        'Option ',
        [1,2],
        [1,2]
    );
  });

  it('Can update order', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
        .first()
        .within(($list) => {
          cy.get('button').contains('edit').click();
        });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible')
        .within(($list) => {
          cy.get('span.headline').should('contain', 'Edit Question');

          cy.get(`[data-cy="Order2"]`).clear({ force: true }).type("20");

          cy.get('button').contains('Save').click();
        });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
        (title = 'Cypress Question Example - 01 - Edited'),
        (content = 'Cypress New Content For Question!'),
        'Option ',
        (correctIndex = [1,2]),
        (correctOptionOrder = [20,2])

    );
  });

  it('Can update number of correct options', function () {
    cy.route('PUT', '/questions/*').as('updateQuestion');

    cy.get('tbody tr')
        .first()
        .within(($list) => {
          cy.get('button').contains('edit').click();
        });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible')
        .within(($list) => {
          cy.get('span.headline').should('contain', 'Edit Question');

          cy.get(`[data-cy="Switch4"]`).check({ force: true });
          cy.get(`[data-cy="Order4"]`).type("5");

          cy.get('button').contains('Save').click();
        });

    cy.wait('@updateQuestion').its('status').should('eq', 200);

    validateQuestionFull(
        (title = 'Cypress Question Example - 01 - Edited'),
        (content = 'Cypress New Content For Question!'),
        'Option ',
        (correctIndex = [1,2,3]),
        (correctOptionOrder = [20,2,5])

    );
  });

  it('Can duplicate question', function () {
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('cached').click();
      });

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTitleTextArea"]')
      .should('have.value', 'Cypress Question Example - 01 - Edited')
      .type('{end} - DUP', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').should(
      'have.value',
      'Cypress New Content For Question!'
    );

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 4)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          cy.get('textarea').should('have.value', 'Option ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01 - Edited - DUP');

    validateQuestionFull(
        (title = 'Cypress Question Example - 01 - Edited - DUP'),
        (content = 'Cypress New Content For Question!'),
        'Option ',
        (correctIndex = [1,2,3]),
        (correctOptionOrder = [20,2,5])

    );
  });

  it('Can delete created question', function () {
    cy.route('DELETE', '/questions/*').as('deleteQuestion');
    cy.get('tbody tr')
      .first()
      .within(($list) => {
        cy.get('button').contains('delete').click();
      });

    cy.wait('@deleteQuestion').its('status').should('eq', 200);
  });

  it('Creates a new multiple choice question with only 2 options', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01 (2 Options)', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
      'Cypress Question Example - Content - 01 (2 Options)',
      {
        force: true,
      }
    );

    cy.get('[data-cy="questionOptionsInput"').should('have.length', 4);

    cy.get(`[data-cy="Option1"]`).type('Option2 0');
    cy.get(`[data-cy="Switch1"]`).check({ force: true });
    cy.get(`[data-cy="Order1"]`).type("1");
    cy.get(`[data-cy="Option2"]`).type('Option2 1');

    cy.get(`[data-cy="Delete4"]`).click({ force: true });
    cy.get(`[data-cy="Delete3"]`).click({ force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01 (2 Options)',
      'Cypress Question Example - Content - 01 (2 Options)',
      'Option2 ',
      [0],
        [1]
    );
  });

  it('Creates a new multiple choice question with 10 options', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01 (10 Options)', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
      'Cypress Question Example - Content - 01 (10 Options)',
      {
        force: true,
      }
    );

    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 5
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 6
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 7
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 8
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 9
    cy.get('[data-cy="addOptionMultipleChoice"]').click({ force: true }); // 10

    cy.get('[data-cy="questionOptionsInput"')
      .should('have.length', 10)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          if (index === 6) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            cy.get(`[data-cy="Order${index + 1}"]`).type("5");
          }
          if (index === 8) {
            cy.get(`[data-cy="Switch${index + 1}"]`).check({ force: true });
            cy.get(`[data-cy="Order${index + 1}"]`).type("10");
          }
          cy.get(`[data-cy="Option${index + 1}"]`).type('Option10 ' + index);
        });
      });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01 (10 Options)',
      'Cypress Question Example - Content - 01 (10 Options)',
      'Option10 ',
      [6,8],
        [5,10]
    );
  });

  it('Creates a question and tries to exploit order', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
        '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01 (2 Options)', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
        'Cypress Question Example - Content - 01 (2 Options)',
        {
          force: true,
        }
    );

    cy.get('[data-cy="questionOptionsInput"').should('have.length', 4);

    cy.get(`[data-cy="Option1"]`).type('Option2 0');
    cy.get(`[data-cy="Switch1"]`).check({ force: true });

    cy.get(`[data-cy="Option2"]`).type('Option2 1');
    cy.get(`[data-cy="Switch2"]`).check({ force: true });
    cy.get(`[data-cy="Order2"]`).type("2");
    cy.get(`[data-cy="Switch2"]`).uncheck({ force: true });

    cy.get(`[data-cy="Delete4"]`).click({ force: true });
    cy.get(`[data-cy="Delete3"]`).click({ force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
        'Cypress Question Example - 01 (2 Options)',
        'Cypress Question Example - Content - 01 (2 Options)',
        'Option2 ',
        [0],
        [null]
    );
  });

  it('Creates a question with correct options with no order', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
        .parent()
        .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get(
        '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01 (2 Options)', { force: true });
    cy.get('[data-cy="questionQuestionTextArea"]').type(
        'Cypress Question Example - Content - 01 (2 Options)',
        {
          force: true,
        }
    );

    cy.get('[data-cy="questionOptionsInput"').should('have.length', 4);

    cy.get(`[data-cy="Option1"]`).type('Option2 0');
    cy.get(`[data-cy="Switch1"]`).check({ force: true });

    cy.get(`[data-cy="Option2"]`).type('Option2 1');
    cy.get(`[data-cy="Switch2"]`).check({ force: true });

    cy.get(`[data-cy="Delete4"]`).click({ force: true });
    cy.get(`[data-cy="Delete3"]`).click({ force: true });

    cy.route('POST', '/courses/*/questions/').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
        .first()
        .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
        'Cypress Question Example - 01 (2 Options)',
        'Cypress Question Example - Content - 01 (2 Options)',
        'Option2 ',
        [0,1],
        [null,null]
    );
  });

});
