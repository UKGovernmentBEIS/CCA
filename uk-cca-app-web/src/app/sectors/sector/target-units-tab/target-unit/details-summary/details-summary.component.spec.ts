import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockTargetUnitAccountDetails, mockUnderlyingAgreementDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveTargetUnitStore } from '../../active-target-unit.store';
import { DetailsSummaryComponent } from './details-summary.component';

describe('DetailsSummaryComponent', () => {
  let component: DetailsSummaryComponent;
  let componentRef: ComponentRef<DetailsSummaryComponent>;
  let fixture: ComponentFixture<DetailsSummaryComponent>;
  let store: ActiveTargetUnitStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsSummaryComponent],
      providers: [
        ActiveTargetUnitStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveTargetUnitStore);
    store.setState({
      targetUnitAccountDetails: mockTargetUnitAccountDetails,
      underlyingAgreementDetails: mockUnderlyingAgreementDetails,
    });

    fixture = TestBed.createComponent(DetailsSummaryComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    componentRef.setInput('isEditableData', { isEditable: true, isFinancialIndependenceEditable: true });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
