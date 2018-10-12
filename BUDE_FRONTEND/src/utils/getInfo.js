import {axiosPost} from '@/utils/fetchData'
import {selectFaceUrl, selectHeadUrl, selectBeadUrl} from '@/config/globalUrl'
import Vue from 'vue'
import {linkeBeadName} from "./handle";
import store from '@/store'

let headData = [];
let faceData = [];
let beadData = [];
let finalData = [];
export const getFaceInfo = function () {
  headData = [];
  faceData = [];
  finalData = [];
  let str = "face";
  selectHead(str);
  store.commit("setLoading", false);
  return finalData;
}

export const getBeadInfo = function () {
  faceData = [];
  beadData = [];
  let str = "bead";
  selectHead(str);
  store.commit("setLoading", false);
  return beadData;
}

// 查询所有项目
function selectHead(str) {
  store.commit("setLoading", true);
  let options = {
    url: selectHeadUrl
  };
  axiosPost(options).then(response => {
    if (response.data.result === 200) {
      let data = response.data.data.list;
      headData = data;
      store.commit('setHeadList', data);
      handleInfo(data,str);
    } else {
      store.commit("setLoading",false);
      new Vue().$alertWarning('操作失败');
    }
  })
    .catch(err => {
      store.commit("setLoading", false);
      new Vue().$alertError('请求超时，请刷新重试');
    })
}

// 查询所有模块
function selectFace(headId, str, index) {
  let options = {
    url: selectFaceUrl,
    data: {
      headId: headId
    }
  };
  axiosPost(options).then(response => {
    if (response.data.result === 200) {
      let data = response.data.data.list;
      faceData[index] = data;
    } else {
      store.commit("setLoading", false);
      new Vue().$alertWarning('操作失败');
    }
  })
    .catch(err => {
      store.commit("setLoading", false);
      new Vue().$alertError('请求超时，请刷新重试');
    })
}

// 查询所有软件包信息
function selectBead(headName, faceName, faceId) {
  let options = {
    url: selectBeadUrl,
    data: {
      faceId: faceId
    }
  };
  axiosPost(options).then(response => {
    if (response.data.result === 200) {
      let data = response.data.data.list;
      initBead(headName, faceName, data);
    } else {
      store.commit("setLoading", false);
      new Vue().$alertWarning('操作失败');
    }
  })
    .catch(err => {
      store.commit("setLoading", false);
      new Vue().$alertError('请求超时，请刷新重试');
    })
}

// 生成多个Promise查询设备端信息
function getPromise(list, str) {
  let promises = [];
  for (let i = 0; i < list.length; i++) {
    let head = list[i];
    promises.push(
      new Promise(function (resolve, reject) {
        setTimeout(resolve, 1000, Math.floor(Math.random() * 100) + 1);
      }).then(selectFace(head.id, str, i))
    );
  }
  return promises;
}

// 数据处理
function handleInfo(list, str) {
  Promise.all(getPromise(list, str)).then(() => {
    for (let i = 0; i < list.length; i++) {
      let head = list[i];
      let faceList = faceData[i];
      let children = [];
      for (let j = 0; j < faceList.length; j++) {
        let obj = {
          value: faceList[j].id,
          label: faceList[j].name
        };
        children.push(obj);
        if (str === "bead") {
          let headName = head.name;
          let faceName = faceList[j].name;
          let faceId = faceList[j].id;
          selectBead(headName, faceName, faceId);
        }
      }
      let info = {
        value:head.id,
        label:head.name,
        children:children
      };
      finalData.push(info);
    }
  });
}

// 加载软件包
function initBead(headName,faceName,beadList){
  for(let i=0;i<beadList.length;i++){
    let bead = beadList[i];
    let beadName = linkeBeadName(headName,faceName,bead);
    let obj = {
      id:bead.id,
      name:beadName
    }
    beadData.push(obj);
  }
}

