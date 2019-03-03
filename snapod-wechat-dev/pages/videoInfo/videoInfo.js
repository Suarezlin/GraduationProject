// pages/videoInfo.js
import Notify from '../../dist/notify/notify';
var videoUtil = require('../../utils/videoUtil.js')
const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    fit: "cover",
    video: {},
    serverUrl: app.serverUrl,
    author: {},
    userLikeVideo: false,
    publisher: {},
  },

  videoContext: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (params) {
    console.log(params);
    const that = this;

    this.videoContext = wx.createVideoContext("myVideo", this);
    let id = params.id;
    wx.request({
      url: `${app.serverUrl}/video/${id}`,
      method: "GET",
      success: function(res) {
        let data = res.data;
        //console.log(data);
        let width = data.data.videoWidth;
        let height = data.data.videoHeight;
        let fit = "cover";
        if (width >= height) {
          fit = "";
        }
        that.setData({
          fit: fit,
          video: data.data
        });
        let user = app.getGlobalUserInfo();
        let loginUserId = "";
        if (user != null && user != undefined && user != "") {
          loginUserId = user.id;
        }

        wx.request({
          url: `${app.serverUrl}/user/publisher/${data.data.id}?loginUserId=${loginUserId}`,
          method: "GET",
          success: function (res) {
            let data = res.data.data;
            //console.log(data);
            that.setData({
              publisher: data.publisher,
              userLikeVideo: data.likeVideo
            });
          }
        })
      }
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.videoContext.play();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    this.videoContext.pause();
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },

  showSearch: function() {
    wx.navigateTo({
      url: '/pages/search/search',
    })
  },

  upload: function() {

    let user = app.getGlobalUserInfo();

    let realUrl = "/pages/videoInfo/videoInfo#id@" + this.data.video.id;
    
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + realUrl,
      });
    } else {
      videoUtil.uploadVideo();
    }
  },

  showIndex: function() {
    wx.redirectTo({
      url: "/pages/index/index",
    })
  },
  showMine: function() {

    let user = app.getGlobalUserInfo();
    let realUrl = "/pages/videoInfo/videoInfo#id@" + this.data.video.id;
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + realUrl,
      });
    } else {
      wx.navigateTo({
        url: '/pages/mine/mine',
      });
    }
  },

  likeVideoOrNot: function() {
    const that = this;
    let user = app.getGlobalUserInfo();
    let realUrl = "/pages/videoInfo/videoInfo#id@" + this.data.video.id;
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + realUrl,
      });
    } else {
      let userLikeVideo = that.data.userLikeVideo;
      let video = that.data.video;
      let serverUrl = app.serverUrl;
      let url = `/video/like?userId=${user.id}&videoId=${video.id}&videoCreaterId=${video.userId}`;
      if (userLikeVideo) {
        url = `/video/notlike?userId=${user.id}&videoId=${video.id}&videoCreaterId=${video.userId}`;
      }
      wx.request({
        url: serverUrl + url,
        method: "POST",
        header: {
          "userId": user.id,
          "userToken": user.userToken
        },
        success: function(res) {
          let data = res.data;
          if (data.status == 200) {
            that.setData({
              userLikeVideo: !userLikeVideo
            });
          } else if (data.status == 502) {
            let realUrl = "/pages/videoInfo/videoInfo#id@" + that.data.video.id;
            wx.redirectTo({
              url: '/pages/userLogin/login?redirectUrl=' + realUrl,
            });
          }
        }
      })
    }
  },
  showPublisher: function() {
    const that = this;
    let user = app.getGlobalUserInfo();
    let video = this.data.video;
    let realUrl = "/pages/mine/mine#publisherId@" + video.userId;
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + realUrl,
      });
    } else {
      wx.navigateTo({
        url: `../mine/mine?publisherId=${video.userId}`,
      })
    }
  }
})