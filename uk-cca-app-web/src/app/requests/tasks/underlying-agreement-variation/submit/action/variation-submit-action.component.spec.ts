import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import { VariationSubmitActionComponent } from './variation-submit-action.component';

describe('VariationSubmitActionComponent', () => {
  let component: VariationSubmitActionComponent;
  let fixture: ComponentFixture<VariationSubmitActionComponent>;
  let router: Router;

  const activatedRoute = new ActivatedRouteStub();

  const mockTasksApiService: MockType<TasksApiService> = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VariationSubmitActionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and text', () => {
    const heading = getByText('Send variation application to regulator');
    expect(heading).toBeTruthy();
  });

  it('should submit and navigate to confirmation page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const apiServiceSpy = jest.spyOn(mockTasksApiService, 'saveRequestTaskAction');
    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button.govuk-button');
    submitButton.click();
    fixture.detectChanges();

    expect(apiServiceSpy).toHaveBeenCalledTimes(1);
    expect(apiServiceSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
      }),
    );
    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], expect.anything());
  });
});
