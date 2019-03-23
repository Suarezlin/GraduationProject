
function uploadVideo() {
  wx.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 60,
    camera: 'back',
    compressed: false,
    success(res) {
      //console.log(res);
      let duration = res.duration;
      let height = res.height;
      let size = res.size;
      let tempFilePath = res.tempFilePath;
      let thumbTempFilePath = res.thumbTempFilePath;
      let width = res.width;

      if (duration > 61) {
        Notify({
          text: "上传视频时长不能超过一分钟",
          duration: 3000,
          backgroundColor: "#FF4444"
        });
      } else {
        // TODO: 打开选择 BGM 页面
        wx.navigateTo({
          url: `/pages/chooseBgm/chooseBgm?duration=${duration}&height=${height}&size=${size}&tempFilePath=${tempFilePath}&thumbTempFilePath=${thumbTempFilePath}&width=${width}`,
        })
      }
    }
  });
};

const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${formatNumber(year)}-${formatNumber(month)}-${formatNumber(day)}`
  // return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
};

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
};

module.exports = {
  uploadVideo: uploadVideo,
  formatTime: formatTime,
}

