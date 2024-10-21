import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO, mockTargetUnitDetails, mockUnderlyingAgreement } from '../../../testing';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import { FacilityContactDetailsComponent } from './facility-contact-details.component';

describe('FacilityContactDetailsComponent', () => {
  let component: FacilityContactDetailsComponent;
  let fixture: ComponentFixture<FacilityContactDetailsComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = { snapshot: { params: { facilityId: 'ADS_1-F00001' }, pathFromRoot: [] } };
  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<FacilityContactDetailsComponent> {
    get sameContact() {
      return this.query<HTMLInputElement>('input[name$="sameContact"]');
    }
    get firstName() {
      return this.getInputValue('#firstName');
    }
    get lastName() {
      return this.getInputValue('#lastName');
    }
    get email() {
      return this.getInputValue('#email');
    }
    get sameAddress() {
      return this.query<HTMLInputElement>('input[name$="sameAddress"]');
    }
    get line1() {
      return this.getInputValue('#address.line1');
    }
    get line2() {
      return this.getInputValue('#address.line2');
    }
    get city() {
      return this.getInputValue('#address.city');
    }
    get county() {
      return this.getInputValue('#address.county');
    }
    get postcode() {
      return this.getInputValue('#address.postcode');
    }
    get country() {
      return this.getInputValue('#address.country');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityContactDetailsComponent],
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

    fixture = TestBed.createComponent(FacilityContactDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.sameContact.checked).toBeFalsy();
    expect(page.firstName).toEqual('FacilityFirst');
    expect(page.lastName).toEqual('FacilityLast');
    expect(page.email).toEqual('facility@email.com');

    expect(page.sameAddress.checked).toBeFalsy();
    expect(page.line1).toEqual('Facility Contact Line1');
    expect(page.line2).toEqual('Facility Contact Line2');
    expect(page.city).toEqual('Facility Contact City');
    expect(page.county).toEqual('');
    expect(page.postcode).toEqual('Facility Contact 14');
    expect(page.country).toEqual('GR');
  });

  it('should edit contact and save', () => {
    const taskServiceSpy = jest.spyOn(unaTaskService, 'saveSubtask');

    page.sameContact.click();
    page.sameAddress.click();
    page.submitButton.click();
    fixture.detectChanges();
    const facilityId = 'ADS_1-F00001';
    const facility = mockUnderlyingAgreement.facilities.find((f) => f.facilityId === facilityId);
    expect(taskServiceSpy).toHaveBeenCalledWith(FACILITIES_SUBTASK, FacilityWizardStep.CONTACT_DETAILS, route, {
      facility: {
        facilityId,
        facilityContact: {
          firstName: mockTargetUnitDetails.administrativeContactDetails.firstName,
          lastName: mockTargetUnitDetails.administrativeContactDetails.lastName,
          email: mockTargetUnitDetails.administrativeContactDetails.email,
          address: {
            ...facility.facilityDetails.facilityAddress,
            county: null,
          },
          phoneNumber: {
            countryCode: '44',
            number: '1234567890',
          },
        },
      },
    });
  });
});
