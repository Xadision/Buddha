export const linkeBeadName = function (headName, faceName, bead) {
  let beadName = headName + '-' + faceName + '_' + bead.firstCode + '.' + bead.secondCode + '.' +
    bead.debugCode + '_' + bead.suffixTime;
  return beadName;
}
export const handleBeadName = function (str) {
  let infoStr1 = str.split('-');
  let headName = infoStr1[0];
  let infoStr2 = infoStr1.split('_');
  let faceName = infoStr2[0];
  let suffixTime = infoStr2[2];
  let infoStr3 = infoStr2.split('.');
  let firstCode = infoStr3[0];
  let secondCode = infoStr3[1];
  let debugCode = infoStr3[2];
  let obj = {
    headName: headName,
    faceName: faceName,
    firstCode: firstCode,
    secondCode: secondCode,
    debugCode: debugCode,
    suffixTime: suffixTime
  };
  return obj;
}
export const judgeCodeLen = function (str) {
  let len = 0;
  for (let i = 0; i < str.length; i++) {
    let c = str.charCodeAt(i);
    //单字节加1
    if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
      len++;
    }
    else {
      len += 2;
    }
  }
  if (len <= 64) {
    return true;
  } else {
    return false;
  }
}
export const refreshHandle = function (type, that) {
  let num = parseInt(that.totallyData / that.pageSize);
  let index = that.totallyData % that.pageSize;
  if (type === "delete") {
    if (index === 1 && that.currentPage !== 1) {
      that.currentPage--;
    }
  } else if (type === "add") {
    if (index !== 0) {
      that.currentPage = num + 1;
      that.$refs.pagination.internalCurrentPage = that.currentPage;
    } else {
      that.currentPage++;
      that.$refs.pagination.internalCurrentPage = that.currentPage;
    }
  }
}
