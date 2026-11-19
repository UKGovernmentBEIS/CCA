import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CorrectiveAction, CorrectiveActions } from 'cca-api';

import { CorrectiveActionsSummaryDetailsComponent } from './corrective-actions-summary-details.component';

describe('CorrectiveActionsSummaryDetailsComponent', () => {
  let fixture: ComponentFixture<CorrectiveActionsSummaryDetailsComponent>;

  const actions: CorrectiveAction[] = [
    { title: 'First', details: 'First', deadline: '2026-01-01' },
    { title: 'Second', details: 'Second', deadline: '2026-02-01' },
    { title: 'Third', details: 'Third', deadline: '2026-03-01' },
  ];

  const setActions = (list: CorrectiveAction[]) =>
    fixture.componentRef.setInput('correctiveActions', { hasActions: true, actions: list } as CorrectiveActions);

  const removeButtons = (): HTMLButtonElement[] => Array.from(fixture.nativeElement.querySelectorAll('section button'));
  const changeLinks = (): HTMLAnchorElement[] => Array.from(fixture.nativeElement.querySelectorAll('section a'));

  // The parent removes the action through the API and passes back the shorter list.
  const removeAction = async (index: number) => {
    removeButtons()[index].click();
    setActions(actions.filter((_, i) => i !== index));
    await fixture.whenStable();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectiveActionsSummaryDetailsComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CorrectiveActionsSummaryDetailsComponent);
    setActions(actions);
    fixture.componentRef.setInput('isEditable', true);
    await fixture.whenStable();
  });

  it('should move focus to the next corrective action after removing an action', async () => {
    await removeAction(1);

    expect(changeLinks()).toHaveLength(2);
    expect(document.activeElement).toBe(changeLinks()[1]);
  });

  it('should move focus to the add button after removing the last action', async () => {
    await removeAction(2);

    expect((document.activeElement as HTMLElement).textContent.trim()).toBe('Add another corrective action');
  });
});
