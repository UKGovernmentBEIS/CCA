import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { AppealOutcomeProvidedComponent } from './appeal-outcome-provided.component';
import { appealOutcomeSubmittedActionStateMock } from './tests/mock-data';

describe('AppealOutcomeProvidedComponent', () => {
  let component: AppealOutcomeProvidedComponent;
  let fixture: ComponentFixture<AppealOutcomeProvidedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppealOutcomeProvidedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(appealOutcomeSubmittedActionStateMock);

    fixture = TestBed.createComponent(AppealOutcomeProvidedComponent);
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
        ['Was the appeal successful?', 'Date of appeal outcome', 'Uploaded files', 'Comments'],
        ['Yes', '2 Mar 2025', 'Appeal_outcome_file.pdf', 'A Martini. Shaken, Not Stirred.'],
      ],
    ]);
  });
});
