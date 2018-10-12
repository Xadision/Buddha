<template>
  <div class="main">
    <PageHeader></PageHeader>
    <PageNav></PageNav>
    <div class="router">
        <router-view/>   
    </div>
  </div>
</template>

<script>
  import PageHeader from '@/components/PageHeader'
  import PageNav from  '@/components/PageNav'
  export default {
    name: "Main",
    components: {
      PageHeader,PageNav
    }
  }
</script>

<style>
  .main{
    position:relative;
    width:100%;
    height:100%;
    background:#fafafb;
  }
  .PageNav,.PageHeader{
    z-index:99999;
  }
  .router{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding:50px 0 0 50px;
  }
</style>