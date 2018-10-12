<template>
	<div class="page">
		<Loading v-if="$store.state.isLoading"></Loading>
		<div class="bead">
			<BeadDetails></BeadDetails>
			<BeadDialog></BeadDialog>
		</div>
	</div>
</template>

<script>
	import BeadDetails from './comp/BeadDetails'
	import BeadDialog from './comp/BeadDialog'
	import Loading from '@/components/Loading'
	export default {
    	name: "bead",
    	data(){
    		return{
    		}
    	},
    	components:{
    		BeadDetails,BeadDialog,Loading
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
	.bead{
		box-sizing:border-box;
		width:100%;
		height:100%;
		padding:20px 20px;
	}
</style>
