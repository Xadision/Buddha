<template>
  <div class="faceDetails">
    <!--标题-->
    <div class="header">
      <span class="title">模块</span>
      <FaceCascader @isFind="find"></FaceCascader>
      <el-button class="button-new-tag" size="small" type="info" @click="btn_publish">发布软件包</el-button>
      <el-dropdown style="float:right;margin-top:1px"  trigger="click">
        <el-button type="primary" size="small" class="el-dropdown-link">
          更多操作<i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="add" @click.native="btn_add"><i class="el-icon-plus" style="margin-right:5px;"></i>添加模块
          </el-dropdown-item>
          <el-dropdown-item command="edit" @click.native="btn_edit"><i class="el-icon-edit"
                                                                       style="margin-right:5px;"></i>编辑模块
          </el-dropdown-item>
          <el-dropdown-item command="delete" @click.native="btn_delete"><i class="el-icon-delete"
                                                                           style="margin-right:5px;"></i>删除模块
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!--详情-->
    <div class="detail">
      <el-scrollbar style="height:100%;">
        <el-table
          stripe
          border
          center="true"
          :data="tableData"
          @selection-change="handleSelectionChange"
          style="width:100%;">
          <el-table-column
            type="selection"
            width="55"
            align="center">
          </el-table-column>

          <el-table-column
            prop="name"
            label="设备名"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="armName"
            label="中转端名"
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
            label="设备端状态"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="update"
            label="软件包发布状态"
            align="center"
          >
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <span @click="single_btn_publish(scope.$index, scope.row)" style="cursor:pointer" title="发布软件包"><icon
                name="publish" scale="2.5"></icon></span>
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
      ref="pagination"
      layout="total,sizes,prev,pager,next, jumper"
      :total="totallyData">
    </el-pagination>
  </div>
</template>

<script>
  import Bus from '@/utils/bus'
  import {axiosPost} from '@/utils/fetchData'
  import {selectFingerUrl, deleteFaceUrl, publishUrl, selectBeadUrl} from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import FaceCascader from './FaceCascader'

  export default {
    name: "faceDetail",
    data() {
      return {
        obj: {},
        // 表格数据
        tableData: [],
        // 当前页面
        currentPage: 1,
        // 每页项数
        pageSize: 10,
        // 总项数
        totallyData: 0,
        // 查询结果
        list: [],
        // 处理结果
        handleInfo:[],
        publishData: [],
        beadList: [],
        fingerName: "",
        armName:"",
        // 是否启用定时器
        isTimeOut: false,
        // 定时器
        myTimeOut: ""
      }
    },
    components: {
      FaceCascader
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off("beadPublishFinish");
      // 取消定时器
      this.clearMyTimeOut();
    },
    mounted() {
      // 监听软件包发布事件
      Bus.$on("beadPublishFinish", (beadId, type) => {
        if (type === 0) {
          for (let i = 0; i < this.publishData.length; i++) {
            let obj = this.publishData[i];
            this.publish(beadId, obj.name,obj.armName);
          }
          this.selectFinger();
        } else if (type === 1) {
          this.publish(beadId, this.fingerName,this.armName);
          this.selectFinger();
        }
      });
    },
    methods: {
      ...mapActions(['setLoading']),
      find: function (obj) {
        this.clearMyTimeOut();
        this.obj = obj;
        this.$nextTick(function () {
          this.selectFinger();
          this.selectBead(obj.faceId)
        })
      },
      // 查询设备端
      selectFinger: function () {
        if(this.isTimeOut === false){
          this.setLoading(true);
        }
        let options = {
          url: selectFingerUrl,
          data: {
            faceId: this.obj.faceId,
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
            this.list = data.list;
            this.$nextTick(function () {
              this.initTableData();
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
      // 初始化表格数据
      initTableData: function () {
        this.handleInfo = [];
        for (let i = 0; i < this.list.length; i++) {
          let obj = this.list[i];
          let status;
          if(obj.status === true){
            status = "在线"
          }else{
            status = "掉线"
          }
          let beadName = obj.headName + '-' + obj.faceName + '_' + obj.bead.firstCode + '.' + obj.bead.secondCode + '.' +
            obj.bead.debugCode + '_' + obj.bead.suffixTime;
          let item = {
            beadId: obj.beadId,
            name: obj.name,
            armName: obj.armName,
            beadName: beadName,
            status:status,
            update:obj.update
          };
          this.handleInfo.push(item);
        }
        this.tableData = this.handleInfo;
        this.setMyTimeOut();
      },
      // currentPage改变
      handleCurrentChange: function (currentPage) {
        this.clearMyTimeOut();
        this.currentPage = currentPage;
        this.selectFinger();
      },
      // pageSize改变
      handleSizeChange: function (pageSize) {
        this.clearMyTimeOut();
        this.pageSize = pageSize;
        this.selectFinger();
      },
      // 多选按钮点击
      handleSelectionChange: function (arr) {
        this.publishData = [];
        for (let i = 0; i < arr.length; i++) {
          let obj = arr[i];
          this.publishData.push(obj);
        }
      },
      // 点击添加按钮
      btn_add: function () {
        Bus.$emit('faceAdd', this.obj);
      },
      // 点击删除按钮
      btn_delete: function () {
        if (this.obj.faceId) {
          this.delete();
        } else {
          this.$alertInfo("请先选择想要删除的模块");
        }
      },
      // 点击编辑按钮
      btn_edit: function () {
        if (this.obj.faceId) {
          Bus.$emit('faceEdit', this.obj);
        } else {
          this.$alertInfo("请先选择想要编辑的模块");
        }
      },
      // 删除
      delete: function () {
        let options = {
          url: deleteFaceUrl,
          data: {
            faceId: this.obj.faceId
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess('删除成功');
            Bus.$emit('isCascaderRefresh', "delete", this.obj);
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 点击发布软件包按钮
      btn_publish: function () {
        if (this.publishData.length > 0) {
          for (let i = 0; i < this.publishData.length; i++) {
            if (this.publishData[i].status === "掉线") {
              this.$alertWarning("不能勾选掉线状态的设备端");
              break;
            }
            if (i === this.publishData.length - 1) {
              this.clearMyTimeOut();
              Bus.$emit("fingerPublish", this.obj, this.beadList, 0);
            }
          }
        }
      },
      // 发布软件包
      publish: function (beadId, name,armName) {
        let options = {
          url: publishUrl,
          data: {
            beadId: beadId,
            fingerName: name,
            armName:armName
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertInfo(response.data.data);
          } else {
            this.$alertWarning(name + '发布出错,错误信息:' + response.data.data);
          }
          this.closePublishDialog();
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
            this.closePublishDialog();
          })
      },
      // 查询软件包信息
      selectBead: function (faceId) {
        this.setLoading(true);
        let options = {
          url: selectBeadUrl,
          data: {
            faceId: faceId
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.beadList = data;
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 单项上传
      single_btn_publish: function (index, row) {
        this.fingerName = row.name;
        this.armName = row.armName;
        if(row.status === "掉线"){
          this.$alertWarning("掉线状态的设备端不能发布软件包");
        }else{
          this.clearMyTimeOut();
          Bus.$emit("fingerPublish", this.obj, this.beadList, 1);
        }
      },
      // 关闭上传软件包模态框
      closePublishDialog: function () {
        Bus.$emit("closePublishDialog", true);
      },
      // 设置定时器
      setMyTimeOut: function () {
        this.isTimeOut = true;
        let that = this;
        this.myTimeOut = setTimeout(function () {
          that.selectFinger();
        },10000)
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
  .faceDetails {
    width: 100%;
    height: 100%;
  }

  .header {
    min-width: 530px;
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

  .header span {
    margin-right: 10px;
  }

  .header .el-dropdown-default {
    float: right;
    cursor: pointer;
  }

  /**/
  .detail {
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 100px);
    padding: 20px 20px;
    margin: 20px auto 10px;
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
</style>
