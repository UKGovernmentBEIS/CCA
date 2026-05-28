import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { NonComplianceClosedComponent } from './non-compliance-closed.component';
import { nonComplianceClosedActionStateMock } from './tests/mock-data';

describe('NonComplianceClosedComponent', () => {
  let component: NonComplianceClosedComponent;
  let fixture: ComponentFixture<NonComplianceClosedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceClosedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(nonComplianceClosedActionStateMock);

    fixture = TestBed.createComponent(NonComplianceClosedComponent);
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
        ['Reason for closing this task', 'Supporting documents'],
        ['There is nothing left to complete.', 'close-task.pdf'],
      ],
    ]);
  });
});
