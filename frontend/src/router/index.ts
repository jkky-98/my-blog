import { createRouter, createWebHistory } from 'vue-router'

import HomePage from '@/pages/HomePage.vue'
import PostDetailPage from '@/pages/PostDetailPage.vue'
import PostListPage from '@/pages/PostListPage.vue'

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
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router
