import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { TrackCorrectiveActionsConfirmationComponent } from './confirmation.component';

describe('TrackCorrectiveActionsConfirmationComponent', () => {
  let component: TrackCorrectiveActionsConfirmationComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();
    fixture = TestBed.createComponent(TrackCorrectiveActionsConfirmationComponent);
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
