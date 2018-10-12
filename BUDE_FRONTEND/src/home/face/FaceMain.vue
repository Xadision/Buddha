<template>
	<div class="page">
		<Loading v-if="$store.state.isLoading"></Loading>
		<div class="face">
			<FaceDetails></FaceDetails>
			<FaceDialog></FaceDialog>
		</div>
	</div>
</template>

<script>
	import Loading from '@/components/Loading'
	import FaceDialog from './comp/FaceDialog'
	import FaceDetails from './comp/FaceDetails'
	export default {
    	name: "face",
    	data(){
    		return{
    		}
    	},
    	components:{
    		Loading,FaceDetails,FaceDialog
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
	.face{
		box-sizing:border-box;
		width:100%;
		height:100%;
		padding:20px 20px;
	}
</style>
