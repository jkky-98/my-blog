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

      <v-btn class="app-header__search" icon="mdi-magnify" variant="text" aria-label="search" />

      <v-menu class="app-header__mobile">
        <template #activator="{ props }">
          <v-btn icon="mdi-menu" variant="text" v-bind="props" aria-label="menu" />
        </template>
        <v-list class="mobile-menu">
          <v-list-item to="/" title="Home" />
          <v-list-item to="/posts" title="Posts" />
          <v-list-item href="#projects" title="Projects" />
        </v-list>
      </v-menu>
    </v-container>
  </v-app-bar>
</template>
