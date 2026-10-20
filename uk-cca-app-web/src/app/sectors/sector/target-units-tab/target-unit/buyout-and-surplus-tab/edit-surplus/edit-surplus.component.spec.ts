import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';
import { EditSurplusComponent } from './edit-surplus.component';

describe('EditSurplusHistoryComponent', () => {
  let component: EditSurplusComponent;
  let fixture: ComponentFixture<EditSurplusComponent>;
  let store: BuyoutAndSurplusTabStore;

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => {
          if (key === 'targetUnitId') return '1';
          if (key === 'targetPeriodType') return 'TP6';
          return null;
        },
      },
      data: {
        targetUnit: { targetUnitAccountDetails: { businessId: 'ADS_1-T00001' } },
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSurplusComponent],
      providers: [
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        provideHttpClientTesting(),
        BuyoutAndSurplusTabStore,
      ],
    }).compileComponents();

    store = TestBed.inject(BuyoutAndSurplusTabStore);
    store.setState({
      surplusInfo: {
        excluded: true,
        surplusGainedDTOList: [
          {
            surplusGained: '1',
            targetPeriod: 'TP6',
            hasHistory: true,
          },
        ],
      },
    });
    fixture = TestBed.createComponent(EditSurplusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should pull currentSurplus from the store', () => {
    expect(component.currentSurplus).toBe(1);
  });

  it('should invalidate the form when surplus equals currentSurplus', () => {
    component.form.controls['surplus'].setValue(1);
    component.form.controls['comments'].setValue('abc');
    expect(component.form.controls['surplus'].hasError('sameAsCurrent')).toBe(true);
    expect(component.form.invalid).toBe(true);
  });

  it('should validate the form when surplus differs from currentSurplus', () => {
    component.form.controls['surplus'].setValue(5);
    component.form.controls['comments'].setValue('abc');
    expect(component.form.controls['surplus'].valid).toBe(true);
    expect(component.form.controls['comments'].valid).toBe(true);
    expect(component.form.valid).toBe(true);
  });
});
