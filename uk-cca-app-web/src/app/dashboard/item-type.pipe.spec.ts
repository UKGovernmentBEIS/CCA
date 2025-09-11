import { ItemTypePipe } from './item-type.pipe';

describe('ItemTypePipe', () => {
  let pipe: ItemTypePipe;

  beforeEach(() => (pipe = new ItemTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map request types to item types', () => {
    expect(pipe.transform(null)).toBeNull();
  });
});
