import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockCorrectiveActions } from '../../../testing/mock-data';
import { CorrectiveActionsSummaryDetailsComponent } from './corrective-actions-summary-details.component';

describe('CorrectiveActionsSummaryDetailsComponent', () => {
  let component: CorrectiveActionsSummaryDetailsComponent;
  let fixture: ComponentFixture<CorrectiveActionsSummaryDetailsComponent>;
  let componentRef: ComponentRef<CorrectiveActionsSummaryDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectiveActionsSummaryDetailsComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(CorrectiveActionsSummaryDetailsComponent);
    component = fixture.componentInstance;

    componentRef = fixture.componentRef;
    componentRef.setInput('correctiveActions', mockCorrectiveActions);
    componentRef.setInput('isEditable', true);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });
});
