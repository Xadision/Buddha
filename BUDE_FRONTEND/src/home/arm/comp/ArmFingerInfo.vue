<template>
  <el-col :span="24">
    <div class="fingerInfo">
      <div class="total" style="cursor:pointer" @click="showFingerInfo('all')"><span>设备端</span><span
        style="margin-left:15px;font-size:22px;color:#409EFF">{{fingerData.totalData.num}}</span></div>
      <div class="classify">
        <div style="cursor:pointer" @click="showFingerInfo('online')"><span
          class="icon_online"></span><span>在线</span><span style="color:#67C23A">{{fingerData.onlineData.num}}</span>
        </div>
        <div style="cursor:pointer" @click="showFingerInfo('dropline')"><span
          class="icon_dropline"></span><span>掉线</span><span style="color:#F56C6C">{{fingerData.droplineData.num}}</span>
        </div>
      </div>
    </div>
  </el-col>
</template>

<script>
  import Bus from '@/utils/bus'

  export default {
    name: "ArmFingerInfo",
    props: ['fingerData'],
    methods: {
      // 显示设备端信息
      showFingerInfo: function (type) {
        switch (type) {
          case "all":
            if (this.fingerData.totalData.num <= 0) {
              this.$alertInfo("无相关设备端信息");
            } else {
              Bus.$emit('showFingerInfo', type, this.fingerData);
            }
            break;
          case "online":
            if (this.fingerData.onlineData.num <= 0) {
              this.$alertInfo("无相关设备端信息");
            } else {
              Bus.$emit('showFingerInfo', type, this.fingerData);
            }
            break;
          case "dropline":
            if (this.fingerData.droplineData.num <= 0) {
              this.$alertInfo("无相关设备端信息");
            } else {
              Bus.$emit('showFingerInfo', type, this.fingerData);
            }
            break;
          default:
            break;
        }
      }
    }
  }
</script>

<style scoped>
  .fingerInfo {
    display: flex;
    align-items: center;
    padding: 20px 0;
  }

  .total {
    display: flex;
    height: 100%;
    align-items: center;
    flex: 1;
  }

  .classify {
    margin-left: -20px;
    padding-left: 33px;
    border-left: 1px solid #ddd;
    flex: 1;
  }

  .classify div {
    display: flex;
    align-items: center;
    margin: 15px 0;
  }

  .classify div span:last-child {
    margin-left: 10px;
  }

  .icon_online, .icon_dropline {
    display: inline-block;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    margin-right: 10px;
  }

  .icon_online {
    background: #67C23A;
  }

  .icon_dropline {
    background: #F56C6C;
  }
</style>
