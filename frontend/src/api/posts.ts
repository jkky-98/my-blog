import {
  findPostBySlug,
  getFeaturedPost,
  getPopularPosts,
  getPublicPosts,
} from '@/data/posts'

import { delay } from './http'

export function fetchPosts(params: Parameters<typeof getPublicPosts>[0]) {
  return delay(getPublicPosts(params))
}

export function fetchPostDetail(slug: string) {
  const post = findPostBySlug(slug)

  if (!post) {
    return delay({
      post: undefined,
      errorCode: 'POST_NOT_FOUND' as const,
    })
  }

  if (post.status !== 'published') {
    return delay({
      post: undefined,
      errorCode: 'POST_NOT_PUBLIC' as const,
    })
  }

  post.viewCount += 1

  return delay({
    post,
    errorCode: undefined,
  })
}

export function fetchPopularPosts(limit = 3) {
  return delay(getPopularPosts(limit))
}

export function fetchFeaturedPost() {
  return delay(getFeaturedPost())
}
