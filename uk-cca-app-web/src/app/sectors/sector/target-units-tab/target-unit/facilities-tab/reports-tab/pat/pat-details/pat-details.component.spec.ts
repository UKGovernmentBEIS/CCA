import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { FacilityPATReportStore } from '../../../facility-pat-report.store';
import { mockFacilityPATStore } from '../../testing/mock-data';
import { PatDetailsComponent } from './pat-details.component';

describe('PatDetailsComponent', () => {
  let component: PatDetailsComponent;
  let fixture: ComponentFixture<PatDetailsComponent>;
  let store: FacilityPATReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatDetailsComponent],
      providers: [FacilityPATReportStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(FacilityPATReportStore);
    store.setState(mockFacilityPATStore);

    fixture = TestBed.createComponent(PatDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
