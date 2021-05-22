import Association from '@/models/management/Association';

export default class Item {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  connections: Association[] = [new Association(), new Association(), new Association()];

  constructor(jsonObj?: Item) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.connections = jsonObj.connections.map(
        (AssociationDto : Association) => {
            return new Association(AssociationDto);
        }
      );
    }
  }
}
