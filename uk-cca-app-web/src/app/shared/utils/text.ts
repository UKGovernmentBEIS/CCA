export function truncateText(text: string, length: number): string {
  if (text.length > length) return `${text.substring(0, length)}...`;
  return text;
}

export const boolToString = (bool: boolean | null | undefined): 'Yes' | 'No' | null =>
  typeof bool !== 'boolean' ? null : bool ? 'Yes' : 'No';
