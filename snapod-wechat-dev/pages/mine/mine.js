var videoUtil = require('../../utils/videoUtil.js')
import Notify from '../../dist/notify/notify';
import Dialog from '../../dist/dialog/dialog';

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
    showMyVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    showLikeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    showFollowVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFalg: false,
    myLikesFalg: true,
    myFollowFalg: true,

    scrollTop: 0,
    nickname: "",
    publisher: "",
    serverUrl: app.serverUrl,
    tabActive: 0,
  },

  getVideoList: function (page, pageSize, isSaveRecord) {
    const that = this;
    let serverUrl = this.data.serverUrl;
    const userInfo = app.getGlobalUserInfo();

    wx.showLoading({
      title: '加载中',
    });

    

    let searchValue = that.data.searchValue;
    let url = `${serverUrl}/video/getVideo/?publisherId=${that.data.publisher}&page=${page}&pageSize=${pageSize}`;
    wx.request({
      url: url,
      method: "GET",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function (res) {
        wx.hideLoading();
        wx.stopPullDownRefresh();
        let data = res.data;
        console.log(data);
        let myVideoPage = that.data.myVideoPage;
        let myVideoList = that.data.myVideoList;
        let myVideoTotal = that.data.myVideoTotal;

        // 判断是否为第一页
        if (page === 1) {
          that.setData({
            myVideoList: []
          });
        }

        let currentVideoList = that.data.myVideoList;
        let newVideoList = data.data.rows;
        that.setData({
          myVideoTotal: data.data.total,
          myVideoPage: data.data.page,
          myVideoList: currentVideoList.concat(newVideoList),
        });

        if (that.data.tabActive == 0) {
          that.setData({
            showMyVideoList: currentVideoList.concat(newVideoList)
          });
        }
        wx.hideLoading();
      }
    });

  },

  getLikeVideoList: function (page, pageSize, isSaveRecord) {
    const that = this;
    let serverUrl = this.data.serverUrl;
    const userInfo = app.getGlobalUserInfo();

    wx.showLoading({
      title: '加载中',
    });

    let searchValue = that.data.searchValue;
    let url = `${serverUrl}/video/getLikeVideo/?userId=${that.data.publisher}&page=${page}&pageSize=${pageSize}`;
    wx.request({
      url: url,
      method: "GET",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function (res) {
        wx.hideLoading();
        wx.stopPullDownRefresh();
        let data = res.data;
        console.log(data);
      
        let likeVideoPage = that.data.likeVideoPage;
        let likeVideoList = that.data.likeVideoList;
        let likeVideoTotal = that.data.likeVideoTotal;
        // 判断是否为第一页
        if (page === 1) {
          that.setData({
            likeVideoList: []
          });
        }

        let currentVideoList = that.data.likeVideoList;
        let newVideoList = data.data.rows;
        that.setData({
          likeVideoTotal: data.data.total,
          likeVideoPage: data.data.page,
          likeVideoList: currentVideoList.concat(newVideoList),
        });
        if (that.data.tabActive == 1) {
          that.setData({
            showLikeVideoList: currentVideoList.concat(newVideoList)
          });
        }
        wx.hideLoading();
      }
    });

  },

  getFollowVideoList: function (page, pageSize, isSaveRecord) {
    const that = this;
    let serverUrl = this.data.serverUrl;
    const userInfo = app.getGlobalUserInfo();

    wx.showLoading({
      title: '加载中',
    });

    let searchValue = that.data.searchValue;
    let url = `${serverUrl}/video/getFollowVideo/?userId=${that.data.publisher}&page=${page}&pageSize=${pageSize}`;
    wx.request({
      url: url,
      method: "GET",
      header: {
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },
      success: function (res) {
        wx.hideLoading();
        wx.stopPullDownRefresh();
        let data = res.data;
        console.log(data);

        let followVideoPage = that.data.followVideoPage;
        let followVideoList = that.data.followVideoList;
        let followVideoTotal = that.data.followVideoTotal;
        // 判断是否为第一页
        if (page === 1) {
          that.setData({
            followVideoList: []
          });
        }

        let currentVideoList = that.data.followVideoList;
        let newVideoList = data.data.rows;
        that.setData({
          followVideoTotal: data.data.total,
          followVideoPage: data.data.page,
          followVideoList: currentVideoList.concat(newVideoList),
        });
        if (that.data.tabActive == 2) {
          that.setData({
            showFollowVideoList: currentVideoList.concat(newVideoList)
          });
        }
        wx.hideLoading();
      }
    });

  },

  onTabChange: function(e) {
    let tab = e.detail.index;
    this.setData({
      tabActive: tab
    });
    if (tab == 0) {
      this.setData({
        showMyVideoList: this.data.myVideoList,
        showLikeVideoList: [],
        showFollowVideoList: []
      });
    } else if (tab == 1) {
      this.setData({
        showLikeVideoList: this.data.likeVideoList,
        showMyVideoList: [],
        showFollowVideoList: []
      });
    } else {
      this.setData({
        showFollowVideoList: this.data.followVideoList,
        showLikeVideoList: [],
        showMyVideoList: []
      });
    }
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
          that.getVideoList(that.data.myVideoPage, 10);
          that.getLikeVideoList(that.data.likeVideoPage, 10);
          that.getFollowVideoList(that.data.followVideoPage, 10);
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


  // followMe: function(e) {

  // },



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
    console.log(e);
    let index = e.currentTarget.dataset.arrindex;
    let tab = this.data.tabActive;
    let id;
    if (tab == 0) {
      id = this.data.myVideoList[index].id;  
    } else if (tab == 1) {
      id = this.data.likeVideoList[index].id;
    } else {
      id = this.data.followVideoList[index].id;
    }
    wx.navigateTo({
      url: `/pages/videoInfo/videoInfo?id=${id}`,
    });
  },

  // 到底部后触发加载
  onReachBottom: function() {
    const that = this;
    let tab = that.data.tabActive;
    if (tab == 0) {
      if (that.data.myVideoPage < that.data.myVideoTotal) {
        wx.showLoading({
          title: '加载中',
        });
        that.getVideoList(that.data.myVideoPage + 1, 10);
      }
    } else if (tab == 1) {
      if (that.data.likeVideoPage < that.data.likeVideoTotal) {
        wx.showLoading({
          title: '加载中',
        });
        that.getLikeVideoList(that.data.likeVideoPage + 1, 10);
      }
    } else {
      if (that.data.followVideoPage < that.data.followVideoTotal) {
        wx.showLoading({
          title: '加载中',
        });
        that.getFollowVideoList(that.data.followVideoPage + 1, 10);
      }
    }
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
  },

  onSwipeClose:function(e) {
    // console.log(e);
    const that = this;
    let position = e.detail.position;
    let index = e.target.dataset.arrindex;
    let instance = e.detail.instance;
    if (position == "right") {
      Dialog.confirm({
        message: '确定删除视频吗？'
      }).then(() => {
        let serverUrl = that.data.serverUrl;
        let url = `${serverUrl}/video/delete?videoId=${that.data.myVideoList[index].id}`;
        const userInfo = app.getGlobalUserInfo();
        let header = {
          'userId': userInfo.id,
          'userToken': userInfo.userToken
        };
        wx.request({
          url: url,
          header: header,
          method: "POST",
          success(res) {
            let data = res.data;
            if (data.status == 200) {
              let videos = that.data.myVideoList;
              videos.splice(index, 1);
              that.setData({
                myVideoList: videos,
                showMyVideoList: videos
              });
            } else if (data.status == 500) {
              Notify({
                text: "删除失败",
                duration: 3000,
                // backgroundColor: "#FF4444"
              });
            }
          }
        })
        instance.close();
      });
    }

  }
})