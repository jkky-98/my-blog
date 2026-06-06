<template>
  <section class="hero-section">
    <v-container>
      <v-row class="hero-section__grid" align="stretch">
        <v-col cols="12" md="8">
          <RouterLink :to="`/posts/${featuredPost.slug}`" class="hero-card">
            <div class="hero-card__content">
              <TagBadge :label="featuredPost.tags[0]" />
              <h1>{{ featuredPost.title }}</h1>
              <p>{{ featuredPost.description }}</p>
              <PostMeta
                :author="featuredPost.author"
                :created-at="featuredPost.createdAt"
                :reading-time="featuredPost.readingTime"
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
                <PostMeta :created-at="post.createdAt" :reading-time="post.readingTime" />
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
  import { popularPosts, posts } from '@/data/posts'

  const featuredPost = posts.find(post => post.featured) ?? posts[0]
</script>
