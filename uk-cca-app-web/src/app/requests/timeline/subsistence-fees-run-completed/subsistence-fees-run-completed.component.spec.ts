import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesRunCompletedComponent } from './subsistence-fees-run-completed.component';
import { subsistenceFeesRunCompletedActionStateMock } from './tests/mock-data';

describe('SubsistenceFeesRunCompletedComponent', () => {
  let component: SubsistenceFeesRunCompletedComponent;
  let fixture: ComponentFixture<SubsistenceFeesRunCompletedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubsistenceFeesRunCompletedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(subsistenceFeesRunCompletedActionStateMock);

    fixture = TestBed.createComponent(SubsistenceFeesRunCompletedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [
        ['Payment request ID', 'Charging year', 'Status', 'Invoices sent', 'Detailed report'],
        ['S2501', '2025', 'Completed', '1', 'S2501 subsistence fees summary report.csv'],
      ],
    ]);
  });
});
