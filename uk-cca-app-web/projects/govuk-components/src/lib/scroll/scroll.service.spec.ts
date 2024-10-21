import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ScrollService } from './scroll.service';

describe('FormService', () => {
  let service: ScrollService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    service = TestBed.inject(ScrollService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
