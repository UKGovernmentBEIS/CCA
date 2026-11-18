import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceNoticeOfIntentSubmittedComponent } from './non-compliance-notice-of-intent-submitted.component';
import { nonComplianceNoticeOfIntentSubmittedActionStateMock } from './tests/mock-data';

describe('NonComplianceNoticeOfIntentSubmittedComponent', () => {
  let component: NonComplianceNoticeOfIntentSubmittedComponent;
  let fixture: ComponentFixture<NonComplianceNoticeOfIntentSubmittedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceNoticeOfIntentSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(nonComplianceNoticeOfIntentSubmittedActionStateMock);

    fixture = TestBed.createComponent(NonComplianceNoticeOfIntentSubmittedComponent);
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
        ['Upload file', 'Comments'],
        ['filename.xls', 'A Martini. Shaken, Not Stirred.'],
      ],
      [
        ['Users notified'],
        [
          'John William, Responsible person, williamsj@abc.comMatthew Johnson, Administrative contact, mjohnson@def.comAlex Turner, Operator user',
        ],
      ],
    ]);
  });
});
