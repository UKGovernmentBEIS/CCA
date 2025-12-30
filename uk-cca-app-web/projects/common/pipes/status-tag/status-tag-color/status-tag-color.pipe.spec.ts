import { TestBed } from '@angular/core/testing';

import { TASK_STATUS_TAG_MAP } from '../status-tag.providers';
import { StatusTagColorPipe } from './status-tag-color.pipe';

describe('StatusTagColorPipe', () => {
  let pipe: StatusTagColorPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StatusTagColorPipe,
        { provide: TASK_STATUS_TAG_MAP, useValue: { COMPLETED: { text: 'COMPLETED', color: 'blue' } } },
      ],
    });

    pipe = TestBed.inject(StatusTagColorPipe);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform status correctly', () => {
    const result = pipe.transform('COMPLETED');
    expect(result).toBe('blue');
  });
});
