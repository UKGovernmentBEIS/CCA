import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';
import { click, getByLabelText, getByText } from '@testing';

import { UpdateTargetUnitAccountService } from 'cca-api';

import { mockTargetUnitAccountDetails } from '../../../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { EditFinancialIndependenceComponent } from './edit-financial-independence.component';

describe('EditFinancialIndependenceComponent', () => {
  let component: EditFinancialIndependenceComponent;
  let fixture: ComponentFixture<EditFinancialIndependenceComponent>;
  let store: ActiveTargetUnitStore;

  const updateTargetUnitAccountService = mockClass(UpdateTargetUnitAccountService);
  updateTargetUnitAccountService.updateTargetUnitAccountFinancialIndependenceStatusCode.mockReturnValue(of({}));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditFinancialIndependenceComponent],
      providers: [
        ActiveTargetUnitStore,
        { provide: UpdateTargetUnitAccountService, useValue: updateTargetUnitAccountService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: '1' }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });
    fixture = TestBed.createComponent(EditFinancialIndependenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have the correct value prepopulated', () => {
    const select = getByLabelText(
      'Select whether this target unit will be financially independent.',
    ) as HTMLSelectElement;
    expect(select.value).toBe('0: FINANCIALLY_INDEPENDENT');
  });

  it('should submit the form', () => {
    click(getByText('Confirm and complete'));

    expect(updateTargetUnitAccountService.updateTargetUnitAccountFinancialIndependenceStatusCode).toHaveBeenCalledTimes(
      1,
    );

    expect(updateTargetUnitAccountService.updateTargetUnitAccountFinancialIndependenceStatusCode).toHaveBeenCalledWith(
      1,
      { financialIndependenceStatus: 'FINANCIALLY_INDEPENDENT' },
    );
  });
});
