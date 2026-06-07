<template>
  <v-app-bar class="app-header" color="transparent" elevation="0" height="72">
    <v-container class="app-header__inner">
      <RouterLink to="/" class="app-header__brand">
        <span>{Jin.dev}</span>
      </RouterLink>

      <nav class="app-header__nav">
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/posts">Posts</RouterLink>
        <a href="#projects">Projects</a>
      </nav>

      <v-btn
        class="app-header__search"
        icon="mdi-magnify"
        variant="text"
        aria-label="search"
        @click="searchDialog = true"
      />

      <v-menu class="app-header__mobile">
        <template #activator="{ props }">
          <v-btn icon="mdi-menu" variant="text" v-bind="props" aria-label="menu" />
        </template>
        <v-list class="mobile-menu">
          <v-list-item to="/" title="Home" />
          <v-list-item to="/posts" title="Posts" />
          <v-list-item href="#projects" title="Projects" />
          <v-list-item prepend-icon="mdi-magnify" title="Search" @click="searchDialog = true" />
        </v-list>
      </v-menu>
    </v-container>
  </v-app-bar>

  <v-dialog v-model="searchDialog" max-width="420">
    <v-card class="search-dialog" rounded="lg">
      <v-card-text>
        <v-icon icon="mdi-magnify" size="36" />
        <h2>추후 서비스 예정입니다</h2>
        <p>검색은 API 연결 이후 글 제목, 요약, 카테고리, 태그 기준으로 제공할 예정입니다.</p>
        <v-btn block color="primary" rounded="pill" @click="searchDialog = false">확인</v-btn>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
  import { ref } from 'vue'

  const searchDialog = ref(false)
</script>
