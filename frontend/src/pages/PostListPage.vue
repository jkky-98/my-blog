<template>
  <section class="page-section">
    <v-container>
      <v-row>
        <v-col cols="12" md="8">
          <div class="page-title">
            <span>Category / {{ selectedTag || 'All' }}</span>
            <h1>Posts</h1>
            <p>{{ pageDescription }}</p>
          </div>

          <v-row>
            <v-col v-for="post in filteredPosts" :key="post.id" cols="12" sm="6">
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
                <RouterLink
                  :class="{ 'category-list__item--active': !selectedTag }"
                  :to="{ name: 'posts' }"
                  class="category-list__item"
                >
                  <span>&gt; All</span>
                  <span>({{ posts.length }})</span>
                </RouterLink>
                <RouterLink
                  v-for="[tag, count] in categories"
                  :key="tag"
                  :class="{ 'category-list__item--active': selectedTag === tag }"
                  :to="{ name: 'posts', query: { tag } }"
                  class="category-list__item"
                >
                  <span>&gt; {{ tag }}</span>
                  <span>({{ count }})</span>
                </RouterLink>
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
  import { useRoute } from 'vue-router'

  import PostMeta from '@/components/blog/PostMeta.vue'
  import PostCard from '@/components/blog/PostCard.vue'
  import { popularPosts, posts } from '@/data/posts'

  const route = useRoute()

  const selectedTag = computed(() => {
    return typeof route.query.tag === 'string' ? route.query.tag : ''
  })

  const filteredPosts = computed(() => {
    if (!selectedTag.value) {
      return posts
    }

    return posts.filter(post => post.tags.includes(selectedTag.value))
  })

  const pageDescription = computed(() => {
    if (!selectedTag.value) {
      return '백엔드, 프론트엔드, 디자인을 만들며 남긴 기록.'
    }

    return `${selectedTag.value} 주제로 남긴 기록.`
  })

  const categories = computed(() => {
    const counts = new Map<string, number>()

    for (const post of posts) {
      for (const tag of post.tags) {
        counts.set(tag, (counts.get(tag) ?? 0) + 1)
      }
    }

    return Array.from(counts.entries()).sort(([tagA, countA], [tagB, countB]) => {
      return countB - countA || tagA.localeCompare(tagB)
    })
  })
</script>
