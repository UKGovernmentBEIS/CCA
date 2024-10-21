import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import FacilityCheckAnswersComponent from './facility-check-answers.component';

describe('FacilityCheckAnswersComponent', () => {
  let component: FacilityCheckAnswersComponent;
  let fixture: ComponentFixture<FacilityCheckAnswersComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_1-F00001',
      },
      paramMap: {
        get: jest.fn().mockReturnValue(mockRequestTaskItemDTO.requestTask.id),
      },
      pathFromRoot: [],
    },
  };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityCheckAnswersComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get sections() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityCheckAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(FacilityCheckAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Check your answers');

    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Facility Details',
      'Facility contact details',
      'CCA eligibility details and authorisation',
      'Extent of the facility',
      'Apply the 70% rule',
    ]);

    expect(page.summaryListValues).toEqual([
      ['Site name', 'Facility 1'],
      ['Facility code', 'ADS_1-F00001'],
      ['Is this facility covered by UK ETS?', 'No'],
      ['UK ETS Installation Identifier', ''],
      ['Application reason', 'New agreement'],
      ['Previous facility ID', ''],
      ['Facility address', `Facility Line1  Facility Line2  Facility City  Facility 14  GR`],
      ['First name', 'FacilityFirst'],
      ['Last name', 'FacilityLast'],
      ['Email address', 'facility@email.com'],
      [
        'Contact address',
        `Facility Contact Line1  Facility Contact Line2  Facility Contact City  Facility Contact 14  GR`,
      ],
      ['Phone number', 'UK (44) 1234567890'],
      ['Is the facility adjacent to or connected to an existing CCA facility?', 'Yes'],
      ['Facility ID of adjacent facility', 'ADS_1-F11111'],
      ['Agreement type - eligible under', 'Environmental Permitting Regulations (EPR)'],
      [
        'Do you hold a current Environmental Permitting Regulations (EPR) authorisation for any activity being carried out in the facility?',
        'Yes',
      ],
      ['Authorisation number', 'authorisation'],
      ['Regulator name', 'Environment Agency (England)'],
      ['Attach a copy of the permit', ''],
      ['Manufacturing process description', 'manufacturingProcessFile.xlsx'],
      ['Process flow maps', 'processFlowFile.xlsx'],
      ['Annotated site plans', 'annotatedSitePlansFile.xlsx'],
      ['Eligible process description', 'eligibleProcessFile.xlsx'],
      ['Are any directly associated activities claimed?', 'Yes'],
      ['Directly associated activities description', 'activitiesDescriptionFile.xlsx'],
      ['Energy consumed in the installation', '50 %'],
      ['Energy consumed in relation to 3/7ths provision', '40 %'],
      ['Energy consumed in eligible facility', '70 %'],
      ['Sub-metered start date', ''],
      ['Evidence', 'evidenceFile.xlsx'],
    ]);
  });

  it('should edit facility extend and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'submitSubtask');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith('facilities');
  });
});
