export default class Association {
  id: number | null = null;
  itemTwo: string = '';

  constructor(jsonObj?: Association) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.itemTwo = jsonObj.itemTwo;
    }
  }
}
