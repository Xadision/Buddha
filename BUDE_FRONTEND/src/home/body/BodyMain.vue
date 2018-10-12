<template>
  <div class="page">
    <Loading v-if="$store.state.isLoading"></Loading>
    <div class="body">
      <BodyDetails></BodyDetails>
      <BodyDialog></BodyDialog>
    </div>
  </div>
</template>

<script>
  import Loading from '@/components/Loading'
  import BodyDetails from './comp/BodyDetails'
  import BodyDialog from "./comp/BodyDialog";
  export default {
    name: "transferUnit",
    data() {
      return {}
    },
    components: {
      BodyDialog,
      Loading,
      BodyDetails
    }
  }
</script>

<style scoped>
  .page{
    position:relative;
    width:100%;
    height:100%;
    overflow:hidden;
  }
  .body{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding:20px 20px;
  }
</style>
