<template>
  <div class="bodyDetails">
    <!--标题-->
    <div class="header">
      <span class="title">中转组</span>
      <div class="options" style="float:right;">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item>
            <el-input v-model="formInline.bodyName" placeholder="中转组名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="btn_find">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-plus" @click="btn_add">新增</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!--详情-->
    <div class="details">
      <el-scrollbar style="height:100%;">
        <el-row :gutter="10">
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="4" v-for="item in finalData" :key="item.bodyId">
            <div class="item">
              <div class="itemHeader">
                <span class="bodyName">{{item.bodyName}}</span>
                <el-dropdown trigger="click" style="margin-right:10px;cursor:pointer">
                    <span class="el-dropdown-link">
                      操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="edit" @click.native="btn_edit(item.bodyId,item.bodyName)"><i
                      class="el-icon-edit" style="margin-right:5px;"></i>修改
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" @click.native="btn_delete(item.bodyId)"><i
                      class="el-icon-delete" style="margin-right:5px;"></i>删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <div class="itemMain">
                <div class="total" style="cursor:pointer" @click="showArmInfo('all',item.totalArmData,item.bodyName)"><span>中转端</span><span
                  style="margin-left:15px;font-size:22px;color:#409EFF">{{item.totalArmData.num}}</span></div>
                <div class="classify">
                  <div style="cursor:pointer" @click="showArmInfo('online',item.onlineArmData,item.bodyName)"><span
                    class="icon_online"></span><span>在线</span><span
                    style="color:#67C23A">{{item.onlineArmData.num}}</span>
                  </div>
                  <div style="cursor:pointer" @click="showArmInfo('dropline',item.droplineArmData,item.bodyName)"><span
                    class="icon_dropline"></span><span>掉线</span><span
                    style="color:#F56C6C">{{item.droplineArmData.num}}</span>
                  </div>
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
      ref="pagination"
      layout="total,sizes,prev,pager,next, jumper"
      :total="totallyData">
    </el-pagination>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {
    deleteBodyUrl,
    selectBodyArmUrl,
    selectBodyUrl,
  } from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import Bus from '@/utils/bus'
  import {refreshHandle} from "../../../utils/handle";

  export default {
    name: "BodyDetail",
    data() {
      return {
        currentPage:1,
        // 每页项数
        pageSize:4,
        // 总项数
        totallyData: 0,
        // 查询表单
        formInline: {
          bodyName: ""
        },
        // 中转组信息
        bodyInfo: [],
        // 中转端信息
        armData:[],
        // 处理后的信息
        handleInfo: [],
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
      Bus.$off('bodyRefresh');
      // 清除定时器
      this.clearMyTimeOut();
    },
    mounted() {
      this.selectBody();
      // 监听刷新事件
      Bus.$on('bodyRefresh', (type) => {
        this.refresh(type);
      })
    },
    methods: {
      ...mapActions(['setLoading']),
      // currentPage改变
      handleCurrentChange: function (currentPage) {
        this.clearMyTimeOut();
        this.currentPage = currentPage;
        this.selectBody();
      },
      // pageSize改变
      handleSizeChange: function (pageSize) {
        this.clearMyTimeOut();
        this.pageSize = pageSize;
        this.selectBody();
      },
      // 查询中转组
      selectBody: function () {
        this.handleInfo = [];
        if(this.isTimeOut === false){
          this.setLoading(true);
        }
        let options = {
          url: selectBodyUrl,
          data: {
            currentPage: this.currentPage,
            pageSize: this.pageSize
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            let data = response.data.data;
            this.totallyData = data.totallyData;
            this.currentPage = data.currentPage;
            this.$refs.pagination.internalCurrentPage = data.currentPage;
            this.bodyInfo = data.list;
            this.$nextTick(function () {
              this.handleArmInfo(data.list);
            })
          } else {
            this.setLoading(false);
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 查询中转组下中转端
      selectArm: function (bodyId, index) {
        let options = {
          url: selectBodyArmUrl,
          data: {
            bodyId: bodyId
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.armData[index] = data.list;
          } else {
            this.setLoading(false);
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 删除中转组
      deleteArm: function (bodyId) {
        let options = {
          url: deleteBodyUrl,
          data: {
            bodyId: bodyId
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
      // 生成多个Promise查询设备端信息
      getPromise: function (list) {
        let promises = [];
        for (let i = 0; i < list.length; i++) {
          let body = list[i];
          promises.push(
            new Promise(function (resolve, reject) {
              setTimeout(resolve, 1000, Math.floor(Math.random() * 100) + 1);
            }).then(this.selectArm(body.id, i))
          );
        }
        return promises;
      },
      // 数据处理
      handleArmInfo: function (list) {
        Promise.all(this.getPromise(list)).then(() => {
          for(let i =0;i<this.bodyInfo.length;i++){
            let item = this.bodyInfo[i];
            let armList = this.armData[i];
            let onlineList = [];
            let onlineLen = 0;
            let droplineList = [];
            let droplineLen = 0;
            for(let j=0;j<armList.length;j++){
              let obj = armList[j];
              if (obj.status === true) {
                onlineList.push(obj);
                onlineLen++;
              } else if (obj.status === false) {
                droplineList.push(obj);
                droplineLen++;
              }
            };
            let totalData = {
              num: armList.length,
              list: armList
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
              bodyName:item.name,
              bodyId: item.id,
              totalArmData: totalData,
              onlineArmData: onlineData,
              droplineArmData: droplineData
            };
            this.handleInfo[i] = info;
          }
          this.finalData = this.handleInfo;
          this.setLoading(false);
          this.setMyTimeOut();
        });
      },
      // 点击删除中转组按钮
      btn_delete: function (bodyId) {
        this.$confirm('是否删除该中转组?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: false
        }).then((action) => {
          if (action === "confirm") {
            this.deleteArm(bodyId);
          }
        }).catch(() => {
          this.$alertInfo("已取消删除");
        });
      },
      // 点击修改中转组按钮
      btn_edit: function (bodyId, bodyName) {
        Bus.$emit('bodyEdit',bodyId,bodyName);
      },
      // 点击查看中转端信息
      showArmInfo: function (type,data,bodyName) {
        if(data.num <= 0){
          this.$alertInfo("无相关中转端信息");
        }else{
          Bus.$emit('showArmInfo',type,data,bodyName);
        }
      },
      // 点击查询中转组按钮
      btn_find:function(){
        this.$alertInfo("暂未开发");
      },
      // 点击添加中转组按钮
      btn_add:function(){
        Bus.$emit('bodyAdd',true);
      },
      // 刷新
      refresh:function(type){
        this.clearMyTimeOut();
        refreshHandle(type, this);
        this.selectBody();
      },
      // 设置定时器
      setMyTimeOut: function () {
        this.isTimeOut = true;
        let that = this;
        this.myTimeOut = setTimeout(function () {
          that.selectBody();
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
  .bodyDetails {
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

  .details {
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

  .details:hover {
    box-shadow: rgba(232, 237, 250, 0.6) 0px 0px 8px 0px,
    rgba(232, 237, 250, 0.5) 0px 2px 4px 0px;
  }

  .item {
    display: flex;
    flex-direction: column;
    width: 100%;
    border: 1px solid #ddd;
    color: #333;
    margin-bottom: 10px;
  }

  .item .itemHeader {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 40px;
    background: #f9f9f9;
  }

  .item .itemHeader .bodyName {
    margin-left: 10px;
  }

  .item .itemMain {
    display: flex;
    align-items: center;
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 40px);
    padding: 20px 20px;
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

  @media screen and (max-width: 450px) {
    .item {
      min-width: 400px;
    }
  }
</style>
