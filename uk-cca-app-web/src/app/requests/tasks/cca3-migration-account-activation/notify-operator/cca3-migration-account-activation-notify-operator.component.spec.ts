import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../testing/mock-data';
import Cca3MigrationAccountActivationNotifyOperatorComponent from './cca3-migration-account-activation-notify-operator.component';

describe('UnderlyingAgreementActivationNotifyOperatorComponent', () => {
  let component: Cca3MigrationAccountActivationNotifyOperatorComponent;
  let fixture: ComponentFixture<Cca3MigrationAccountActivationNotifyOperatorComponent>;
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Cca3MigrationAccountActivationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(Cca3MigrationAccountActivationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
