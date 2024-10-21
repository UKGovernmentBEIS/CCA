import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import { mockCreateTargetUnitState } from '../specs/fixture/mocks';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }, CreateTargetUnitStore],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct success panel message', () => {
    expect(screen.getByText('Target unit Operator name created')).toBeInTheDocument();
  });

  it('should display the correct "What happens next" message', () => {
    expect(screen.getByText('What happens next')).toBeInTheDocument();
    expect(screen.getByText('You can now apply for an underlying agreement.')).toBeInTheDocument();
  });

  it('should display the correct "return to" link', () => {
    expect(screen.getByText('Return to: your tasks')).toBeInTheDocument();
  });
});
