export enum SchemeVersion {
  CCA_2 = 'CCA_2',
  CCA_3 = 'CCA_3',
}

export type SchemeVersions = (keyof typeof SchemeVersion)[];
