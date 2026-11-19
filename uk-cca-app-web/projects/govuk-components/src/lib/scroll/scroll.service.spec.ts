import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';

import { ScrollService } from './scroll.service';

describe('FormService', () => {
  let service: ScrollService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    });
    service = TestBed.inject(ScrollService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
