<template>
  <footer class="app-footer">
    <v-container class="app-footer__inner">
      <strong>{Jin.dev}</strong>
      <span>Copyright {{ currentYear }}. Built with Vue 3, Vuetify, and Spring Boot in mind.</span>
    </v-container>
  </footer>
</template>

<script setup lang="ts">
  const currentYear = new Date().getFullYear()
</script>
