<template>
  <div class="page">
    <Loading v-if="$store.state.isLoading"></Loading>
    <div class="arm">
      <ArmDetails></ArmDetails>
      <ArmDialog></ArmDialog>
    </div>
  </div>
</template>

<script>
  import Loading from '@/components/Loading'
  import ArmDetails from './comp/ArmDetails'
  import ArmDialog from './comp/ArmDialog'
  export default {
    name: "arm",
    data() {
      return {}
    },
    components: {
      Loading,
      ArmDetails,
      ArmDialog
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
  .arm{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding:20px 20px;
  }
</style>
