<template>
  <v-chip class="tag-badge" size="small" variant="flat">
    {{ label }}
  </v-chip>
</template>

<script setup lang="ts">
  defineProps<{
    label: string
  }>()
</script>
