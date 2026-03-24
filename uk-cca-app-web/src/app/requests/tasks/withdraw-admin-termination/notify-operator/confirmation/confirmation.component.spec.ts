import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

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
    expect(getByText('Admin termination withdrawal notice sent to operator', fixture.nativeElement)).toBeTruthy();
    expect(getByText('The admin termination agreement has been withdrawn.', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText('The selected users will receive an email notification of your decision.', fixture.nativeElement),
    ).toBeTruthy();
    expect(getByText('Return to: Dashboard', fixture.nativeElement)).toBeTruthy();
  });
});
