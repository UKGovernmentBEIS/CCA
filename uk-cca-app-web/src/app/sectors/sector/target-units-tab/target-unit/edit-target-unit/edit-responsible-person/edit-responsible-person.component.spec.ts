import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { CountryService } from '@core/services/country.service';
import { ActivatedRouteStub, BasePage, mockClass } from '@netz/common/testing';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { CountryServiceStub } from 'src/testing/country.service.stub';

import { mockTargetUnitAccountDetails } from '../../../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { EditResponsiblePersonComponent } from './edit-responsible-person.component';

describe('EditResponsiblePersonComponent', () => {
  let component: EditResponsiblePersonComponent;
  let fixture: ComponentFixture<EditResponsiblePersonComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;
  let store: ActiveTargetUnitStore;
  const updateTargetUnitAccountService = mockClass(UpdateTargetUnitAccountService);
  updateTargetUnitAccountService.updateTargetUnitAccountResponsiblePerson.mockReturnValue(of({}));

  class Page extends BasePage<EditResponsiblePersonComponent> {
    get jobTitleValue() {
      return this.getInputValue('#jobTitle');
    }
    set jobTitleValue(value: string) {
      this.setInputValue('#jobTitle', value);
    }

    get phoneNumberValue() {
      return this.getInputValue('#phoneNumber');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub({ targetUnitId: '1' });
    await TestBed.configureTestingModule({
      imports: [EditResponsiblePersonComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ActiveTargetUnitStore,
        { provide: UpdateTargetUnitAccountService, useValue: updateTargetUnitAccountService },
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditResponsiblePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pre-populated form', () => {
    expect(page.jobTitleValue).toEqual(mockTargetUnitAccountDetails.responsiblePerson.jobTitle);
    expect(page.phoneNumberValue).toEqual(mockTargetUnitAccountDetails.responsiblePerson.phoneNumber.number);
  });

  it('should edit details and submit form', () => {
    page.jobTitleValue = 'Changed';

    page.submitButton.click();
    fixture.detectChanges();

    expect(updateTargetUnitAccountService.updateTargetUnitAccountResponsiblePerson).toHaveBeenCalledTimes(1);
    expect(updateTargetUnitAccountService.updateTargetUnitAccountResponsiblePerson).toHaveBeenCalledWith(1, {
      phoneNumber: mockTargetUnitAccountDetails.responsiblePerson.phoneNumber,
      jobTitle: 'Changed',
    });
  });
});
