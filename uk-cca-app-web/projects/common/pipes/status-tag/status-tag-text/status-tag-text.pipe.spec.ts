import { TestBed } from '@angular/core/testing';

import { TASK_STATUS_TAG_MAP } from '../status-tag.providers';
import { StatusTagTextPipe } from './status-tag-text.pipe';

describe('StatusTagTextPipe', () => {
  let pipe: StatusTagTextPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StatusTagTextPipe,
        { provide: TASK_STATUS_TAG_MAP, useValue: { COMPLETED: { text: 'COMPLETED', color: 'blue' } } },
      ],
    });

    pipe = TestBed.inject(StatusTagTextPipe);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform status correctly', () => {
    const result = pipe.transform('COMPLETED');
    expect(result).toBe('COMPLETED');
  });
});
