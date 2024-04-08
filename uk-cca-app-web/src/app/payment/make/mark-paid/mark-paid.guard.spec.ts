import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MarkPaidGuard } from './mark-paid.guard';

describe('MarkPaidGuard', () => {
  let guard: MarkPaidGuard;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(MarkPaidGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
