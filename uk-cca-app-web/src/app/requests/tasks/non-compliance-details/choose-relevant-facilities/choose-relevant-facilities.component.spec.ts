import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { ChooseRelevantFacilitiesComponent } from './choose-relevant-facilities.component';

describe('ChooseRelevantFacilitiesComponent', () => {
  let component: ChooseRelevantFacilitiesComponent;
  let fixture: ComponentFixture<ChooseRelevantFacilitiesComponent>;
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
      imports: [ChooseRelevantFacilitiesComponent],
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

    fixture = TestBed.createComponent(ChooseRelevantFacilitiesComponent);
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

  it('should submit with relevantFacilities as WorkflowFacilityDTO[]', () => {
    component.facilities.clear();
    component.onAddFacility();
    component.onAddHistoricalFacility();
    component.onAddFacility();

    component.facilities.at(0).controls.facilityBusinessId.setValue(' FAC-002 ');
    component.facilities.at(1).controls.facilityBusinessId.setValue('HIST-777');
    component.facilities.at(2).controls.facilityBusinessId.setValue('   ');

    component.onSubmit();

    const dto = mockTasksApiService.saveRequestTaskAction.mock.calls.at(-1)?.[0];

    expect(dto.requestTaskActionPayload.nonComplianceDetails.relevantFacilities).toEqual([
      { facilityBusinessId: 'FAC-002', isHistorical: false },
      { facilityBusinessId: 'HIST-777', isHistorical: true },
    ]);
  });

  it('should add facility row with isHistorical false on onAddFacility', () => {
    const initialLength = component.facilities.length;

    component.onAddFacility();

    expect(component.facilities.length).toBe(initialLength + 1);
    expect(component.facilities.at(initialLength).controls.isHistorical.value).toBe(false);
  });

  it('should disable Add facility button when all facility options are selected', () => {
    component.onAddFacility();
    component.facilities.at(2).controls.facilityBusinessId.setValue('FAC-002');
    fixture.detectChanges();

    const [addFacilityButton, addHistoricalFacilityButton] = Array.from(
      fixture.nativeElement.querySelectorAll('button.govuk-button--secondary'),
    ) as HTMLButtonElement[];

    expect(addFacilityButton.disabled).toBe(true);
    expect(addHistoricalFacilityButton.disabled).toBe(false);
  });

  it('should not add facility row when all facility options are selected', () => {
    component.onAddFacility();
    component.facilities.at(2).controls.facilityBusinessId.setValue('FAC-002');
    const initialLength = component.facilities.length;

    component.onAddFacility();

    expect(component.facilities.length).toBe(initialLength);
  });

  it('should add historical facility row with isHistorical true on onAddHistoricalFacility', () => {
    const initialLength = component.facilities.length;

    component.onAddHistoricalFacility();

    expect(component.facilities.length).toBe(initialLength + 1);
    expect(component.facilities.at(initialLength).controls.isHistorical.value).toBe(true);
  });

  it('should delete a facility row on onDeleteItem', () => {
    component.facilities.clear();
    component.onAddFacility();
    component.onAddHistoricalFacility();

    component.facilities.at(0).controls.facilityBusinessId.setValue('FAC-001');
    component.facilities.at(1).controls.facilityBusinessId.setValue('HIST-001');

    component.onDeleteItem('FAC-001', false, 0);

    expect(component.facilities.length).toBe(1);
    expect(component.facilities.at(0).controls.facilityBusinessId.value).toBe('HIST-001');
    expect(component.facilities.at(0).controls.isHistorical.value).toBe(true);
  });

  it('should filter already selected facilities in getOptionsForRow', () => {
    component.facilities.clear();
    component.onAddFacility();
    component.onAddFacility();

    component.facilities.at(0).controls.facilityBusinessId.setValue('FAC-001');
    component.facilities.at(1).controls.facilityBusinessId.setValue(null);

    const optionsForSecondRow = component.getOptionsForRow(1);
    const optionValues = optionsForSecondRow.map((option) => option.value);

    expect(optionValues).not.toContain('FAC-001');
    expect(optionValues).toContain('FAC-002');
  });
});
