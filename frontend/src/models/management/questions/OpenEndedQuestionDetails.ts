import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionDetails extends QuestionDetails {
  maxCharacters: number = 0;
  proposedAnswer: string = '';

  constructor(jsonObj?: OpenEndedQuestionDetails) {
    super(QuestionTypes.OpenEnded);
    if (jsonObj) {
      this.maxCharacters = jsonObj.maxCharacters;
      this.proposedAnswer = jsonObj.proposedAnswer;
    }
  }

  setAsNew(): void {
    // TODO: Figure out what this is
  }
}
