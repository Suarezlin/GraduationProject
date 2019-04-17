//index.js
//获取应用实例
const app = getApp()
var videoUtil = require('../../utils/videoUtil.js')
// import { Tabbar, TabbarItem } from 'vant';

// Vue.use(Tabbar).use(TabbarItem);

Page({
  data: {
    serverUrl: app.serverUrl,
    imageUrl: "/1902026YH9N73HX4/face/tmp_e29d6d8dc3eda8b58eec86096f67ce04.jpg",
    videoName: "视频测试",
    coverUrl: "/1902026YH9N73HX4/video/cover/DFAAF7DB97714763A90F273074F27FEB.jpg",
    show: false,
    isLike: {
      status: false,
      word: "喜欢",
      icon: "star-o"
    },
    active: 0,
    current: 0,
    likeNum: 100,
    screenWidth: 0,
    videoList: [],
    totalPages: 1,
    page: 1,
    pageSize: 5,
    searchValue: "",
  },

  getVideoList: function (page, pageSize, isSaveRecord) {
    const that = this;
    let serverUrl = this.data.serverUrl;

    wx.showLoading({
      title: '加载中',
    });

    let searchValue = that.data.searchValue;
    let url = `${serverUrl}/video/list/?page=${page}&pageSize=${pageSize}&isSaveRecords=${isSaveRecord}`;
    wx.request({
      url: url,
      method: "POST",
      data: {
        videoDesc: searchValue
      },
      success: function (res) {
        wx.hideLoading();
        wx.stopPullDownRefresh();
        let data = res.data;
        console.log(data);

        // 判断是否为第一页
        if (page === 1) {
          that.setData({
            videoList: []
          });
        }

        let currentVideoList = that.data.videoList;
        let newVideoList = data.data.rows;

        that.setData({
          totalPages: data.data.total,
          page: data.data.page,
          videoList: currentVideoList.concat(newVideoList),
        });
      }
    });

  },

  onLoad: function(params) {
    const that = this;
    let screenWidth = wx.getSystemInfoSync().screenWidth;
    that.setData({
      screenWidth: screenWidth
    });

    let searchValue = params.searchValue;
    let isSaveRecord = params.isSaveRecord

    if (isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined) {
      isSaveRecord = 0;
    }
    if (searchValue != null && searchValue != '' && searchValue != undefined) {
      that.setData({
        active: 1,
        searchValue: searchValue
      });
    }

    this.getVideoList(1, this.data.pageSize, isSaveRecord);
  },

  onPullDownRefresh: function() {
    const that = this;
    let pageSize = this.data.pageSize;
    let currentPage = 1;
    wx.showNavigationBarLoading();
    this.getVideoList(currentPage, pageSize, 0);
    wx.hideNavigationBarLoading();
  },

  onReachBottom: function() {
    const that = this;
    let pageSize = this.data.pageSize;
    let currentPage = this.data.page;

    // 判断是否还有页面
    if (currentPage < this.data.totalPages) {
      this.getVideoList(currentPage + 1, pageSize, 0);
    }
  },

  showProcessPanel: function(e) {
    this.setData({
      show: true
    });
  },

  onClose: function() {
    this.setData({
      show: false
    });
  },

  doLike: function(e) {
    if (this.data.isLike.status) {
      this.setData({
        isLike: {
          status: false,
          word: "喜欢",
          icon: "star-o"
        },
      });
    } else {
      this.setData({
        isLike: {
          status: true,
          word: "不喜欢",
          icon: "star"
        },
      });
    }
  },

  getVideo: function(e) {
    console.log(e);
    let index = e.target.dataset.arrindex;
    let video = this.data.videoList[index];
    wx.navigateTo({
      url: `/pages/videoInfo/videoInfo?id=${video.id}`,
    });
  },

  toUserSpace: function(e) {
    let userId = e.currentTarget.id;
    const that = this;
    let user = app.getGlobalUserInfo();
    let video = this.data.video;
    let realUrl = "/pages/mine/mine#publisherId@" + userId;
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + realUrl,
      });
    } else {
      wx.navigateTo({
        url: `../mine/mine?publisherId=${userId}`,
      })
    }
  },

  onChange(event) {
    //console.log(event.detail);
    let user = app.getGlobalUserInfo();
    let realUrl;
    const that = this;
    let current = that.data.current;
    console.log(current);

    switch(event.detail) {
      case 0:
        wx.redirectTo({
          url: '/pages/index/index',
        });
        that.setData({
          current: 0
        });
        break;
      case 1:
        wx.navigateTo({
          url: '/pages/search/search',
        });
        that.setData({
          current: 1
        });
        break;
      case 2:

        realUrl = "/pages/index/index";

        if (user == null || user == undefined || user == "") {
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + realUrl,
          });
        } else {

          videoUtil.uploadVideo();
        }
        break;
      case 3:
        //TODO: 实现跳转到 关注页面
        that.setData({
          current: 3
        });
        break;
      case 4:

        realUrl = "/pages/MineTab/mine"

        if (user == null || user == undefined || user == "") {
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + realUrl,
          });
        } else {
          wx.redirectTo({
            url: '/pages/MineTab/mine',
          })
        }
        that.setData({
          current: 4
        });
        break;
    }

    that.setData({
      active: current
    });
  }
  
})
