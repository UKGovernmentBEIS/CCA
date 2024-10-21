import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import { ManageFacilitiesSummaryComponent } from './manage-facilities-summary.component';

describe('ManageFacilitiesSummaryComponent', () => {
  let component: ManageFacilitiesSummaryComponent;
  let fixture: ComponentFixture<ManageFacilitiesSummaryComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<ManageFacilitiesSummaryComponent> {
    get facilitiesTable() {
      return this.queryAll<HTMLTableRowElement>('tr')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('td')) ?? []),
          ...(Array.from(row.querySelectorAll('th')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ManageFacilitiesSummaryComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: unaTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);
    store.setIsEditable(true);

    fixture = TestBed.createComponent(ManageFacilitiesSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show list values', () => {
    expect(page.facilitiesTable).toEqual([
      ['Name', 'Facility ID', 'Status', 'Actions'],
      ['Facility 1', 'ADS_1-F00001', 'Live', 'Edit Exclude'],
      ['Facility 2', 'ADS_1-F00002', 'Excluded', 'Edit Undo'],
      ['Name', 'Facility ID', 'Status', 'Actions'],
      ['Facility 1', 'ADS_1-F00001', 'Live', 'Edit Exclude'],
      ['Facility 2', 'ADS_1-F00002', 'Excluded', 'Edit Undo'],
      ['Name', 'Facility ID', 'Status', 'Actions'],
      ['Facility 1', 'ADS_1-F00001', 'Live', 'Edit Exclude'],
      ['Facility 2', 'ADS_1-F00002', 'Excluded', 'Edit Undo'],
    ]);
  });

  it('should submit', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'submitSubtask');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith('manageFacilities');
  });
});
