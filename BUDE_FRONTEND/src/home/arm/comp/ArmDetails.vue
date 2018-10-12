<template>
  <div class="armDetails">
    <!--标题-->
    <div class="header">
      <span class="title">中转端</span>
      <div class="options" style="float:right;">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item>
            <el-input v-model="formInline.armName" placeholder="中转端名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="selectArm">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" @click="btn_add">新增</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!--详情-->
    <div class="detail">
      <el-scrollbar style="height:100%;">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="6" v-for="item in finalData" :key="item.armName">
            <div class="item">
              <div class="itemHeader">
                <span class="armName">{{item.armName}}</span>
                <el-dropdown trigger="click" style="margin-right:10px;cursor:pointer">
                    <span class="el-dropdown-link">
                      操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="edit" @click.native="btn_editBody(item.armName,item.bodyId)"><i
                      class="el-icon-edit" style="margin-right:5px;"></i>更换中转组
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" @click.native="btn_delete(item.armName)"><i
                      class="el-icon-delete" style="margin-right:5px;"></i>删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <div class="itemMain">
                <div class="box">
                  <div class="armMain">
                    <div>
                      <el-col :span="24">
                        <span style="margin-right:20px;">所属中转组</span><span>{{item.bodyName}}</span>
                      </el-col>
                    </div>
                    <div>
                      <el-col :span="12">
                        <span style="margin-right:10px;">端口</span><span>{{item.serverPort}}</span>
                      </el-col>
                      <el-col :span="12">
                        <div v-if="item.status"><span class="icon_online"></span><span>在线</span></div>
                        <div v-else><span class="icon_dropline"></span><span>掉线</span></div>
                      </el-col>
                    </div>
                  </div>
                  <ArmFingerInfo :fingerData="item"></ArmFingerInfo>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-scrollbar>
    </div>
    <!--分页-->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[4,8,12,16]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      layout="total,sizes,prev,pager,next, jumper"
      ref="pagination"
      :total="totallyData">
    </el-pagination>
  </div>
</template>

<script>
  import ArmFingerInfo from './ArmFingerInfo'
  import {axiosPost} from '@/utils/fetchData'
  import {
    selectArmUrl,
    deleteArmUrl,
    selectBodyUrl,
    selectArmFingerUrl
  } from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import Bus from '@/utils/bus'
  import {linkeBeadName, refreshHandle} from "@/utils/handle";

  export default {
    name: "ArmDetails",
    data() {
      return {
        currentPage: 1,
        // 每页项数
        pageSize: 8,
        // 总项数
        totallyData: 0,
        // 查询表单
        formInline: {
          armName: ""
        },
        // 中转端是否在线
        isOnline: true,
        // 获取的中转端信息
        armInfo: [],
        // 获取的中转组信息
        bodyInfo: [],
        // 获取设备端信息
        fingerData: [],
        // 处理后的信息
        handleInfo:[],
        // 最终信息
        finalData:[],
        // 是否启用定时器
        isTimeOut: false,
        // 定时器
        myTimeOut: ""
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('armRefresh');
      // 清除定时器
      this.clearMyTimeOut();
    },
    created() {
      this.selectBody();
    },
    mounted() {
      // 监听刷新事件
      Bus.$on('armRefresh', (type) => {
        this.refresh(type);
      })
    },
    components: {
      ArmFingerInfo
    },
    methods: {
      ...mapActions(['setLoading']),
      // 查询所有中转组
      selectBody: function () {
        this.handleInfo = [];
        this.setLoading(true);
        let options = {
          url: selectBodyUrl,
          data: {}
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.bodyInfo = data.list;
            this.$nextTick(function () {
              this.selectArm();
            })
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 查询中转端
      selectArm: function () {
        this.handleInfo = [];
        if (this.isTimeOut === false) {
          this.setLoading(true);
        }
        let options = {
          url: selectArmUrl,
          data: {
            armName: this.formInline.armName,
            currentPage: this.currentPage,
            pageSize: this.pageSize
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.totallyData = data.totallyData;
            this.currentPage = data.currentPage;
            this.$refs.pagination.internalCurrentPage = data.currentPage;
            if (this.totallyData === 0) {
              this.$alertInfo("暂无数据");
            } else {
              this.armInfo = data.list;
              this.$nextTick(function () {
                this.handleFingerInfo(data.list);
              })
            }
          } else {
            this.$alertWarning(response.data.data);
          }
          this.formInline.armName = "";
        })
          .catch(err => {
            this.setLoading(false);
            this.formInline.armName = "";
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 查询中转端下设备端
      selectFinger: function (armName, index) {
        if (this.isTimeOut === false) {
          this.setLoading(true);
        }
        let options = {
          url: selectArmFingerUrl,
          data: {
            armName: armName
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            let data = response.data.data;
            this.$nextTick(function () {
              this.fingerData[index] = data.list;
            })
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 生成多个Promise查询设备端信息
      getPromise: function (list) {
        let promises = [];
        for (let i = 0; i < list.length; i++) {
          let item = list[i];
          promises.push(
            new Promise(function (resolve, reject) {
              setTimeout(resolve, 1000, Math.floor(Math.random() * 100) + 1);
            }).then(this.selectFinger(item.armName, i))
          );
        }
        return promises;
      },
      // 信息处理
      handleFingerInfo: function (list) {
        Promise.all(this.getPromise(list)).then(() => {
          for(let i =0;i<this.armInfo.length;i++){
            let item = this.armInfo[i];
            let bodyName = this.getBodyName(item.bodyId);
            let fingerList = this.fingerData[i];
            let onlineList = [];
            let onlineLen = 0;
            let droplineList = [];
            let droplineLen = 0;
            for(let j=0;j<fingerList.length;j++){
              let obj = fingerList[j];
              this.linkString(obj);
              if (obj.status === true) {
                onlineList.push(obj);
                onlineLen++;
              } else if (obj.status === false) {
                droplineList.push(obj);
                droplineLen++;
              }
            };
            let totalData = {
              num: fingerList.length,
              list: fingerList
            };
            let onlineData = {
              num: onlineLen,
              list: onlineList
            };
            let droplineData = {
              num: droplineLen,
              list: droplineList
            };
            let info = {
              armName: item.armName,
              bodyId: item.bodyId,
              bodyName: bodyName,
              serverPort: item.serverPort,
              status: item.status,
              totalData: totalData,
              onlineData: onlineData,
              droplineData: droplineData
            };
            this.handleInfo[i] = info;
          }
          this.finalData = this.handleInfo;
          this.setLoading(false);
          this.setMyTimeOut();
        });
      },
      // 根据bodyId查找bodyName
      getBodyName: function (bodyId) {
        let bodyName;
        if (bodyId !== null) {
          for (let i = 0; i < this.bodyInfo.length; i++) {
            if (bodyId === this.bodyInfo[i].id) {
              bodyName = this.bodyInfo[i].name;
              return bodyName;
            }
          }
        } else {
          bodyName = "无";
        }
        return bodyName;
      },
      // currentPage改变
      handleCurrentChange: function (currentPage) {
        this.clearMyTimeOut();
        this.currentPage = currentPage;
        this.selectArm();
      },
      // pageSize改变
      handleSizeChange: function (pageSize) {
        this.clearMyTimeOut();
        this.pageSize = pageSize;
        this.selectArm();
      },
      // 添加
      btn_add: function () {
        Bus.$emit('armAdd', true);
      },
      // 更换中转组
      btn_editBody: function (armName, bodyId) {
        let nowBodyId = bodyId === null ? "" : bodyId;
        Bus.$emit('editBody', armName, this.bodyInfo, nowBodyId);
      },
      // 删除按钮点击事件
      btn_delete: function (armName) {
        this.$confirm('是否删除该中转端?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: false
        }).then((action) => {
          if (action === "confirm") {
            this.delete(armName);
          }
        }).catch(() => {
          this.$alertInfo("已取消删除");
        });
      },
      // 删除
      delete: function (armName) {
        let options = {
          url: deleteArmUrl,
          data: {
            armName: armName
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess("删除成功");
            this.$nextTick(function () {
              this.refresh("delete");
            })
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 拼接软件包名
      linkString: function (obj) {
        let beadName = linkeBeadName(obj.headName, obj.faceName, obj.bead);
        obj["beadName"] = beadName;
      },
      // 刷新
      refresh: function (type) {
        this.clearMyTimeOut();
        refreshHandle(type, this);
        this.selectArm();
      },
      // 设置定时器
      setMyTimeOut: function () {
        this.isTimeOut = true;
        let that = this;
        this.myTimeOut = setTimeout(function () {
          that.selectArm();
        },128000)
      },
      // 清除定时器
      clearMyTimeOut: function () {
        this.isTimeOut = false;
        clearTimeout(this.myTimeOut);
        this.myTimeOut = null;
      }
    }
  }
</script>

<style scoped>
  .armDetails {
    width: 100%;
    height: 100%;
  }

  .header {
    min-width: 640px;
    height: 40px;
    line-height: 41px;
  }

  .header span.title {
    padding-left: 20px;
    border-left: 3px solid rgb(0, 176, 240);
    font-size: 20px;
    font-weight: bold;
    margin-right: 30px;
  }

  .detail {
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 100px);
    padding: 20px 20px;
    margin: 15px auto 10px;
    border-width: 1px;
    border-style: solid;
    border-color: rgb(235, 235, 235);
    border-image: initial;
    border-radius: 3px;
    transition: all 0.2s ease 0s;
    background-color: #fff;
  }

  .detail:hover {
    box-shadow: rgba(232, 237, 250, 0.6) 0px 0px 8px 0px,
    rgba(232, 237, 250, 0.5) 0px 2px 4px 0px;
  }

  .item {
    display: flex;
    flex-direction: column;
    width: 100%;
    min-width: 400px;
    border: 1px solid #ddd;
    color: #333;
  }

  .item .itemHeader {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 40px;
    background: #f9f9f9;
  }

  .item .itemHeader .armName {
    margin-left: 10px;
  }

  .item .itemMain {
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 40px);
    padding: 0 20px;
  }

  .box {
    width: 100%;
    font-size: 16px;
  }

  .box .armMain {
    box-sizing: border-box;
    width: 100%;
    color: #777;
  }

  .armMain > div {
    width: 100%;
    height: 40px;
    line-height: 40px;
    border-bottom: 1px solid #ddd;
  }

  .armMain .el-col div {
    display: flex;
    align-items: center;
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

  .el-row > .el-col {
    margin-bottom: 20px;
  }
</style>
