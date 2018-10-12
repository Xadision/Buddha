<template>
  <div class="page">
    <Loading v-if="$store.state.isLoading"></Loading>
    <div class="finger">
      <FingerDetail></FingerDetail>
      <FingerDialog></FingerDialog>
    </div>
  </div>
</template>

<script>
  import Loading from '@/components/Loading'
  import FingerDetail from './comp/FingerDetail'
  import FingerDialog from './comp/FingerDialog'
  export default {
    name: "finger",
    data() {
      return {}
    },
    components: {
      Loading,
      FingerDetail,
      FingerDialog
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
  .finger{
    box-sizing:border-box;
    width:100%;
    height:100%;
    padding:20px 20px;
  }
</style>
