// // 模拟数据
// const mockPosts = [
//     {
//         id: 1,
//         user: {
//             name: "张三",
//             avatar: "images\\avatars\\user1.jpg",
//             timestamp: "2小时前"
//         },
//         title: "今天的游戏真好玩！",
//         description: "和朋友们一起玩了新出的多人联机游戏，太刺激了！",
//         image: "images/posts/game1.jpg",
//         category: "game",
//         likes: 156,
//         comments: 23,
//         isLiked: false
//     },
//     {
//         id: 2,
//         user: {
//             name: "李四",
//             avatar: "images\\avatars\\user2.jpg",
//             timestamp: "3小时前"
//         },
//         title: "校园篮球赛精彩瞬间",
//         description: "今天的校园篮球赛真是太精彩了，看到了很多精彩的表现！",
//         image: "images/posts/sports1.jpg",
//         category: "sports",
//         likes: 89,
//         comments: 12,
//         isLiked: false
//     },
//     {
//         id: 3,
//         user: {
//             name: "王五",
//             avatar: "images\\avatars\\user3.jpg",
//             timestamp: "4小时前"
//         },
//         title: "搞笑日常",
//         description: "今天在校园里看到的有趣一幕，忍不住分享给大家！",
//         image: "images/posts/funny1.jpg",
//         category: "funny",
//         likes: 234,
//         comments: 45,
//         isLiked: false
//     },
//     {
//         id: 4,
//         user: {
//             name: "赵六",
//             avatar: "images\\avatars\\user4.jpg",
//             timestamp: "5小时前"
//         },
//         title: "艺术展览",
//         description: "参观了今天的艺术展览，被这些作品深深打动了。",
//         image: "images/posts/art1.jpg",
//         category: "art",
//         likes: 167,
//         comments: 28,
//         isLiked: false
//     },
//     {
//         id: 5,
//         user: {
//             name: "小明",
//             avatar: "images\\avatars\\user5.jpg",
//             timestamp: "6小时前"
//         },
//         title: "儿童节活动",
//         description: "今天和小朋友们一起参加了儿童节活动，真是开心！",
//         image: "images/posts/kids1.jpg",
//         category: "kids",
//         likes: 145,
//         comments: 19,
//         isLiked: false
//     },
//     {
//         id: 6,
//         user: {
//             name: "小红",
//             avatar: "images\\avatars\\user6.jpg",
//             timestamp: "7小时前"
//         },
//         title: "美食分享",
//         description: "今天尝试了一道新菜，味道真的很不错！",
//         image: "images/posts/food1.jpg",
//         category: "food",
//         likes: 198,
//         comments: 34,
//         isLiked: false
//     },
//     {
//         id: 7,
//         user: {
//             name: "小华",
//             avatar: "images\\avatars\\user7.jpg",
//             timestamp: "8小时前"
//         },
//         title: "自然风光",
//         description: "周末去郊游，拍到了这么美的风景！",
//         image: "images/posts/nature1.jpg",
//         category: "nature",
//         likes: 276,
//         comments: 41,
//         isLiked: false
//     },
//     {
//         id: 8,
//         user: {
//             name: "小军",
//             avatar: "images\\avatars\\user8.jpg",
//             timestamp: "9小时前"
//         },
//         title: "校园生活",
//         description: "记录美好的校园时光，这里有我们最珍贵的回忆。",
//         image: "images/posts/campus1.jpg",
//         category: "campus",
//         likes: 189,
//         comments: 27,
//         isLiked: false
//     },
//     {
//         id: 9,
//         user: {
//             name: "小芳",
//             avatar: "images\\avatars\\user9.jpg",
//             timestamp: "10小时前"
//         },
//         title: "游戏攻略分享",
//         description: "分享一下最近玩的游戏的一些技巧和心得。",
//         image: "images/posts/game2.jpg",
//         category: "game",
//         likes: 134,
//         comments: 31,
//         isLiked: false
//     }
// ];
//
// // 模拟 API 响应
// function getMockPosts(category = 'all') {
//   if (category === 'all') {
//     return mockPosts;
//   }
//   return {
//     data: mockPosts.filter(post => post.category === category)
//   };
// }
//
// // 模拟获取单个帖子详情
// function getMockPostById(id) {
//   const post = mockPosts.find(p => p.id === parseInt(id));
//   return post ? { data: post } : null;
// }
//
// // 导出模拟数据和函数
// window.mockData = {
//   getMockPosts,
//   getMockPostById
// };
