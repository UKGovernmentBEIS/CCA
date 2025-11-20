import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { CorrectiveActionsSummaryComponent } from './corrective-actions-summary.component';

describe('CorrectiveActionsSummaryComponent', () => {
  let component: CorrectiveActionsSummaryComponent;
  let fixture: ComponentFixture<CorrectiveActionsSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectiveActionsSummaryComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(CorrectiveActionsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });
});
