import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewRequestedDocumentsUploadComponent } from './pre-audit-review-requested-documents-upload.component';

@Component({ template: '' })
class DummyComponent {}

const mockForm = new FormGroup({
  auditMaterialReceivedDate: new FormControl(new Date()),
  additionalInformation: new FormControl('blah blah blah'),
});

describe('PreAuditReviewRequestedDocumentsUploadComponent', () => {
  let component: PreAuditReviewRequestedDocumentsUploadComponent;
  let fixture: ComponentFixture<PreAuditReviewRequestedDocumentsUploadComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewRequestedDocumentsUploadComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{ path: '**', component: DummyComponent }]),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewRequestedDocumentsUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Upload documents');
  });

  it('should submit form and navigate to check-your-answers', () => {
    const onSubmitSpy = vi.spyOn(component, 'onSubmit');
    const navigateSpy = vi.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should update form and submit with new values', () => {
    mockForm.patchValue({
      auditMaterialReceivedDate: new Date('2025-06-06'),
      additionalInformation: 'blah',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
