import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'cca-api';

import { PaymentGuard } from './payment.guard';

describe('PaymentGuard', () => {
  let guard: PaymentGuard;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(PaymentGuard);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
