import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import ConfirmationComponent from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct banner and content', () => {
    expect(screen.getByText('Admin termination withdrawal notice sent to operator')).toBeInTheDocument();
    expect(screen.getByText('The admin termination agreement has been withdrawn.')).toBeInTheDocument();
    expect(
      screen.getByText('The selected users will receive an email notification of your decision.'),
    ).toBeInTheDocument();
    expect(screen.getByText('Return to: Dashboard')).toBeInTheDocument();
  });
});
