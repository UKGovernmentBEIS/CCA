import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../testing/mock-data';
import { AuditDetailsAndCorrectiveActionsConfirmationComponent } from './confirmation.component';

describe('AuditDetailsAndCorrectiveActionsConfirmationComponent', () => {
  let component: AuditDetailsAndCorrectiveActionsConfirmationComponent;
  let fixture: ComponentFixture<AuditDetailsAndCorrectiveActionsConfirmationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditDetailsAndCorrectiveActionsConfirmationComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(AuditDetailsAndCorrectiveActionsConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(fixture).toMatchSnapshot();
  });
});
