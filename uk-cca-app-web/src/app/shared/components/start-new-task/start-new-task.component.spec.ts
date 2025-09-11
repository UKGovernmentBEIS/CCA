import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { RequestItemsService, RequestsService, UserStateDTO } from 'cca-api';

import { StartNewTaskComponent } from './start-new-task.component';

const userStateDTO: UserStateDTO = { userId: 'abcd-1234', roleType: 'SECTOR_USER', status: 'ACCEPTED' };

describe('StartNewTaskComponent', () => {
  let component: StartNewTaskComponent;
  let fixture: ComponentFixture<StartNewTaskComponent>;
  let authStore: AuthStore;
  let requestService: jest.Mocked<Partial<RequestsService>>;
  let requestItemService: jest.Mocked<Partial<RequestItemsService>>;

  beforeEach(async () => {
    requestService = {
      getAvailableWorkflows: jest.fn(),
    };

    requestItemService = {
      getItemsByRequest: jest.fn(),
    };

    TestBed.configureTestingModule({
      imports: [StartNewTaskComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1 }, null, {
            targetUnit: {
              targetUnitAccountDetails: { name: 'Target unit account 1' },
            },
          }),
        },
        { provide: RequestsService, useValue: requestService },
        { provide: RequestItemsService, useValue: requestItemService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState(userStateDTO);
  });

  function createComponentWithMock(mockResponse: any) {
    requestService.getAvailableWorkflows.mockReturnValue(of(mockResponse));
    fixture = TestBed.createComponent(StartNewTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponentWithMock({});
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    createComponentWithMock({});
    expect(requestService.getAvailableWorkflows).toHaveBeenCalledTimes(1);
    expect(screen.getByText('Start a new task')).toBeInTheDocument();
  });

  it('should render the page heading', async () => {
    createComponentWithMock({});
    const noWorkflowsMessage = screen.getByTestId('no-workflows');
    expect(noWorkflowsMessage).toBeInTheDocument();
    expect(noWorkflowsMessage.textContent).toBe('No tasks are currently available.');
  });

  it('should display workflows in correct order', () => {
    createComponentWithMock({
      PERFORMANCE_DATA_DOWNLOAD: { valid: true },
      PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD: { valid: true },
      PERFORMANCE_DATA_UPLOAD: { valid: true },
    });

    const compiled = fixture.nativeElement as HTMLElement;
    const headings = compiled.querySelectorAll('h2.govuk-heading-m');

    expect(headings.length).toBe(3);
    expect(headings[0].textContent?.trim()).toBe('Download target period reporting (TPR) spreadsheets');
    expect(headings[1].textContent?.trim()).toBe('Upload target period reporting (TPR) spreadsheets');
    expect(headings[2].textContent?.trim()).toBe('Upload PAT spreadsheets');
  });
});
