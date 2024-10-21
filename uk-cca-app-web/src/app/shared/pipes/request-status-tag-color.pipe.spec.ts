import { RequestStatusTagColorPipe } from './request-status-tag-color.pipe';

describe('RequestStatusTagColorPipe', () => {
  let pipe: RequestStatusTagColorPipe;

  beforeEach(() => {
    pipe = new RequestStatusTagColorPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return green for approved status', () => {
    expect(pipe.transform('Approved')).toBe('green');
    expect(pipe.transform('approved')).toBe('green');
    expect(pipe.transform('APPROVED')).toBe('green');
  });

  it('should return grey for cancelled status', () => {
    expect(pipe.transform('Cancelled')).toBe('grey');
    expect(pipe.transform('cancelled')).toBe('grey');
    expect(pipe.transform('CANCELLED')).toBe('grey');
  });

  it('should return green for completed status', () => {
    expect(pipe.transform('Completed')).toBe('green');
    expect(pipe.transform('completed')).toBe('green');
    expect(pipe.transform('COMPLETED')).toBe('green');
  });

  it('should return blue for in progress status', () => {
    expect(pipe.transform('IN_PROGRESS')).toBe('blue');
    expect(pipe.transform('In_Progress')).toBe('blue');
  });

  it('should return red for rejected status', () => {
    expect(pipe.transform('Rejected')).toBe('red');
    expect(pipe.transform('rejected')).toBe('red');
    expect(pipe.transform('REJECTED')).toBe('red');
  });

  it('should return red for withdrawn status', () => {
    expect(pipe.transform('Withdrawn')).toBe('red');
    expect(pipe.transform('withdrawn')).toBe('red');
    expect(pipe.transform('WITHDRAWN')).toBe('red');
  });

  it('should throw an error if the status is empty', () => {
    expect(() => pipe.transform('')).toThrow(new Error('invalid request status. received '));
  });

  it('should throw an error if the status is null', () => {
    expect(() => pipe.transform(null)).toThrow(new Error('invalid request status. received null'));
  });

  it('should throw an error if the status is undefined', () => {
    expect(() => pipe.transform(undefined)).toThrow(new Error('invalid request status. received undefined'));
  });
});
