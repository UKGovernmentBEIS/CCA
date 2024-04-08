import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { MockType } from '@testing';

import { RequestActionsService } from 'cca-api';

import { PaymentActionGuard } from './payment-action.guard';

describe('PermitSurrenderActionGuard', () => {
  let guard: PaymentActionGuard;

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionById: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(PaymentActionGuard);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
