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

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { AuditDetailsComponent } from './audit-details.component';

@Component({ template: '' })
class DummyComponent {}

describe('AuditDetailsComponent', () => {
  let component: AuditDetailsComponent;
  let fixture: ComponentFixture<AuditDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const mockForm = new FormGroup({
    auditTechnique: new FormControl('DESK_BASED_INTERVIEW'),
    auditDate: new FormControl(new Date()),
    comments: new FormControl('blah blah blah'),
    finalAuditReportDate: new FormControl(new Date()),
    auditDocuments: new FormControl([]),
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditDetailsComponent],
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
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(AuditDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Details of the audit');
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
      ...mockForm.value,
      comments: 'updated comments',
      auditDate: new Date('2025-05-15'),
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
