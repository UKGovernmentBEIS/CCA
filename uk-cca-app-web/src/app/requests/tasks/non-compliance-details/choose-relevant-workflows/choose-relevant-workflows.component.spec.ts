import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { ChooseRelevantWorkflowsComponent } from './choose-relevant-workflows.component';

describe('ChooseRelevantWorkflowsComponent', () => {
  let component: ChooseRelevantWorkflowsComponent;
  let fixture: ComponentFixture<ChooseRelevantWorkflowsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn() },
      pathFromRoot: [],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChooseRelevantWorkflowsComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockNonComplianceDetailsState);

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(ChooseRelevantWorkflowsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    mockTasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and call saveRequestTaskAction', () => {
    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
  });

  it('should navigate after submit to check your answers when wizard is already completed', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route as any });
  });

  it('should submit with relevantWorkflows array filtering out null values', () => {
    component.workflows.clear();
    component.onAddItem();
    component.onAddItem();
    component.onAddItem();

    component.workflows.at(0).setValue('WF-001');
    component.workflows.at(1).setValue(null);
    component.workflows.at(2).setValue('WF-003');

    component.onSubmit();

    const dto = mockTasksApiService.saveRequestTaskAction.mock.calls.at(-1)?.[0];

    expect(dto.requestTaskActionPayload.nonComplianceDetails.relevantWorkflows).toEqual(['WF-001', 'WF-003']);
  });

  it('should add a workflow row on onAddItem', () => {
    const initialLength = component.workflows.length;

    component.onAddItem();

    expect(component.workflows.length).toBe(initialLength + 1);
  });

  it('should format workflow option labels with the workflow id and name', () => {
    component.workflows.clear();
    component.onAddItem();

    const options = component.getOptionsForRow(0);

    expect(options).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ value: 'WF-001', text: 'WF-001 - Workflow 1' }),
        expect.objectContaining({ value: 'WF-002', text: 'WF-002 - Workflow 2' }),
      ]),
    );
  });

  it('should show Add another item when at least one workflow row exists', () => {
    const addButton = fixture.nativeElement.querySelector('button.govuk-button--secondary') as HTMLButtonElement;

    expect(addButton.textContent?.trim()).toBe('Add another item');

    component.workflows.clear();
    fixture.detectChanges();

    expect(addButton.textContent?.trim()).toBe('Add Item');
  });

  it('should disable Add item button when all workflow options are selected', () => {
    component.onAddItem();
    component.workflows.at(2).setValue('WF-003');
    fixture.detectChanges();

    const addButton = fixture.nativeElement.querySelector('button.govuk-button--secondary') as HTMLButtonElement;

    expect(addButton.disabled).toBe(true);
  });

  it('should not add workflow row when all workflow options are selected', () => {
    component.onAddItem();
    component.workflows.at(2).setValue('WF-003');
    const initialLength = component.workflows.length;

    component.onAddItem();

    expect(component.workflows.length).toBe(initialLength);
  });

  it('should delete a workflow row on onDeleteItem', () => {
    component.workflows.clear();
    component.onAddItem();
    component.onAddItem();
    component.workflows.at(0).setValue('WF-001');
    component.workflows.at(1).setValue('WF-002');

    component.onDeleteItem('WF-001', 0);

    expect(component.workflows.length).toBe(1);
    expect(component.workflows.at(0).value).toBe('WF-002');
  });

  it('should filter already selected workflows in getOptionsForRow', () => {
    component.workflows.clear();
    component.onAddItem();
    component.onAddItem();

    component.workflows.at(0).setValue('WF-001');
    component.workflows.at(1).setValue(null);

    const optionsForSecondRow = component.getOptionsForRow(1);
    const optionValues = optionsForSecondRow.map((option) => option.value);

    expect(optionValues).not.toContain('WF-001');
    expect(optionValues).toEqual(expect.arrayContaining(['WF-002', 'WF-003']));
  });
});
