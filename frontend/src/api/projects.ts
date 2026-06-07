import { projects } from '@/data/projects'

import { delay } from './http'

export function fetchProjects() {
  return delay(projects)
}
