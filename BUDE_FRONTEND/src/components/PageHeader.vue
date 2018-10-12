<template>
	<div class="PageHeader">
		<div>
      <span class="logo"><!--<img src="@/assets/logo.png" style="width:32px" height="32px;" />--><icon name="taichi" scale="4"></icon></span>
			<span class="title">Bude</span>
		</div>
	</div>
</template>

<script>
  export default {
    name: "PageHeader",
  }
</script>

<style scoped>
.PageHeader{
	position:fixed;
	top:0;
	left:0;
	width:100%;
	box-sizing:border-box;
	height:50px;
	line-height:50px;
	background:#262626;
	color:#fff;
}
div{
	display:flex;
	align-items:center;
	height:100%;
}
.title{
	font-size:18px;
	font-weight:bold;
}
.logo{
	display:flex;
	align-items:center;
	justify-content:center;
	width:50px;
	height:50px;
	display: flex;
	animation-name: myfirst;
    animation-duration:10s;
    animation-timing-function: linear;
    animation-delay:0;
    animation-iteration-count: infinite;
    animation-direction:normal;
    animation-play-state: running;
    /* Safari 与 Chrome: */
    -webkit-animation-name: myfirst;
    -webkit-animation-duration:10s;
    -webkit-animation-timing-function: linear;
    -webkit-animation-delay:0;
    -webkit-animation-iteration-count: infinite;
    -webkit-animation-direction:normal;
    -webkit-animation-play-state: running;
}
@keyframes myfirst
{
    0%{	
    	transform: rotate(0deg);
		-ms-transform: rotate(0deg); /* IE 9 */
		-webkit-transform: rotate(0deg); /* Safari and Chrome */ 
	}
	25%{	
		transform: rotate(90deg);
		-ms-transform: rotate(90deg); /* IE 9 */
		-webkit-transform: rotate(90deg); /* Safari and Chrome */ 
	}
	50%{	
		transform: rotate(180deg);
		-ms-transform: rotate(180deg); /* IE 9 */
		-webkit-transform: rotate(180deg); /* Safari and Chrome */ 
	}
	75%{	
		transform: rotate(270deg);
		-ms-transform: rotate(270deg); /* IE 9 */
		-webkit-transform: rotate(270deg); /* Safari and Chrome */ 
	}
	100%{	
		transform: rotate(360deg);
		-ms-transform: rotate(360deg); /* IE 9 */
		-webkit-transform: rotate(360deg); /* Safari and Chrome */ 
	}
}

</style>
