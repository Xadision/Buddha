<template>
	<div id="app">
    	<router-view/>
  	</div>
</template>

<script>
	export default {
  		name: 'App'
	}
</script>

<style>
	html,body,#app,h1,ul,li{
		margin:0;
		padding:0;
	}
	html,body,#app{
		width:100%;
		height:100%;
	}
	ul,li{
		list-style:none;
	}

	/*element-ui*/
	.el-scrollbar__wrap{
		overflow-x:hidden;
	}
	.el-pagination button:disabled,.el-pagination .btn-next, .el-pagination .btn-prev,.el-pager li{
		background-color:transparent;
	}
	.el-dialog{
  		position:absolute;
  		top:50%;
  		left:50%;
  		margin:0 !important;
  		transform:translate(-50%, -50%);
  	}
	.el-dialog__body{
		padding:15px 20px;
		font-size:20px;
	}
	.el-dialog__title{
		font-weight:bold;
	}
	.el-dialog__footer{
		padding-bottom:15px;
	}
  .el-select-dropdown .el-scrollbar .el-scrollbar__wrap{
    overflow: scroll;
  }
	.el-row{
		width:100%;
	}
</style>
