export class OrderItem {
  constructor(private id: string, private name: string, private description: string, private price: number, private quantity: number = 0) {};
  getId(): string { return this.id; }
  getName() { return this.name; }
  getPrice() { return this.price; }
  getDescription() { return this.description; }
  getQuantity(): number { return this.quantity; }

  increaseQuantity(quantity: number = 1): void {
    this.quantity += quantity;
  }
}
