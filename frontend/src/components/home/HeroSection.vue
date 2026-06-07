<template>
  <section class="hero-section">
    <v-container>
      <v-row class="hero-section__grid" align="stretch">
        <v-col cols="12" md="8">
          <RouterLink v-if="featuredPost" :to="`/posts/${featuredPost.slug}`" class="hero-card">
            <div class="hero-card__content">
              <TagBadge :label="featuredPost.category" />
              <h1>{{ featuredPost.title }}</h1>
              <p>{{ featuredPost.description }}</p>
              <PostMeta
                :author="featuredPost.author"
                :created-at="featuredPost.createdAt"
                :reading-time="featuredPost.readingTime"
                :view-count="featuredPost.viewCount"
              />
            </div>
            <div class="hero-card__preview" aria-hidden="true">
              <div class="code-window">
                <span />
                <span />
                <span />
              </div>
              <pre>cache.set(post.slug, summary)</pre>
            </div>
          </RouterLink>
          <div v-else class="hero-card hero-card--empty">
            <div class="hero-card__content">
              <TagBadge label="Mockup" />
              <h1>Private Blog Platform</h1>
              <p>대표 글 후보가 없을 때 보여주는 목업 이미지 영역입니다.</p>
            </div>
          </div>
        </v-col>

        <v-col cols="12" md="4">
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
                  <PostMeta
                    :created-at="post.createdAt"
                    :reading-time="post.readingTime"
                    :view-count="post.viewCount"
                  />
                </div>
              </RouterLink>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup lang="ts">
  import PostMeta from '@/components/blog/PostMeta.vue'
  import TagBadge from '@/components/blog/TagBadge.vue'
  import { getFeaturedPost, getPopularPosts } from '@/data/posts'

  const featuredPost = getFeaturedPost()
  const popularPosts = getPopularPosts(3)
</script>
