import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { UnderlyingAgreementSubmitActionComponent } from './action.component';

describe('UnderlyingAgreementSubmitActionComponent', () => {
  let component: UnderlyingAgreementSubmitActionComponent;
  let fixture: ComponentFixture<UnderlyingAgreementSubmitActionComponent>;
  let router: Router;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(async () => {
    const requestTaskStore = {
      select: jest.fn().mockImplementation((selector) => {
        if (selector === requestTaskQuery.selectRequestTaskId) {
          return () => 123;
        }
        return () => ({});
      }),
    };

    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: RequestTaskStore, useValue: requestTaskStore },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementSubmitActionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and text', () => {
    const heading = screen.getByRole('heading', { name: 'Submit to regulator' });
    expect(heading).toBeInTheDocument();
    expect(
      screen.getByText('Your application will be sent directly to your Regulator (Environment Agency).'),
    ).toBeInTheDocument();
  });

  it('should submit and navigate to confirmation page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const tasksApiServiceSpy = jest.spyOn(tasksApiService, 'saveRequestTaskAction');
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and send'));

    expect(tasksApiServiceSpy).toHaveBeenCalledTimes(1);
    expect(tasksApiServiceSpy).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], expect.anything());
  });
});
