<template>
	<div class="page">
		<Loading v-if="$store.state.isLoading"></Loading>
		<div class="head">
			<HeadDetails></HeadDetails>
			<HeadDialog></HeadDialog>
		</div>
	</div>
</template>

<script>
	import HeadDialog from './comp/HeadDialog'
	import HeadDetails from './comp/HeadDetails'
	import Loading from '@/components/Loading'
	export default {
    	name: "project",
    	data(){
    		return{
    		}
    	},
    	components:{
    		HeadDialog,HeadDetails,Loading
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
	.head{
		box-sizing:border-box;
		width:100%;
		height:100%;
		padding:20px 20px;
	}
</style>
