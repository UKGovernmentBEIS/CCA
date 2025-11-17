import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockTargetUnitAccountDetails, mockUnderlyingAgreementDetails } from '../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../active-target-unit.store';
import { TargetUnitComponent } from './target-unit.component';

describe('TargetUnitComponent', () => {
  let component: TargetUnitComponent;
  let fixture: ComponentFixture<TargetUnitComponent>;
  let store: ActiveTargetUnitStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitComponent],
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

    fixture = TestBed.createComponent(TargetUnitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
