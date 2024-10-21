import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  viewChild,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Router } from '@angular/router';

import diff from 'html-diff-ts';

@Component({
  selector: 'cca-highlight-diff',
  templateUrl: './highlight-diff.component.html',
  styleUrl: './highlight-diff.component.scss',
  standalone: true,
  // eslint-disable-next-line @angular-eslint/use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HighlightDiffComponent implements AfterViewInit {
  private readonly domSanitizer = inject(DomSanitizer);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly router = inject(Router);

  private readonly previous = viewChild<ElementRef<HTMLDivElement>>('previous');
  private readonly current = viewChild<ElementRef<HTMLDivElement>>('current');

  htmlDiff: SafeHtml;

  ngAfterViewInit(): void {
    const previous = this.stripHtmlComments(this.previous().nativeElement.innerHTML);
    const current = this.stripHtmlComments(this.current().nativeElement.innerHTML);

    const differences = diff(previous, current);
    const parsedDiff = new DOMParser().parseFromString(differences, 'text/html');

    const changeLinks = Array.from(parsedDiff.querySelectorAll('a > ins.diffmod, a > del.diffmod')).filter((diffItem) =>
      ['Change', 'Remove'].includes(diffItem.textContent),
    );

    const nodeParents: Element[] = [];

    changeLinks.forEach((diffItem) => {
      if (diffItem.tagName !== 'DEL') {
        nodeParents.push(diffItem.parentNode as Element);
      }

      diffItem.parentNode.removeChild(diffItem);
    });

    nodeParents.forEach((np) => (np.textContent = 'Change'));

    this.htmlDiff = this.domSanitizer.bypassSecurityTrustHtml(parsedDiff.documentElement.innerHTML);

    this.changeDetectorRef.detectChanges();
  }

  onClickDiff(event: MouseEvent | KeyboardEvent) {
    let eventTarget: HTMLElement;

    if (event.target instanceof HTMLAnchorElement) {
      eventTarget = event.target;
    } else if ((event.target as Node).nodeName === 'INS' || (event.target as Node).nodeName === 'DEL') {
      eventTarget = (event.target as Node).parentElement;
    }

    if (
      !!eventTarget &&
      eventTarget instanceof HTMLAnchorElement &&
      eventTarget.getAttribute('target') !== '_blank' &&
      event.type === 'click'
    ) {
      event.preventDefault();
      const link = eventTarget.href.replace(eventTarget.baseURI, '');
      this.router.navigateByUrl(this.router.serializeUrl(this.router.parseUrl(link)));
    }
  }

  private stripHtmlComments(html: string) {
    if (typeof html !== 'string') {
      throw new TypeError('strip-html-comments expected a string');
    }

    return html.replace(/<!--[\s\S]*?(?:-->)/g, '');
  }
}
