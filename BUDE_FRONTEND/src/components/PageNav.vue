<template>
	<div class="PageNav">
		<el-scrollbar style="height:100%;">
			<ul class="first">
				<li :class="activeItem === 'head' ? 'activeClass' : '' " @click="initData('head')">
					<span class="icon"><icon name="head" scale="2"></icon></span>
					<span class="text">项目</span>
				</li>
				<li :class="activeItem === 'face' ? 'activeClass' : '' " @click="initData('face')">
					<span class="icon"><icon name="face" scale="2"></icon></span>
					<span class="text">模块</span>
				</li>
				<li :class="activeItem === 'bead' ? 'activeClass' : '' " @click="initData('bead')">
					<span class="icon"><icon name="bead" scale="2"></icon></span>
					<span class="text">软件包</span>
				</li>
				<li :class="activeItem === 'body' ? 'activeClass' : '' " @click="initData('body')">
					<span class="icon"><icon name="body" scale="2"></icon></span>
					<span class="text">中转组</span>
				</li>
				<li :class="activeItem === 'arm' ? 'activeClass' : '' " @click="initData('arm')">
					<span class="icon"><icon name="arm" scale="2"></icon></span>
					<span class="text">中转端</span>
				</li>
				<li :class="activeItem === 'finger' ? 'activeClass' : '' " @click="initData('finger')">
					<span class="icon"><icon name="finger" scale="2"></icon></span>
					<span class="text">设备端</span>
				</li>
			</ul>
		<!--	<ul class="second">
				<li :class="activeItem === 'user' ? 'activeClass' : '' " @click="initData('user')">
					<span class="icon"><icon name="user" scale="2"></icon></span>
					<span class="text">用户</span>
				</li>
				<li>
					<span class="icon"><icon name="logout" scale="2"></icon></span>
					<span class="text">退出</span>
				</li>
			</ul>-->
		</el-scrollbar>
	</div>
</template>

<script>
  export default {
    name: "PageNav",
    data() {
      return {
        activeItem:'head'
      };
    },
    mounted() {
      if (this.$route.path !== '/') {
        let tempPath = this.$route.path.slice(1);
        let index = tempPath.indexOf('/');
        if (index > -1) {
          this.activeItem = tempPath.slice(0, tempPath.indexOf('/'))
        } else {
          this.activeItem = tempPath
        }
      }
    },
    methods:{
    	initData:function(item){
    		if(item !== this.activeItem){
    			this.activeItem = item;
    			this.$router.push('/'+item);
    		}else{
    			this.$router.push('/'+item);
    		}
    	}
    }
  }
</script>

<style scoped>
.PageNav{
	box-sizing:border-box;
	position:absolute;
	top:50px;
	lef：0；
	z-index:9999;
	background:#262626;
	width:50px;
	height:calc(100% - 50px);
	border-top:1px solid hsla(0,0%,100%,.1);
	transition:width 0.3s;
	/* Safari */
	-webkit-transition:width 0.3s;
}
ul.first,ul.second{
	box-sizing:border-box;
	width:250px;
}
ul.second{
	position:absolute;
	bottom:10px;
}
li{
	display:flex;
	align-items:center;
	box-sizing:border-box;
	height:50px;
	cursor:pointer;
}
li .icon{
	display:flex;
	justify-content:center;
	align-items:center;
	width:50px;
	text-align: center;
	vertical-align:middle;
}
li .text{
	color:#b3b3b3;
	font-size:16px;
}
li:nth-child(7){
	position:relative;
	bottom:50px;
	left:0;
	z-index:1000;
}
svg{
	color:#b3b3b3;
}

/*hover*/
.PageNav:hover{
	width:250px;
}
.PageNav:hover .first li:nth-child(2),.PageNav:hover li:nth-child(5){
	padding-left:30px;
}
.PageNav:hover .first li:nth-child(3),.PageNav:hover li:nth-child(6){
	padding-left:60px;
}
li:hover svg{
	color:#fff;
}
li:hover .text{
	color:#fff;
}

/*click*/
.activeClass{
	background:rgba(0,176,240,1);
}
.activeClass svg{
	color:#fff;
}
.activeClass .text{
	color:#fff;
}
@media screen and (max-height:460px) {
	ul.second{
		bottom:0;
		top:460px;
	}
}
</style>
