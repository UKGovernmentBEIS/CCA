import { ItemNamePipe } from './item-name.pipe';

describe('ItemNamePipe', () => {
  const pipe = new ItemNamePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform(null)).toBeNull();
  });
});
