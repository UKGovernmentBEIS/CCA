import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@netz/common/testing';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { mockSectorScheme, mockTargetUnitAccountDetails } from '../../../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { EditDetailsComponent } from './edit-details.component';

describe('EditDetailsComponent', () => {
  let component: EditDetailsComponent;
  let fixture: ComponentFixture<EditDetailsComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;
  let store: ActiveTargetUnitStore;

  const updateTargetUnitAccountService = mockClass(UpdateTargetUnitAccountService);
  updateTargetUnitAccountService.updateTargetUnitAccountSicCode.mockReturnValue(of({}));

  class Page extends BasePage<EditDetailsComponent> {
    get sicCodeValue() {
      return this.getInputValue('#sicCode');
    }
    set sicCodeValue(value: string) {
      this.setInputValue('#sicCode', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub({ id: '1' }, null, {
      subSectorScheme: mockSectorScheme,
    });
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, EditDetailsComponent],
      providers: [
        ActiveTargetUnitStore,
        { provide: UpdateTargetUnitAccountService, useValue: updateTargetUnitAccountService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });
    fixture = TestBed.createComponent(EditDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pre-populated form', () => {
    expect(page.sicCodeValue).toEqual(mockTargetUnitAccountDetails.sicCode);
  });

  it('should edit details and submit form', () => {
    const spy = jest.spyOn(updateTargetUnitAccountService, 'updateTargetUnitAccountSicCode');
    page.sicCodeValue = 'Changed';

    page.submitButton.click();
    fixture.detectChanges();
    expect(spy).toHaveBeenCalledTimes(1);
    expect(spy).toHaveBeenCalledWith(0, { sicCode: 'Changed' });
  });
});
