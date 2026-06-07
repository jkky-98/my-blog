<template>
  <RouterLink :to="`/posts/${post.slug}`" class="post-card-link">
    <v-card class="post-card" rounded="lg" elevation="0">
      <div class="post-card__visual" :class="`post-card__visual--${post.accent}`">
        <span>{{ post.category }}</span>
      </div>

      <v-card-text class="post-card__body">
        <div class="post-card__tags">
          <TagBadge :label="post.category" />
          <TagBadge v-for="tag in post.tags.slice(0, 2)" :key="tag" :label="tag" />
        </div>
        <h3>{{ post.title }}</h3>
        <PostMeta
          :author="post.author"
          :created-at="post.createdAt"
          :reading-time="post.readingTime"
          :view-count="post.viewCount"
        />
        <p>{{ post.description.slice(0, 100) }}</p>
      </v-card-text>
    </v-card>
  </RouterLink>
</template>

<script setup lang="ts">
  import type { PostSummary } from '@/types/blog'

  import PostMeta from './PostMeta.vue'
  import TagBadge from './TagBadge.vue'

  defineProps<{
    post: PostSummary
  }>()
</script>
