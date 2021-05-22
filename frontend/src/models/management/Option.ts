export default class Option {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: boolean = false;
  order!: number | null;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.correct = jsonObj.correct;
      this.order = jsonObj.correct ? jsonObj.order : null;
    }
  }

  getOrderStr(): string {
    return this.order == null ? '' : '#' + this.order.toString();
  }
}
