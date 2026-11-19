import { Location } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { BasePage, mockClass } from '@netz/common/testing';

import { OperatorUsersService, RegulatorUsersService, SectorUsersService } from 'cca-api';

import { ResetTwoFaComponent } from './reset-two-fa.component';

describe('ResetTwoFaComponent', () => {
  const regulatorUsersService = mockClass(RegulatorUsersService);
  const operatorUsersService = mockClass(OperatorUsersService);
  const sectorUsersService = mockClass(SectorUsersService);

  class Page extends BasePage<ResetTwoFaComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  function render(state: Record<string, unknown>) {
    window.history.replaceState(state, 'test');

    TestBed.configureTestingModule({
      imports: [ResetTwoFaComponent],
      providers: [
        provideRouter([]),
        { provide: RegulatorUsersService, useValue: regulatorUsersService },
        { provide: OperatorUsersService, useValue: operatorUsersService },
        { provide: SectorUsersService, useValue: sectorUsersService },
      ],
    });
    const navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const backSpy = vi.spyOn(TestBed.inject(Location), 'back').mockImplementation(() => undefined);

    const fixture = TestBed.createComponent(ResetTwoFaComponent);
    fixture.detectChanges();
    return { page: new Page(fixture), navigateSpy, backSpy };
  }

  afterEach(() => {
    window.history.replaceState(null, '');
    vi.clearAllMocks();
  });

  it.each([
    ['REGULATOR', { userId: 'u1' }, () => expect(regulatorUsersService.resetRegulator2Fa).toHaveBeenCalledWith('u1')],
    [
      'OPERATOR',
      { userId: 'u1', accountId: 7 },
      () => expect(operatorUsersService.resetOperator2Fa).toHaveBeenCalledWith(7, 'u1'),
    ],
    [
      'SECTOR_USER',
      { userId: 'u1', sectorAssociationId: 9 },
      () => expect(sectorUsersService.resetSectorUser2Fa).toHaveBeenCalledWith(9, 'u1'),
    ],
  ])('should reset 2FA for a %s user and go back', (role, ids, expectReset) => {
    regulatorUsersService.resetRegulator2Fa.mockReturnValue(of(null));
    operatorUsersService.resetOperator2Fa.mockReturnValue(of(null));
    sectorUsersService.resetSectorUser2Fa.mockReturnValue(of(null));
    const { page, navigateSpy, backSpy } = render({ ...ids, role, userName: 'Test User' });

    expect(page.heading.textContent).toContain(
      'Are you sure you want to reset two-factor authentication for Test User',
    );
    expect(navigateSpy).not.toHaveBeenCalled();

    page.submitButton.click();

    expectReset();
    expect(backSpy).toHaveBeenCalledTimes(1);
  });

  it('should redirect to the landing page without navigation state', () => {
    const { navigateSpy } = render({});

    expect(navigateSpy).toHaveBeenCalledWith(['/landing'], { replaceUrl: true });
  });
});
