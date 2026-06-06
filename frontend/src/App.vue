<template>
  <v-app>
    <AppHeader />
    <v-main class="app-main">
      <router-view />
    </v-main>
    <AppFooter />
  </v-app>
</template>

<script setup lang="ts">
  import AppFooter from '@/components/layout/AppFooter.vue'
  import AppHeader from '@/components/layout/AppHeader.vue'
</script>
