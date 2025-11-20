import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { AuditDetailsSummaryComponent } from './audit-details-summary.component';

describe('AuditDetailsSummaryComponent', () => {
  let component: AuditDetailsSummaryComponent;
  let fixture: ComponentFixture<AuditDetailsSummaryComponent>;
  let store: RequestTaskStore;

  const route: any = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn().mockReturnValue(123) },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditDetailsSummaryComponent],
      providers: [provideHttpClient(), RequestTaskStore, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(AuditDetailsSummaryComponent);
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
