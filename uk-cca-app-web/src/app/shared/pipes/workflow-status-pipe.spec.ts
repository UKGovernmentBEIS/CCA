import { WorkflowStatusPipe } from '@shared/pipes';

describe('WorkflowStatusPipe', () => {
  let pipe: WorkflowStatusPipe;

  beforeEach(() => {
    pipe = new WorkflowStatusPipe();
  });

  it('should throw an error if the value is empty', () => {
    expect(() => pipe.transform('')).toThrow(new Error('invalid workflow status. received '));
  });

  it('should throw an error if the value is null', () => {
    expect(() => pipe.transform(null)).toThrow(new Error('invalid workflow status. received null'));
  });

  it('should throw an error if the value is undefined', () => {
    expect(() => pipe.transform(undefined)).toThrow(new Error('invalid workflow status. received undefined'));
  });

  it('should return the correct value for valid statuses', () => {
    expect(pipe.transform('APPROVED')).toBe('Approved');
    expect(pipe.transform('CANCELLED')).toBe('Cancelled');
    expect(pipe.transform('COMPLETED')).toBe('Completed');
    expect(pipe.transform('IN_PROGRESS')).toBe('In progress');
    expect(pipe.transform('REJECTED')).toBe('Rejected');
    expect(pipe.transform('WITHDRAWN')).toBe('Withdrawn');
  });
});
