<template>
  <section class="content-section">
    <v-container>
      <div class="section-heading">
        <h2>Recently</h2>
        <v-btn append-icon="mdi-arrow-right" rounded="pill" to="/posts" variant="text">
          All posts
        </v-btn>
      </div>

      <v-row>
        <v-col v-for="post in recentPosts" :key="post.id" cols="12" sm="6">
          <PostCard :post="post" />
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import PostCard from '@/components/blog/PostCard.vue'
  import { getPublicPosts } from '@/data/posts'

  const recentPosts = getPublicPosts({ page: 1, size: 4 }).items
</script>
