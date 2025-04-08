import { MarkingOfFacilitiesStatusPipe } from './marking-of-facilities-status.pipe';

describe('MarkingOfFacilitiesStatusPipe', () => {
  const pipe = new MarkingOfFacilitiesStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct marking of facilities status according to amounts', () => {
    expect(pipe.transform(0, 0)).toEqual('Cancelled');
    expect(pipe.transform(1, 0)).toEqual('In progress');
    expect(pipe.transform(1, 2)).toEqual('Completed');
    expect(pipe.transform(2, 2)).toEqual('Completed');
  });
});
