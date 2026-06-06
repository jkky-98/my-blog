<template>
  <RouterLink :to="`/posts/${post.slug}`" class="post-card-link">
    <v-card class="post-card" rounded="lg" elevation="0">
      <div class="post-card__visual" :class="`post-card__visual--${post.accent}`">
        <span>{{ post.tags[0] }}</span>
      </div>

      <v-card-text class="post-card__body">
        <div class="post-card__tags">
          <TagBadge v-for="tag in post.tags.slice(0, 2)" :key="tag" :label="tag" />
        </div>
        <h3>{{ post.title }}</h3>
        <PostMeta
          :author="post.author"
          :created-at="post.createdAt"
          :reading-time="post.readingTime"
        />
        <p>{{ post.description }}</p>
      </v-card-text>
    </v-card>
  </RouterLink>
</template>

<script setup lang="ts">
  import type { PostSummary } from '@/data/posts'

  import PostMeta from './PostMeta.vue'
  import TagBadge from './TagBadge.vue'

  defineProps<{
    post: PostSummary
  }>()
</script>
