import {
  findPostById,
  getAdminPosts,
  savePost,
  updatePostStatus,
} from '@/data/posts'
import type { AdminPostRequest, PostStatus } from '@/types/blog'

import { delay } from './http'

export function fetchAdminPosts(params: Parameters<typeof getAdminPosts>[0]) {
  return delay(getAdminPosts(params))
}

export function fetchAdminPostDetail(id: number) {
  const post = findPostById(id)
  if (!post) throw new Error('글을 찾을 수 없습니다.')
  return delay(post)
}

export function createPost(request: AdminPostRequest) {
  return delay(savePost(request))
}

export function updatePost(id: number, request: AdminPostRequest) {
  return delay(savePost(request, id))
}

export function patchPostStatus(id: number, status: PostStatus) {
  return delay(updatePostStatus(id, status))
}
