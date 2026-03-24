import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NonComplianceCompleteTaskButtonComponent } from './complete-task-button.component';

describe('NonComplianceCompleteTaskButtonComponent', () => {
  let component: NonComplianceCompleteTaskButtonComponent;
  let fixture: ComponentFixture<NonComplianceCompleteTaskButtonComponent>;
  let router: Router;

  const route = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn() },
      pathFromRoot: [],
    },
  };

  const isEditableSignal = signal(true);
  const sectionsCompletedSignal = signal<Record<string, string>>({
    'provide-details': TaskItemStatus.COMPLETED,
  });

  const mockStore = {
    select: jest.fn().mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectIsEditable) {
        return isEditableSignal;
      }

      if (selector === nonComplianceDetailsQuery.selectSectionsCompleted) {
        return sectionsCompletedSignal;
      }

      return signal(undefined);
    }),
  };

  beforeEach(async () => {
    isEditableSignal.set(true);
    sectionsCompletedSignal.set({ 'provide-details': TaskItemStatus.COMPLETED });

    await TestBed.configureTestingModule({
      imports: [NonComplianceCompleteTaskButtonComponent],
      providers: [
        { provide: RequestTaskStore, useValue: mockStore },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NonComplianceCompleteTaskButtonComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show button when subtask COMPLETED and isEditable', () => {
    isEditableSignal.set(true);
    sectionsCompletedSignal.set({ 'provide-details': TaskItemStatus.COMPLETED });

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Complete task');
  });

  it('should hide button when subtask is not COMPLETED', () => {
    sectionsCompletedSignal.set({ 'provide-details': TaskItemStatus.IN_PROGRESS });

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button');
    expect(button).toBeFalsy();
  });

  it('should navigate to complete-task route on click', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onCompleteTask();

    expect(navigateSpy).toHaveBeenCalledWith(['non-compliance', 'complete-task'], { relativeTo: route as any });
  });
});
