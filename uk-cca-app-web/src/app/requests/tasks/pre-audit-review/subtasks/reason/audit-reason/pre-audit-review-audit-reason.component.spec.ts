import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';
import { ActivatedRouteStub } from 'projects/common/testing';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewAuditReasonComponent } from './pre-audit-review-audit-reason.component';

const mockForm = new FormGroup({
  reasonsForAudit: new FormControl(['ELIGIBILITY', 'SEVENTY_RULE_EVALUATION']),
  comment: new FormControl('blah blah blah'),
});

describe('PreAuditReviewAuditReasonComponent', () => {
  let component: PreAuditReviewAuditReasonComponent;
  let fixture: ComponentFixture<PreAuditReviewAuditReasonComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewAuditReasonComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewAuditReasonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Why should this facility be audited?');
  });

  it('should submit form and navigate to check-your-answers', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    const navigateSpy = jest.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should update form and submit with new values', () => {
    mockForm.patchValue({
      reasonsForAudit: ['BASE_YEAR_DATA', 'REPORTING_DATA'],
      comment: 'blah',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
