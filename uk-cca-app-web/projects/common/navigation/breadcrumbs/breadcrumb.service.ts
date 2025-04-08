import { Injectable, signal } from '@angular/core';

export type Link = {
  text: string;
  link: string | string[];
  queryParams?: Map<string, string | number>;
  fragment?: string;
};
export type BreadcrumbRouteData = Array<Link | (() => Link)>;

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  links = signal<Link[]>([]);
  inverse = signal<boolean>(false);

  show(l: Link[]): void {
    this.links.set(l);
  }

  clear(): void {
    this.links.set([]);
  }
}
