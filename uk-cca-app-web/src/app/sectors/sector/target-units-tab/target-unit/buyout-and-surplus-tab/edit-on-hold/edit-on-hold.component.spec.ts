import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';
import { EditOnHoldComponent } from './edit-on-hold.component';

describe('EditOnHoldComponent', () => {
  let fixture: ComponentFixture<EditOnHoldComponent>;
  let component: EditOnHoldComponent;
  let store: BuyoutAndSurplusTabStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, EditOnHoldComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
        BuyoutAndSurplusTabStore,
        ActiveTargetUnitStore,
      ],
    }).compileComponents();

    TestBed.inject(ActiveTargetUnitStore).setState({
      subsectorAssociation: null,
      targetUnitAccountDetails: { businessId: 'ADS_1-T00001' },
      underlyingAgreementDetails: null,
    });

    fixture = TestBed.createComponent(EditOnHoldComponent);
    store = TestBed.inject(BuyoutAndSurplusTabStore);
    store.setState({
      surplusInfo: {
        excluded: true,
      },
    });

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the target unit id in the legend', () => {
    expect(fixture.nativeElement.querySelector('legend').textContent.trim()).toEqual(
      'Do you want to put the account ADS_1-T00001 on hold for the next buy-out and surplus batch?',
    );
  });

  it('should pre-populate form value with false', () => {
    store.setState({
      surplusInfo: {},
    });

    fixture.detectChanges();
    component = fixture.componentInstance;

    expect(component.form.valid).toBe(true);
    expect(component.form.value.changeOnHold).toEqual(false);
  });
});
