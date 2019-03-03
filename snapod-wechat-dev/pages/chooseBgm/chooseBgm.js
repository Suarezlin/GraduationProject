const app = getApp();
const innerAudioContext = wx.createInnerAudioContext();
import Notify from '../../dist/notify/notify';


Page({
  data: {
    description: "",
    radio: "noBgm",
    serverUrl: app.serverUrl,
    play: [],
    videoParams: {},
  },

  onReady: function() {

  },

  onUnload: function() {
    innerAudioContext.stop();
  },

  onHide: function() {
    innerAudioContext.stop();
  },

  onDescChange: function(e) {
    this.setData({
      description: e.detail
    });
  },

  onLoad: function(params) {
    this.setData({
      videoParams: params
    });
    const that = this;
    const serverUrl = app.serverUrl;

    innerAudioContext.onPlay(() => {
      console.log('开始播放');
      console.log(innerAudioContext.src)
    })
    innerAudioContext.onError((res) => {
      console.log(res.errMsg)
      console.log(res.errCode)
    })

    innerAudioContext.onPause(
      () => {
        console.log('停止播放')
      }
    )

    let userInfo = app.getGlobalUserInfo();

    wx.request({
      url: serverUrl + '/bgm/all',
      method: 'GET',
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function(res) {
        let data = res.data;

        if (data.status == 502) {
          wx.redirectTo({
            url: '../userLogin/login',
          })
        }

        let play = [];
        for (let i = 0; i < data.data.length; i++) {
          play.push("play");
        }
        console.log(data);
        that.setData({
          bgmList: data.data,
          play: play,
        });

      }
    });
  },

  onRadioChange: function(e) {

    if (e.target.id == "noBgm") {
      let i = 0;
      for (let value of this.data.play) {
        if (value == "stop") {
          innerAudioContext.stop();
          let play = this.data.play;
          play[i] = "play"
          this.setData({
            play: play
          });
          break;
        }
        i++;
      }
      this.setData({
        radio: "noBgm"
      });
      return;
    }


    let i = 0;
    for (let value of this.data.play) {
      if (value == "stop") {
        innerAudioContext.stop();
        let play = this.data.play;
        play[i] = "play"
        this.setData({
          play: play
        });
        break;
      }
      i++;
    }
    

    if (e.target.dataset.name != this.data.radio) {
      this.setData({
        radio: e.target.dataset.name
      });
      let src = this.data.bgmList[parseInt(e.target.id)].path;

      innerAudioContext.src = app.serverUrl + encodeURI(src);
      //console.log(innerAudioContext.src);
      innerAudioContext.play();
      let play = this.data.play;
      play[parseInt(e.target.id)] = "stop";
      this.setData({
        play: play
      });
    } else {
      let flag = true;
      this.data.play.forEach((value) => {
        if (value == "stop") {
          flag = false;
        }
      });
      if (flag && i != parseInt(e.target.id)) {
        let src = this.data.bgmList[parseInt(e.target.id)].path;

        innerAudioContext.src = app.serverUrl + encodeURI(src);
        //console.log(innerAudioContext.src);
        innerAudioContext.play();
        let play = this.data.play;
        play[parseInt(e.target.id)] = "stop";
        this.setData({
          play: play
        });
      }
    }


    //console.log(e);
  },

  resetStatus: function() {
    this.setData({
      description: "",
      radio: "radio",
    });
  },

  upload: function() {

    const that = this;
    const serverUrl = app.serverUrl;

    let desc = this.data.description;
    let bgm = this.data.radio;
    let duration = this.data.videoParams.duration;
    let height = this.data.videoParams.height;
    let size = this.data.videoParams.size;
    let tempFilePath = this.data.videoParams.tempFilePath;
    let thumbTempFilePath = this.data.videoParams.thumbTempFilePath;
    let width = this.data.videoParams.width;

    // 上传短视频

    wx.showLoading({
      title: '上传中',
    })

    let userInfo = app.getGlobalUserInfo();



    let url = `${serverUrl}/video/${userInfo.id}?videoHeight=${height}&videoWidth=${width}&videoSeconds=${duration}`;
    if (desc != "") {
      url += `&description=${desc}`;
    }
    if (bgm != "noBgm") {
      url += `&bgmId=${bgm}`;
    }

    wx.uploadFile({
      url: url,
      filePath: tempFilePath,
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      name: 'file',
      success(result) {
        var data = JSON.parse(result.data);
        wx.hideLoading();
        if (data.status == 200) {
          Notify({
            text: "上传成功",
            duration: 3000,
            backgroundColor: "#1E90FF"
          });
        } else if (data.status == 500) {
          Notify({
            text: "上传失败",
            duration: 3000,
            // backgroundColor: "#FF4444"
          });
          console.log(data);
        } else if (data.status == 502) {
          wx.redirectTo({
            url: '../userLogin/login',
          });
        }
        wx.redirectTo({
          url: '../index/index',
        }); 
        //wx.hideLoading();
      }
    });
  },
})