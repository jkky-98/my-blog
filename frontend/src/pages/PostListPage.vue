<template>
  <section class="page-section">
    <v-container>
      <v-row>
        <v-col cols="12" md="8">
          <div class="page-title">
            <span>Category / All</span>
            <h1>Posts</h1>
            <p>백엔드, 프론트엔드, 디자인을 만들며 남긴 기록.</p>
          </div>

          <v-row>
            <v-col v-for="post in posts" :key="post.id" cols="12" sm="6">
              <PostCard :post="post" />
            </v-col>
          </v-row>
        </v-col>

        <v-col cols="12" md="4">
          <div class="sticky-sidebar">
            <div class="side-panel">
              <div class="panel-title">Popular !</div>
              <RouterLink
                v-for="post in popularPosts"
                :key="post.id"
                :to="`/posts/${post.slug}`"
                class="popular-item"
              >
                <div class="popular-item__thumb" :class="`popular-item__thumb--${post.accent}`" />
                <div>
                  <strong>{{ post.title }}</strong>
                  <PostMeta :created-at="post.createdAt" :reading-time="post.readingTime" />
                </div>
              </RouterLink>
            </div>

            <div class="side-panel">
              <div class="panel-title">Category</div>
              <div class="category-list">
                <div v-for="[tag, count] in categories" :key="tag">
                  <span>&gt; {{ tag }}</span>
                  <span>({{ count }})</span>
                </div>
              </div>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import { computed } from 'vue'

  import PostMeta from '@/components/blog/PostMeta.vue'
  import PostCard from '@/components/blog/PostCard.vue'
  import { popularPosts, posts } from '@/data/posts'

  const categories = computed(() => {
    const counts = new Map<string, number>()

    for (const post of posts) {
      for (const tag of post.tags) {
        counts.set(tag, (counts.get(tag) ?? 0) + 1)
      }
    }

    return Array.from(counts.entries()).slice(0, 6)
  })
</script>
