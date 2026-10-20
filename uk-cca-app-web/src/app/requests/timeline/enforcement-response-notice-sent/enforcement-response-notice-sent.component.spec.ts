import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { EnforcementResponseNoticeSentComponent } from './enforcement-response-notice-sent.component';
import { enforcementResponseNoticeSentActionStateMock } from './tests/mock-data';

describe('EnforcementResponseNoticeSentComponent', () => {
  let component: EnforcementResponseNoticeSentComponent;
  let fixture: ComponentFixture<EnforcementResponseNoticeSentComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnforcementResponseNoticeSentComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(enforcementResponseNoticeSentActionStateMock);

    fixture = TestBed.createComponent(EnforcementResponseNoticeSentComponent);
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
        ['Type of enforcement response notice', 'Upload file', 'Your comments'],
        ['Penalty notice', 'penalty_notice.pdf', 'A Martini. Shaken, Not Stirred.'],
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
