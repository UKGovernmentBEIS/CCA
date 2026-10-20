import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceAppealProvidedComponent } from './non-compliance-appeal-provided.component';
import { nonComplianceAppealProvidedActionStateMock } from './tests/mock-data';

describe('NonComplianceAppealProvidedComponent', () => {
  let component: NonComplianceAppealProvidedComponent;
  let fixture: ComponentFixture<NonComplianceAppealProvidedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceAppealProvidedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(nonComplianceAppealProvidedActionStateMock);

    fixture = TestBed.createComponent(NonComplianceAppealProvidedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        ['When was the appeal registered?', 'Appeal file', 'Comments'],
        ['2 Mar 2025', 'Appeal_file.pdf', 'A Martini. Shaken, Not Stirred.'],
      ],
    ]);
  });
});
