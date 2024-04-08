export interface TaskSection {
  title: string;
  tasks: TaskItem[];
}

export interface TaskItem {
  name?: string;
  status: string;
  linkText: string;
  link: string;
}
