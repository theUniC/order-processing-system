export class AvailableItem {
  constructor(private id: string, private name: string, private assetPath: string, private price: number) {};
  getId(): string { return this.id; }
  getName(): string { return this.name; }
  getAssetPath(): string { return this.assetPath; }
  getPrice(): number { return this.price; }
}
