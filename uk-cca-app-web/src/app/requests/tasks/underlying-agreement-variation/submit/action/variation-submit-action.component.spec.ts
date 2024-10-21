import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ActivatedRouteStub, MockType } from '@netz/common/testing';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { VariationSubmitActionComponent } from './variation-submit-action.component';

describe('VariationSubmitActionComponent', () => {
  let component: VariationSubmitActionComponent;
  let fixture: ComponentFixture<VariationSubmitActionComponent>;
  let router: Router;

  const activatedRoute = new ActivatedRouteStub();

  const tasksService: MockType<TaskService> = {
    submit: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: TaskService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VariationSubmitActionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and text', () => {
    const heading = screen.getByRole('heading', { name: 'Send variation application to regulator' });
    expect(heading).toBeInTheDocument();
  });

  it('should submit and navigate to confirmation page', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const tasksServiceSpy = jest.spyOn(tasksService, 'submit');
    const user = UserEvent.setup();
    await user.click(screen.getByText('Confirm and send'));

    expect(tasksServiceSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], expect.anything());
  });
});
