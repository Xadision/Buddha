<template>
  <div class="fingerDetails">
    <!--标题-->
    <div class="header">
      <span class="title">设备端</span>
      <el-form :inline="true">
        <el-form-item label="中转端" style="margin-bottom:0px">
          <el-select v-model="armName" filterable placeholder="请选择中转端" @change="selectChange">
            <el-option
              v-for="item in armInfo"
              :key="item.id"
              :label="item.armName"
              :value="item.armName">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!--详情-->
    <div class="details">
      <el-form :inline="true" class="demo-form-inline" :model="selectInfo" style="min-width:1150px;">
        <el-form-item label="设备端名">
          <el-input placeholder="设备端名" v-model="selectInfo.fingerName"></el-input>
        </el-form-item>
        <el-form-item label="软件包">
          <el-input placeholder="如:SMT-display_2.0.0_201808091002" style="width:300px;"
                    v-model="selectInfo.beadName"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select placeholder="状态" v-model="selectInfo.status">
            <el-option value="0" label="不限"></el-option>
            <el-option value="1" label="在线"></el-option>
            <el-option value="2" label="掉线"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="btn_find">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-plus" @click="btn_add">新增</el-button>
        </el-form-item>
      </el-form>
      <el-scrollbar style="height:calc(100% - 62px);">
        <el-table
          stripe
          border
          center="true"
          :data="tableData"
          :empty-text="emptyText"
          style="width: 100%;">
          <el-table-column
            prop="fingerName"
            label="设备端名"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="beadName"
            label="软件包"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            align="center"
          >
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <span @click="btn_delete(scope.$index, scope.row)" style="cursor:pointer;font-size:18px;" title="删除设备端"><i
                class="el-icon-delete"></i></span>
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </div>
    <!--分页-->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10,20,30,40]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      layout="total,sizes,prev,pager,next, jumper"
      ref="pagination"
      :total="totallyData">
    </el-pagination>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {
    selectArmUrl,
    selectArmFingerUrl,
    deleteFingerUrl
  } from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import {linkeBeadName, handleBeadName} from '@/utils/handle.js'
  import {getBeadInfo} from "@/utils/getInfo"
  import Bus from '@/utils/bus'
  import {refreshHandle} from "@/utils/handle";

  export default {
    name: "FingerDetail",
    data() {
      return {
        currentPage: 1,
        // 每页项数
        pageSize:10,
        // 总项数
        totallyData: 0,
        // 中转端信息
        armInfo: [],
        // 设备端信息
        fingerInfo: [],
        // 软件包信息
        beadInfo: [],
        // 中转端名
        armName: "",
        // 查询信息
        selectInfo: {
          beadName: "",
          fingerName: "",
          status: ""
        },
        // 表格无数据时的显示信息
        emptyText: "暂无数据",
        // 处理后的信息
        handleInfo:[],
        // 表格信息
        tableData: [],
        // 定时器
        myTimeOut:"",
        // 是否处于定时器期间
        isTimeOut:false
      }
    },
    beforeDestroy(){
      // 取消监听
      Bus.$off('fingerRefresh');
      // 取消定时器
      this.clearMyTimeOut();
    },
    mounted() {
      // 加载软件包信息
      this.beadInfo = getBeadInfo();
      // 加载中转端信息
      this.selectArm();
      // 监听刷新事件
      Bus.$on('fingerRefresh',(armName,type) => {
        this.armName = armName;
        this.refresh(type,armName);
      })
    },
    methods: {
      ...mapActions(['setLoading']),
      // currentPage改变
      handleCurrentChange: function (currentPage) {
        this.clearMyTimeOut();
        this.currentPage = currentPage;
        this.selectFinger(this.armName);
      },
      // pageSize改变
      handleSizeChange: function (pageSize) {
        this.clearMyTimeOut();
        this.pageSize = pageSize;
        this.selectFinger(this.armName);
      },
      // 查询中转端
      selectArm: function () {
        this.setLoading(true);
        let options = {
          url: selectArmUrl,
          data: {}
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.armInfo = data.list;
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 查询中转端下的设备端
      selectFinger: function (armName) {
        this.tableData = [];
        if(this.isTimeOut === false){
          this.setLoading(true);
        }
        let options = {
          url: selectArmFingerUrl,
          data: {
            armName: armName,
            currentPage:this.currentPage,
            pageSize:this.pageSize
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.fingerInfo = data.list;
            this.$nextTick(function () {
              if (this.fingerInfo.length <= 0) {
                this.emptyText = "该中转端下无相关设备端"
              } else {
                this.totallyData = data.totallyData;
                this.currentPage = data.currentPage;
                this.$refs.pagination.internalCurrentPage = data.currentPage;
                this.handleFingerInfo();
              }
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
      // 删除设备端
      deleteFinger: function (fingerName) {
        let options = {
          url: deleteFingerUrl,
          data: {
            fingerName:fingerName,
            armName:this.armName
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess("删除成功");
            this.$nextTick(function () {
              this.refresh("delete",this.armName);
            })
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 设备端信息处理
      handleFingerInfo: function () {
        this.handleInfo  = [];
        for (let i = 0; i < this.fingerInfo.length; i++) {
          let finger = this.fingerInfo[i];
          let fingerName = finger.name;
          let beadName = linkeBeadName(finger.headName, finger.faceName, finger.bead);
          let status = finger.status === true ? "在线" : "掉线";
          let obj = {
            fingerName: fingerName,
            beadName: beadName,
            status: status
          };
          this.handleInfo.push(obj);
        }
        this.tableData = this.handleInfo;
        this.setMyTimeOut();
      },
      // 点击删除设备端按钮
      btn_delete: function (index, row) {
        this.$confirm('是否删除该设备端?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: false
        }).then((action) => {
          if (action === "confirm") {
            this.deleteFinger(row.fingerName);
          }
        }).catch(() => {
          this.$alertInfo("已取消删除");
        });
      },
      // 点击新增按钮
      btn_add: function () {
        if (this.armName === ""){
          this.$alertInfo("请先选择中转端");
        }else{
          Bus.$emit("fingerAdd",this.armName,this.beadInfo);
        }
      },
      // 点击查询按钮
      btn_find:function(){
        this.$alertWarning("暂未开发");
      },
      // 中转端值变化
      selectChange: function (val) {
        this.clearMyTimeOut();
        this.currentPage = 1;
        this.selectFinger(val);
      },
      // 刷新
      refresh:function(type,armName){
        this.clearMyTimeOut();
        refreshHandle(type,this);
        this.selectFinger(armName);
      },
      // 建立定时器
      setMyTimeOut:function(){
        if(this.armName !== ""){
          this.isTimeOut = true;
          let that = this;
          this.myTimeOut = setTimeout(function () {
            that.selectFinger(that.armName);
          },128000)
        }
      },
      // 清除定时器
      clearMyTimeOut:function(){
        this.isTimeOut = false;
        clearTimeout(this.myTimeOut);
        this.myTimeOut = null;
      }
    }
  }
</script>

<style scoped>
  .fingerDetails {
    width: 100%;
    height: 100%;
  }
  .header {
    min-width:400px;
  }
  .header span.title {
    display:inline-block;
    padding-left: 20px;
    border-left: 3px solid rgb(0, 176, 240);
    font-size: 20px;
    font-weight: bold;
    margin-bottom:15px;
  }
  .details {
    box-sizing: border-box;
    width: 100%;
    min-width: 1150px;
    height: calc(100% - 125px);
    padding: 20px 20px;
    margin: 10px auto 10px;
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
</style>
