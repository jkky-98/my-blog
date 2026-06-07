import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import PostDetailPage from '@/pages/PostDetailPage.vue'
import PostListPage from '@/pages/PostListPage.vue'
import { isAdminAuthenticated } from '@/api/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/posts',
      name: 'posts',
      component: PostListPage,
    },
    {
      path: '/posts/:slug',
      name: 'post-detail',
      component: PostDetailPage,
      props: true,
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/pages/admin/AdminLoginPage.vue'),
    },
    {
      path: '/admin/posts',
      name: 'admin-posts',
      component: () => import('@/pages/admin/AdminPostListPage.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/posts/new',
      name: 'admin-post-new',
      component: () => import('@/pages/admin/AdminPostEditorPage.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/posts/:id/edit',
      name: 'admin-post-edit',
      component: () => import('@/pages/admin/AdminPostEditorPage.vue'),
      meta: { requiresAdmin: true },
    },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach(to => {
  if (to.meta.requiresAdmin && !isAdminAuthenticated()) {
    return {
      name: 'admin-login',
      query: { expired: '1' },
    }
  }

  return true
})

export default router
