import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId } from '@testing';

import { RequestErrorComponent } from './request-error.component';

describe('RequestErrorComponent', () => {
  let component: RequestErrorComponent;
  let fixture: ComponentFixture<RequestErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestErrorComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, { errorCode: 'inProgress' }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the `in progress` error', () => {
    const errorText = getByTestId('in-progress-error');
    expect(errorText.textContent.trim()).toBe(
      'Payment request run is in progress, you cannot initiate a new one until it has finished',
    );
  });
});
