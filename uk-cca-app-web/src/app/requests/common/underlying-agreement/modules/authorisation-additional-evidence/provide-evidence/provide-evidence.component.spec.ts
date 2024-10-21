import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskState } from '../../../testing';
import { ProvideEvidenceComponent } from './provide-evidence.component';

describe('ProvideEvidenceComponent', () => {
  let component: ProvideEvidenceComponent;
  let fixture: ComponentFixture<ProvideEvidenceComponent>;
  let store: RequestTaskStore;

  const underlyingAgreementTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const saveSubTaskSpy = jest.spyOn(underlyingAgreementTaskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideEvidenceComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: underlyingAgreementTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);
    fixture = TestBed.createComponent(ProvideEvidenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    const heading = screen.getByRole('heading', { name: 'Provide evidence' });
    expect(heading).toBeInTheDocument();
    expect(screen.getByText('Authorisation and additional evidence')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Continue')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Authorisation')).toBeInTheDocument();
    expect(screen.getByText('Additional evidence (optional)')).toBeInTheDocument();
  });

  it('should submit form and call "saveSubTask" method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });
});
