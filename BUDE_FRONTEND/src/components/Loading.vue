<!--加载动画-->
<template>
  <div class="loading-wrap">
    <div class="loading">
      <div class="loading-taichi">
        <!--<img src="@/assets/logo.png"/>--><icon name="taichi1" scale="25"></icon>
      </div>
      <div class="loading-text">
        <span></span><span></span><span></span><span></span>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "loading"
  }
</script>

<style scoped>
  .loading-wrap {
    z-index: 20000;
    position: absolute;
    display: flex;
    width: 100%;
    height:100%;
    justify-content: center;
    align-items: center;
    background:rgba(0,0,0,0.5);
  }

  .loading {
    display: flex;
    width:100%;
    height:100%;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .loading-taichi{
    opacity:0.8;
    -webkit-animation-name: myfirst;
    -webkit-animation-duration:5s;
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
        -ms-transform: rotate(0deg);
        -webkit-transform: rotate(0deg);
      }
    25%{  
        transform: rotate(90deg);
        -ms-transform: rotate(90deg);
        -webkit-transform: rotate(90deg); 
      }
    50%{  
        transform: rotate(180deg);
        -ms-transform: rotate(180deg); 
        -webkit-transform: rotate(180deg);
      }
    75%{  
        transform: rotate(270deg);
        -ms-transform: rotate(270deg);
        -webkit-transform: rotate(270deg); 
      }
    100%{ 
        transform: rotate(360deg);
        -ms-transform: rotate(360deg);
        -webkit-transform: rotate(360deg); 
    }
  }

  .loading-text span{
    display:inline-block;
    width:10px;
    height:10px;
    border-radius:50%;
    background:#ccc;
    margin:0 10px;
    -webkit-animation-duration:1s;
    -webkit-animation-timing-function: linear;
    -webkit-animation-iteration-count: infinite;
    -webkit-animation-direction:normal;
    -webkit-animation-play-state: running;
  }
  .loading-text span:nth-child(1){
    -webkit-animation-name:jump1;
    -webkit-animation-delay:0.1s;
  }
  .loading-text span:nth-child(2){
    -webkit-animation-name:jump2;
    -webkit-animation-delay:0.2s;
    background-color:#f1f1f1
  }
  .loading-text span:nth-child(3){
    -webkit-animation-name:jump3;
    -webkit-animation-delay:0.3s;
  }
  .loading-text span:nth-child(4){
    -webkit-animation-name:jump4;
    -webkit-animation-delay:0.4s;
    background-color:#f1f1f1
  }
  @keyframes jump1
  {
    0%{ 
        transform: translate(0,20px);
        -ms-transform: translate(0,20px);
        -webkit-transform: translate(0,20px);
      }
    50%{ 
        transform: translate(0,-20px);
        -ms-transform: translate(0,-20px);
        -webkit-transform: translate(0,-20px);
      }
    100%{ 
        transform: translate(0,20px);
        -ms-transform: translate(0,20px);
        -webkit-transform: translate(0,20px);
    }
  }
  @keyframes jump2
  {
    0%{ 
        transform: translate(0,15px);
        -ms-transform: translate(0,15px);
        -webkit-transform: translate(0,15px);
      }
    50%{ 
        transform: translate(0,-15px);
        -ms-transform: translate(0,-15px);
        -webkit-transform: translate(0,-15px);
      }
    100%{ 
        transform: translate(0,15px);
        -ms-transform: translate(0,15px);
        -webkit-transform: translate(0,15px);
    }
  }
  @keyframes jump3
  {
    0%{ 
        transform: translate(0,10px);
        -ms-transform: translate(0,10px);
        -webkit-transform: translate(0,10px);
      }
    50%{ 
        transform: translate(0,-10px);
        -ms-transform: translate(0,-10px);
        -webkit-transform: translate(0,-10px);
      }
    100%{ 
        transform: translate(0,10px);
        -ms-transform: translate(0,10px);
        -webkit-transform: translate(0,10px);
    }
  }
  @keyframes jump4
  {
    0%{ 
        transform: translate(0,5px);
        -ms-transform: translate(0,5px);
        -webkit-transform: translate(0,5px);
      }
    50%{ 
        transform: translate(0,-5px);
        -ms-transform: translate(0,-5px);
        -webkit-transform: translate(0,-5px);
      }
    100%{ 
        transform: translate(0,5px);
        -ms-transform: translate(0,5px);
        -webkit-transform: translate(0,5px);
    }
  }
</style>
