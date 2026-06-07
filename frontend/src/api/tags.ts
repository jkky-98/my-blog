import { getTags } from '@/data/posts'

import { delay } from './http'

export function fetchTags() {
  return delay(getTags())
}
