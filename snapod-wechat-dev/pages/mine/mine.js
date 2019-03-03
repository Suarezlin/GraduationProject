var videoUtil = require('../../utils/videoUtil.js')
import Notify from '../../dist/notify/notify';

const app = getApp()

Page({
  data: {
    faceUrl: "../resource/images/noneface.jpg",
    isMe: true,
    isFollow: false,

    receiveLikeCounts: 0,
    followCounts: 0,
    fansCounts: 0,


    videoSelClass: "video-info",
    isSelectedWork: "video-info-selected",
    isSelectedLike: "",
    isSelectedFollow: "",

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true,

    scrollTop: 0,
    nickname: "",
    publisher: "",
  },

  onLoad: function(params) {
    const that = this;
    const serverUrl = app.serverUrl;

    const userInfo = app.getGlobalUserInfo();
    let userId = userInfo.id;

    let publisherId = params.publisherId;

    if (publisherId != null && publisherId != '' && publisherId != undefined) {
      if (publisherId != userId) {
        userId = publisherId;
        that.setData({
          isMe: false
        });
      }
    }

    wx.showLoading({
      title: '加载中',
    });
    
    wx.request({
      url: serverUrl + "/user/info/?id=" + userId,
      method: "GET",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function(res) {
        wx.hideLoading();

        if (res.data.status == 200) {
          const user = res.data.data;
          console.log(user);
          var faceImage = "../resource/images/noneface.jpg"
          if (user.faceImage != null && user.faceImage != '' && user.faceImage != undefined) {
            faceImage = serverUrl + user.faceImage;
          }
          that.setData({
            faceUrl: faceImage,
            receiveLikeCounts: user.receiveLikeCounts,
            followCounts: user.followCounts,
            fansCounts: user.fansCounts,
            nickname: user.nickname,
            publisher: user.id
          });
        } else if (res.data.status == 502) {
          Notify({
            text: res.data.msg,
            duration: 3000,
          });
          let realUrl = "/pages/mine/mine#publisherId@" + userId;
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + realUrl,
          });
        }
      }
    });

    wx.request({
      url: `${serverUrl}/user/fan?userId=${userId}&fanId=${userInfo.id}`,
      method: "GET",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function (res) {
        if (res.data.status == 200) {
          let isFollow = res.data.data;
          that.setData({
            isFollow: isFollow
          });
        } else if (res.data.status == 502) {
          Notify({
            text: res.data.msg,
            duration: 3000,
          });
          let realUrl = "/pages/mine/mine#publisherId@" + userId;
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + realUrl,
          });
        }
      }
    });

  },

  onPageScroll(event) {
    this.setData({
      scrollTop: event.scrollTop
    });
  },


  followMe: function(e) {

  },



  logout: function() {
    var user = app.getGlobalUserInfo();
    var serverUrl = app.serverUrl;
    const that = this;
    wx.showLoading({
      title: '注销中',
    });
    wx.request({
      url: serverUrl + '/logout?userId=' + user.id,
      method: 'POST',

      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        wx.hideLoading();
        Notify({
          text: "注销成功",
          duration: 3000,
          backgroundColor: "#FF4444"
        });
        console.log(res.data);
        wx.removeStorageSync("userInfo");
        wx.redirectTo({
          url: '/pages/userLogin/login',
        })
      }
    });
  },

  changeFace: function() {
    const that = this;
    const serverUrl = app.serverUrl;
    // wx.showLoading({
    //   title: '上传中',
    // });
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        // tempFilePath可以作为img标签的src属性显示图片
        const tempFilePaths = res.tempFilePaths;
        // console.log(tempFilePaths);
        wx.uploadFile({
          url: serverUrl + "/user/face?userId=" + app.getGlobalUserInfo().id,
          filePath: tempFilePaths[0],
          name: 'file',
          success(result) {
            var data = JSON.parse(result.data);
            // wx.hideLoading();
            if (data.status == 200) {
              Notify({
                text: "上传成功",
                duration: 3000,
                backgroundColor: "#1E90FF"
              });
              console.log(data);
              app.setGlobalUserInfo(data.data);
              //app.userInfo = data.data;
              that.setData({
                faceUrl: serverUrl + app.getGlobalUserInfo().faceImage
              });
            } else if (data.status == 500) {
              Notify({
                text: "上传失败",
                duration: 3000,
                // backgroundColor: "#FF4444"
              });
              console.log(data);
            }
          }
        });
      }
    })
  },

  uploadVideo: function() {
    videoUtil.uploadVideo();
    
  },


  // 点击跳转到视频详情页面
  showVideo: function(e) {



  },

  // 到底部后触发加载
  onReachBottom: function() {

  },

  followMe: function() {
    const that = this;
    let status;
    const serverUrl = app.serverUrl;
    const userInfo = app.getGlobalUserInfo();
    let plusOrReduce;
    if (that.data.isFollow) {
      status = "cencelFan";
      plusOrReduce = -1;
    } else {
      status = "fan";
      plusOrReduce = 1;
    }

    wx.request({
      url: `${serverUrl}/user/${status}?userId=${that.data.publisher}&fanId=${userInfo.id}`,
      method: "POST",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function (res) {
        if (res.data.status == 200) {
          that.setData({
            isFollow: !that.data.isFollow,
            fansCounts: that.data.fansCounts + plusOrReduce
          });
        } else if (res.data.status == 502) {
          Notify({
            text: res.data.msg,
            duration: 3000,
          });
          let realUrl = "/pages/mine/mine#publisherId@" + that.data.publisher;
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + realUrl,
          });
        }
      }
    });
  }
})