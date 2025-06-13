// DOM 元素
const postsContainer = document.querySelector('.posts-container');
const categoryButtons = document.querySelectorAll('.category-btn');
const modal = document.getElementById('postModal');
const modalClose = document.querySelector('.modal-close');
const addPostBtn = document.getElementById('addPostBtn');
const addPostModal = document.getElementById('addPostModal');
const addPostModalClose = document.getElementById('addPostModalClose');
const cancelPostBtn = document.getElementById('cancelPost');
const postForm = document.getElementById('postForm');
const postImageInput = document.getElementById('postImage');
const imagePreview = document.getElementById('imagePreview');

// 当前选中的分类
let currentCategory = 'all';

// 加载帖子
async function loadPosts(category = 'all') {
    try {
        // 显示加载中
        const loading = document.createElement('div');
        loading.className = 'loading active';
        loading.innerHTML = '<div class="loading-spinner"></div><div class="loading-text">加载中...</div>';
        document.body.appendChild(loading);
        
        // 实际API请求，使用实际后端API
        let url = '/api/posts';
        if (category !== 'all') {
            url = `/api/posts/category/${category}`;
        }
        
        const response = await fetch(url, {
            credentials: 'include'
        });
        const result = await response.json();
        
        // 移除加载中
        document.body.removeChild(loading);
        
        // 检查响应状态
        if (result.code !== 200) {
            throw new Error(result.message || '加载帖子失败');
        }
        
        // 获取帖子数据
        const posts = result.data;
        
        // 清空容器
        postsContainer.innerHTML = '';
        
        // 渲染帖子
        if (posts && posts.length > 0) {
            posts.forEach(post => {
                const postElement = createPostElement(post);
                postsContainer.appendChild(postElement);
            });
        } else {
            postsContainer.innerHTML = '<div class="no-posts">暂无帖子</div>';
        }
    } catch (error) {
        console.error('Error loading posts:', error);
        postsContainer.innerHTML = `<div class="error-message">加载失败: ${error.message}</div>`;
    }
}

// 创建帖子元素
function createPostElement(post) {
    const article = document.createElement('article');
    article.className = 'card';
    article.dataset.postId = post.postId;
    
    // 默认用户信息，如果后端未提供则使用默认值
    const userName = post.user ? post.user.username : '匿名用户';
    const userAvatar = post.user && post.user.avatarUrl ? post.user.avatarUrl : 'https://via.placeholder.com/32';
    
    // 确保图片地址有效
    const imageUrl = post.imageUrl || 'https://via.placeholder.com/300x200';
    
    article.innerHTML = `
        <div class="card-image">
            <div class="image-loading"></div>
            <img 
                src="${imageUrl}" 
                alt="${post.title || '无标题'}" 
                onload="this.classList.add('loaded'); this.parentNode.querySelector('.image-loading').style.display='none';" 
                onerror="this.onerror=null; this.parentNode.querySelector('.image-loading').style.display='none'; this.parentNode.innerHTML += '<div class=\\'image-error\\'>图片加载失败</div>';"
            >
        </div>
        <div class="card-content">
            <div class="card-user">
                <img src="${userAvatar}" alt="${userName}" class="user-avatar" onerror="this.src='https://via.placeholder.com/32'">
                <span class="user-name">${userName}</span>
            </div>
            <h2>${post.title || '无标题'}</h2>
            <p>${post.description || '暂无描述'}</p>
            <div class="card-stats">
                <div class="stat-item">
                    <i class="fas fa-heart heart-icon ${post.isLiked ? 'liked' : ''}" data-post-id="${post.postId}"></i>
                    <span class="likes-count">${post.likes || 0}</span>
                </div>
                <div class="stat-item comment-btn" data-post-id="${post.postId}">
                    <i class="fas fa-comment"></i>
                    <span class="comments-count">${post.commentsCount || 0}</span>
                </div>
            </div>
        </div>
    `;

    // 添加点击事件监听器
    article.addEventListener('click', (e) => {
        if (!e.target.closest('.heart-icon')) {
            showPostDetails(post);
        }
    });

    // 添加点赞按钮事件监听器
    const likeBtn = article.querySelector('.heart-icon');
    likeBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        toggleLike(likeBtn, post);
    });

    // 添加评论按钮事件监听器
    const commentBtn = article.querySelector('.comment-btn');
    commentBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        openCommentsModal(post.postId);
    });

    return article;
}

// 显示帖子详情
async function showPostDetails(post) {
    try {
        // 如果是直接点击卡片，则需要从后端获取完整帖子信息
        if (!post.user || typeof post.user === 'undefined') {
            try {
                const response = await fetch(`/api/posts/${post.postId}`, {
                    credentials: 'include'
                });
                const result = await response.json();
                
                if (result.code === 200) {
                    post = result.data;
                }
                // 如果获取失败，继续使用现有数据
            } catch (error) {
                console.warn('Could not fetch post details, using available data:', error);
            }
        }
        
        // 默认用户信息，如果后端未提供则使用默认值
        const userName = post.user ? post.user.username : '匿名用户';
        const userAvatar = post.user && post.user.avatarUrl ? post.user.avatarUrl : 'https://via.placeholder.com/40';
        
        // 确保图片地址有效
        const imageUrl = post.imageUrl || 'https://via.placeholder.com/800x450';
        
        // 创建日期格式化
        const createdDate = post.createdAt ? new Date(post.createdAt) : new Date();
        const formattedDate = createdDate.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        
        const modalContent = document.querySelector('.modal-content');
        modalContent.innerHTML = `
            <button class="modal-close">
                <i class="fas fa-times"></i>
            </button>
            <div class="modal-user">
                <img src="${userAvatar}" alt="${userName}" onerror="this.src='https://via.placeholder.com/40'">
                <div class="user-name">${userName}</div>
            </div>
            <div class="modal-image">
                <img src="${imageUrl}" alt="${post.title || '无标题'}" onerror="this.src='https://via.placeholder.com/800x450'">
            </div>
            <div class="modal-body">
                <h2>${post.title || '无标题'}</h2>
                <div class="post-meta">
                    <span class="post-date"><i class="far fa-calendar-alt"></i> ${formattedDate}</span>
                    <span class="post-category"><i class="fas fa-tag"></i> ${post.category || '未分类'}</span>
                </div>
                <p>${post.description || '暂无描述'}</p>
                <div class="modal-stats">
                    <div class="modal-actions">
                        <button class="modal-action-btn like-btn ${post.isLiked ? 'liked' : ''}" data-post-id="${post.postId}">
                            <i class="fas fa-heart"></i>
                            <span class="likes-count">${post.likes || 0}</span>
                            <span class="action-text">赞</span>
                        </button>
                        <div class="modal-comment-info">
                            <i class="fas fa-comment"></i>
                            <span class="comments-count">${post.commentsCount || 0}</span>
                            <span class="action-text">条评论</span>
                        </div>
                    </div>
                </div>
                
                <!-- 评论输入区域 -->
                <div class="modal-comment-input-section">
                    <div class="comment-input-container">
                        <textarea 
                            id="modalCommentText" 
                            placeholder="写下你的评论..." 
                            maxlength="500"
                            class="modal-comment-textarea"
                        ></textarea>
                        <div class="comment-input-actions">
                            <span class="char-count">0/500</span>
                            <button type="button" class="modal-comment-submit-btn" data-post-id="${post.postId}">发布评论</button>
                        </div>
                    </div>
                </div>
                
                <!-- 评论列表区域 -->
                <div class="modal-comments-section">
                    <div class="comments-header">
                        <h3>评论列表</h3>
                        <div class="comments-loading">加载中...</div>
                    </div>
                    <div class="modal-comments-list" id="modalCommentsList">
                        <!-- 评论内容将在这里动态加载 -->
                    </div>
                </div>
            </div>
        `;

        // 重新绑定关闭按钮事件
        const newModalClose = modalContent.querySelector('.modal-close');
        newModalClose.addEventListener('click', () => {
            closeModal(modal);
        });
        
        // 绑定点赞按钮事件
        const modalLikeBtn = modalContent.querySelector('.like-btn');
        if (modalLikeBtn) {
            modalLikeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                toggleLike(modalLikeBtn.querySelector('.fas'), post);
            });
        }
        
        // 绑定评论输入框事件
        const modalCommentTextarea = modalContent.querySelector('#modalCommentText');
        const modalCommentSubmitBtn = modalContent.querySelector('.modal-comment-submit-btn');
        const charCountSpan = modalContent.querySelector('.char-count');
        
        if (modalCommentTextarea && charCountSpan) {
            modalCommentTextarea.addEventListener('input', () => {
                const length = modalCommentTextarea.value.length;
                charCountSpan.textContent = `${length}/500`;
                
                // 根据字数变化颜色
                if (length > 450) {
                    charCountSpan.style.color = '#e41e3f';
                } else if (length > 400) {
                    charCountSpan.style.color = '#ff9500';
                } else {
                    charCountSpan.style.color = '#65676b';
                }
            });
        }
        
        if (modalCommentSubmitBtn) {
            modalCommentSubmitBtn.addEventListener('click', () => {
                submitModalComment(post.postId);
            });
        }
        
        // 加载评论
        loadModalComments(post.postId);
        
        // 显示模态框
        openModal(modal);
    } catch (error) {
        console.error('Error showing post details:', error);
        alert(`加载帖子详情失败: ${error.message}`);
    }
}

// 切换点赞状态
async function toggleLike(button, post) {
    try {
        // 防止重复点击
        if (button.disabled) return;
        button.disabled = true;
        
        const isLiked = button.classList.contains('liked');
        
        // 发送请求到后端，让后端处理点赞逻辑
        const response = await fetch(`/api/posts/${post.postId}/like`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                action: isLiked ? 'unlike' : 'like'
            }),
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '操作失败');
        }
        
        // 从响应中获取最新的点赞状态和数量
        const newLikes = result.data.likes;
        const newIsLiked = result.data.isLiked;
        
        // 更新UI状态
        if (newIsLiked) {
            button.classList.add('liked');
        } else {
            button.classList.remove('liked');
        }
        
        // 更新点赞数显示
        const likesCount = button.nextElementSibling;
        if (likesCount) {
            likesCount.textContent = newLikes;
        }
        
        // 更新post对象中的点赞数
        post.likes = newLikes;
        post.isLiked = newIsLiked;
        
        // 同时更新模态框中的点赞状态（如果存在）
        const modalLikeBtn = document.querySelector('#postModal .like-btn[data-post-id="' + post.postId + '"]');
        if (modalLikeBtn && modalLikeBtn !== button) {
            if (newIsLiked) {
                modalLikeBtn.classList.add('liked');
            } else {
                modalLikeBtn.classList.remove('liked');
            }
            const modalLikesCount = modalLikeBtn.querySelector('.likes-count');
            if (modalLikesCount) {
                modalLikesCount.textContent = newLikes;
            }
        }
        
        // 同时更新卡片中的点赞状态（如果是从模态框操作）
        const cardLikeBtn = document.querySelector('.card[data-post-id="' + post.postId + '"] .heart-icon');
        if (cardLikeBtn && cardLikeBtn !== button) {
            if (newIsLiked) {
                cardLikeBtn.classList.add('liked');
            } else {
                cardLikeBtn.classList.remove('liked');
            }
            const cardLikesCount = cardLikeBtn.nextElementSibling;
            if (cardLikesCount) {
                cardLikesCount.textContent = newLikes;
            }
        }
        
    } catch (error) {
        console.error('Error toggling like:', error);
        alert(`操作失败: ${error.message}`);
    } finally {
        // 重新启用按钮
        button.disabled = false;
    }
}

// 打开添加帖子模态框
function openAddPostModal() {
    // 重置表单
    postForm.reset();
    
    // 清空图片预览
    imagePreview.innerHTML = '<div class="placeholder">预览图片将显示在这里</div>';
    
    // 显示模态框
    openModal(addPostModal);
}

// 关闭添加帖子模态框
function closeAddPostModal() {
    closeModal(addPostModal);
}

// 上传图片到服务器
async function uploadImage(file) {
    try {
        console.log('开始上传图片:', file.name, '大小:', file.size, '类型:', file.type);
        
        // 验证文件类型
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        const allowedExtensions = ['.jpg', '.jpeg', '.png'];
        
        if (!allowedTypes.includes(file.type)) {
            throw new Error('只支持JPG和PNG格式的图片文件');
        }
        
        // 验证文件扩展名
        const fileName = file.name.toLowerCase();
        const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));
        if (!hasValidExtension) {
            throw new Error('文件扩展名必须是 .jpg、.jpeg 或 .png');
        }
        
        // 验证文件大小（可选，限制为10MB）
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            throw new Error('图片文件大小不能超过5MB');
        }
        
        const formData = new FormData();
        formData.append('file', file);
        
        const response = await fetch('/api/posts/upload-image', {
            method: 'POST',
            body: formData,
            credentials: 'include'
        });
        
        const result = await response.json();
        console.log('上传响应:', result);
        
        if (result.code !== 200) {
            throw new Error(result.message || '图片上传失败');
        }
        
        // 记录成功上传的信息
        console.log('图片上传成功:', result.data);
        if (result.data.fullPath) {
            console.log('文件保存路径:', result.data.fullPath);
        }
        
        return result.data.imageUrl;
    } catch (error) {
        console.error('Error uploading image:', error);
        throw error;
    }
}

// 测试上传目录配置
async function testUploadDirectory() {
    try {
        const response = await fetch('/api/posts/test-upload-dir', {
            credentials: 'include'
        });
        const result = await response.json();
        console.log('上传目录测试结果:', result);
        return result;
    } catch (error) {
        console.error('测试上传目录失败:', error);
        return null;
    }
}

// 预览图片
function previewImage(file) {
    if (file) {
        // 验证文件类型
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        const allowedExtensions = ['.jpg', '.jpeg', '.png'];
        
        if (!allowedTypes.includes(file.type)) {
            imagePreview.innerHTML = '<div class="placeholder" style="color: #e41e3f;">只支持JPG和PNG格式的图片文件</div>';
            return;
        }
        
        // 验证文件扩展名
        const fileName = file.name.toLowerCase();
        const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));
        if (!hasValidExtension) {
            imagePreview.innerHTML = '<div class="placeholder" style="color: #e41e3f;">文件扩展名必须是 .jpg、.jpeg 或 .png</div>';
            return;
        }
        
        // 验证文件大小（限制为5MB）
        const maxSize = 5 * 1024 * 1024; // 5MB
        if (file.size > maxSize) {
            imagePreview.innerHTML = '<div class="placeholder" style="color: #e41e3f;">图片文件大小不能超过5MB</div>';
            return;
        }
        
        const reader = new FileReader();
        
        reader.onload = function(e) {
            imagePreview.innerHTML = `<img src="${e.target.result}" alt="预览图片">`;
        };
        
        reader.readAsDataURL(file);
    }
}

// 提交帖子
async function submitPost(e) {
    e.preventDefault();
    
    // 获取表单数据
    const title = document.getElementById('postTitle').value;
    const description = document.getElementById('postDescription').value;
    const category = document.getElementById('postCategory').value;
    const file = document.getElementById('postImage').files[0];
    
    // 简单验证
    if (!title || !description || !category) {
        alert('请填写必填字段');
        return;
    }
    
    try {
        // 获取提交按钮，用于显示进度
        const submitBtn = postForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        
        let imageUrl = '';
        
        // 如果有选择图片，先上传图片
        if (file) {
            // 显示上传进度
            submitBtn.textContent = '上传图片中...';
            submitBtn.disabled = true;
            
            try {
                imageUrl = await uploadImage(file);
                submitBtn.textContent = '发布中...';
            } catch (error) {
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
                throw new Error('图片上传失败：' + error.message);
            }
        }
        
        // 获取当前用户信息
        let currentUserId = 1; // 默认值
        if (window.UserManager && window.UserManager.currentUser) {
            const user = window.UserManager.currentUser;
            if (typeof user === 'object' && user.userId) {
                currentUserId = user.userId;
            }
        }
        
        // 构建帖子数据
        const postData = {
            title: title,
            description: description,
            category: category,
            userId: currentUserId,
            imageUrl: imageUrl // 使用上传后的图片URL，如果没有上传图片则为空字符串
        };

        // 发送请求创建帖子
        const response = await fetch('/api/posts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(postData),
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '创建帖子失败');
        }
        
        // 恢复按钮状态
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
        // 关闭模态框
        closeAddPostModal();
        
        // 重新加载帖子列表
        loadPosts(currentCategory);
        
        // 显示成功提示
        alert('帖子发布成功！');
        
    } catch (error) {
        console.error('Error creating post:', error);
        alert(`创建帖子失败: ${error.message}`);
        
        // 恢复按钮状态
        const submitBtn = postForm.querySelector('button[type="submit"]');
        submitBtn.textContent = '发布';
        submitBtn.disabled = false;
    }
}

// 事件监听器
categoryButtons.forEach(button => {
    button.addEventListener('click', () => {
        // 移除之前选中的类
        categoryButtons.forEach(btn => btn.classList.remove('active'));
        // 添加新的选中类
        button.classList.add('active');
        // 更新当前分类并加载帖子
        currentCategory = button.dataset.category;
        loadPosts(currentCategory);
    });
});

// 关闭模态框
function closeModal(modalElement) {
    // 移除active类，开始淡出效果
    modalElement.classList.remove('active');
    // 等待过渡效果完成后隐藏元素
    setTimeout(() => {
        modalElement.style.display = 'none';
    }, 300); // 匹配CSS中的过渡时间
}

// 关闭模态框
modalClose.addEventListener('click', () => {
    closeModal(modal);
});

window.addEventListener('click', (e) => {
    if (e.target === modal) {
        closeModal(modal);
    }
    if (e.target === addPostModal) {
        closeModal(addPostModal);
    }
});

// 添加帖子按钮点击事件
addPostBtn.addEventListener('click', openAddPostModal);

// 新增帖子模态框关闭按钮
addPostModalClose.addEventListener('click', closeAddPostModal);
cancelPostBtn.addEventListener('click', closeAddPostModal);

// 图片预览
postImageInput.addEventListener('change', (e) => {
    previewImage(e.target.files[0]);
});

// 提交表单
postForm.addEventListener('submit', submitPost);

// 初始加载
document.addEventListener('DOMContentLoaded', () => {
    loadPosts();
    // 测试上传目录配置
    testUploadDirectory();
});

// 显示模态框
function openModal(modalElement) {
    // 设置为flex显示
    modalElement.style.display = 'flex';
    // 强制浏览器重绘以确保过渡效果正常触发
    void modalElement.offsetHeight;
    // 添加active类开始过渡效果
    requestAnimationFrame(() => {
        modalElement.classList.add('active');
    });
}

// 评论功能相关变量
let currentPostId = null;

// 打开评论模态框
async function openCommentsModal(postId) {
    currentPostId = postId;
    
    // 更新标题
    document.getElementById('commentModalTitle').textContent = `评论`;
    
    // 清空输入框
    document.getElementById('commentInput').value = '';
    updateCharCount();
    
    // 加载评论
    await loadComments(postId);
    
    // 显示模态框
    openModal(document.getElementById('commentModal'));
}

// 加载评论列表
async function loadComments(postId) {
    try {
        const response = await fetch(`/api/posts/${postId}/comments`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '加载评论失败');
        }
        
        const comments = result.data;
        renderComments(comments);
        
        // 更新评论数量显示
        document.getElementById('commentModalTitle').textContent = `评论 (${comments.length})`;
        
    } catch (error) {
        console.error('Error loading comments:', error);
        document.getElementById('commentsList').innerHTML = 
            `<div class="error-message">加载评论失败: ${error.message}</div>`;
    }
}

// 渲染评论列表
function renderComments(comments) {
    const commentsList = document.getElementById('commentsList');
    
    if (comments.length === 0) {
        commentsList.innerHTML = '<div class="no-comments">暂无评论，快来抢沙发吧！</div>';
        return;
    }
    
    commentsList.innerHTML = comments.map(comment => createCommentElement(comment)).join('');
}

// 创建评论元素
function createCommentElement(comment) {
    const createdDate = new Date(comment.createdAt);
    const timeAgo = getTimeAgo(createdDate);
    const userAvatar = comment.userAvatar || 'https://via.placeholder.com/40';
    
    return `
        <div class="comment-item" data-comment-id="${comment.commentId}">
            <div class="comment-avatar">
                <img src="${userAvatar}" alt="${comment.username}" onerror="this.src='https://via.placeholder.com/40'">
            </div>
            <div class="comment-content">
                <div class="comment-header">
                    <span class="comment-username">${comment.username}</span>
                    <span class="comment-time">${timeAgo}</span>
                </div>
                <div class="comment-text">${comment.content}</div>
                <div class="comment-actions">
                    <button class="delete-comment-btn" data-comment-id="${comment.commentId}" style="display: ${canDeleteComment(comment) ? 'inline-block' : 'none'}">
                        <i class="fas fa-trash"></i> 删除
                    </button>
                </div>
            </div>
        </div>
    `;
}

// 判断是否可以删除评论
function canDeleteComment(comment) {
    // 简化：这里可以根据当前登录用户判断
    // 实际项目中应该检查 comment.userId 是否等于当前用户ID
    return true; // 暂时允许所有人删除
}

// 计算时间差
function getTimeAgo(date) {
    const now = new Date();
    const diffMs = now - date;
    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    
    if (diffMinutes < 1) {
        return '刚刚';
    } else if (diffMinutes < 60) {
        return `${diffMinutes}分钟前`;
    } else if (diffHours < 24) {
        return `${diffHours}小时前`;
    } else if (diffDays < 7) {
        return `${diffDays}天前`;
    } else {
        return date.toLocaleDateString('zh-CN');
    }
}

// 发表评论
async function submitComment() {
    const content = document.getElementById('commentInput').value.trim();
    
    if (!content) {
        alert('请输入评论内容');
        return;
    }
    
    if (content.length > 500) {
        alert('评论内容不能超过500字');
        return;
    }
    
    try {
        const submitBtn = document.getElementById('submitComment');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = '发送中...';
        submitBtn.disabled = true;
        
        const response = await fetch(`/api/posts/${currentPostId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content }),
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '发表评论失败');
        }
        
        // 清空输入框
        document.getElementById('commentInput').value = '';
        updateCharCount();
        
        // 重新加载评论
        await loadComments(currentPostId);
        
        // 更新帖子卡片中的评论数量
        updatePostCommentCount(currentPostId);
        
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
    } catch (error) {
        console.error('Error submitting comment:', error);
        alert(`发表评论失败: ${error.message}`);
        
        const submitBtn = document.getElementById('submitComment');
        submitBtn.textContent = '发送';
        submitBtn.disabled = false;
    }
}

// 删除评论
async function deleteComment(commentId) {
    if (!confirm('确定要删除这条评论吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '删除评论失败');
        }
        
        // 重新加载评论
        await loadComments(currentPostId);
        
        // 更新帖子卡片中的评论数量
        updatePostCommentCount(currentPostId);
        
    } catch (error) {
        console.error('Error deleting comment:', error);
        alert(`删除评论失败: ${error.message}`);
    }
}

// 更新帖子的评论数量显示
async function updatePostCommentCount(postId) {
    try {
        const response = await fetch(`/api/posts/${postId}/comments/count`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            // 更新卡片中的评论数量
            const postCard = document.querySelector(`.card[data-post-id="${postId}"]`);
            if (postCard) {
                const commentsCountSpan = postCard.querySelector('.comments-count');
                if (commentsCountSpan) {
                    commentsCountSpan.textContent = result.data;
                }
            }
            
            // 同时更新模态框中的评论数量（如果存在）
            const modalCommentBtn = document.querySelector('#postModal .comment-btn[data-post-id="' + postId + '"]');
            if (modalCommentBtn) {
                const modalCommentsCount = modalCommentBtn.querySelector('.comments-count');
                if (modalCommentsCount) {
                    modalCommentsCount.textContent = result.data;
                }
            }
        }
    } catch (error) {
        console.error('Error updating comment count:', error);
    }
}

// 更新字符计数
function updateCharCount() {
    const textarea = document.getElementById('commentInput');
    const charCount = document.querySelector('.char-count');
    charCount.textContent = `${textarea.value.length}/500`;
}

// 事件监听器
document.addEventListener('DOMContentLoaded', () => {
    // 评论模态框关闭按钮
    document.getElementById('commentModalClose').addEventListener('click', () => {
        closeModal(document.getElementById('commentModal'));
    });
    
    // 发送评论按钮
    document.getElementById('submitComment').addEventListener('click', submitComment);
    
    // 输入框字符计数
    document.getElementById('commentInput').addEventListener('input', updateCharCount);
    
    // 评论输入框回车发送（Ctrl+Enter）
    document.getElementById('commentInput').addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && e.ctrlKey) {
            e.preventDefault();
            submitComment();
        }
    });
    
    // 评论列表点击事件委托
    document.getElementById('commentsList').addEventListener('click', (e) => {
        if (e.target.closest('.delete-comment-btn')) {
            const commentId = e.target.closest('.delete-comment-btn').dataset.commentId;
            deleteComment(commentId);
        }
    });
    
    // 点击模态框背景关闭
    window.addEventListener('click', (e) => {
        const commentModal = document.getElementById('commentModal');
        if (e.target === commentModal) {
            closeModal(commentModal);
        }
    });
});

// ========== 模态框评论功能 ==========

// 加载模态框中的评论
async function loadModalComments(postId) {
    try {
        const commentsLoadingDiv = document.querySelector('.comments-loading');
        const commentsListDiv = document.getElementById('modalCommentsList');
        
        if (commentsLoadingDiv) {
            commentsLoadingDiv.style.display = 'block';
        }
        
        const response = await fetch(`/api/posts/${postId}/comments`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (commentsLoadingDiv) {
            commentsLoadingDiv.style.display = 'none';
        }
        
        if (result.code === 200) {
            const comments = result.data || [];
            renderModalComments(comments, commentsListDiv);
        } else {
            if (commentsListDiv) {
                commentsListDiv.innerHTML = '<div class="no-comments">加载评论失败</div>';
            }
        }
    } catch (error) {
        console.error('Error loading modal comments:', error);
        const commentsLoadingDiv = document.querySelector('.comments-loading');
        const commentsListDiv = document.getElementById('modalCommentsList');
        
        if (commentsLoadingDiv) {
            commentsLoadingDiv.style.display = 'none';
        }
        if (commentsListDiv) {
            commentsListDiv.innerHTML = '<div class="no-comments">加载评论失败</div>';
        }
    }
}

// 渲染模态框中的评论
function renderModalComments(comments, container) {
    if (!container) return;
    
    if (!comments || comments.length === 0) {
        container.innerHTML = '<div class="no-comments">暂无评论，快来发表第一条评论吧！</div>';
        return;
    }
    
    container.innerHTML = comments.map(comment => createModalCommentElement(comment)).join('');
}

// 创建模态框中的评论元素
function createModalCommentElement(comment) {
    console.log('Creating comment element for:', comment);
    
    const timeAgo = getTimeAgo(new Date(comment.createdAt));
    const canDelete = canDeleteComment(comment);
    const userName = comment.username || '匿名用户';
    const userAvatar = comment.userAvatar || 'https://via.placeholder.com/32';
    
    console.log('User info - Name:', userName, 'Avatar:', userAvatar);
    
    return `
        <div class="modal-comment-item" data-comment-id="${comment.commentId}">
            <div class="modal-comment-avatar">
                <img src="${userAvatar}" alt="${userName}" onerror="this.src='https://via.placeholder.com/32'">
            </div>
            <div class="modal-comment-content">
                <div class="modal-comment-header">
                    <span class="modal-comment-author">${userName}</span>
                    <span class="modal-comment-time">${timeAgo}</span>
                    ${canDelete ? `<button class="modal-comment-delete" onclick="deleteModalComment(${comment.commentId})">删除</button>` : ''}
                </div>
                <div class="modal-comment-text">${comment.content || ''}</div>
            </div>
        </div>
    `;
}

// 提交模态框中的评论
async function submitModalComment(postId) {
    try {
        const commentText = document.getElementById('modalCommentText');
        const submitBtn = document.querySelector('.modal-comment-submit-btn');
        
        if (!commentText || !commentText.value.trim()) {
            alert('请输入评论内容');
            return;
        }
        
        if (commentText.value.length > 500) {
            alert('评论内容不能超过500字');
            return;
        }
        
        // 禁用按钮和输入框
        submitBtn.disabled = true;
        commentText.disabled = true;
        submitBtn.textContent = '发布中...';
        
        const response = await fetch('/api/comments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                postId: postId,
                content: commentText.value.trim()
            }),
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            // 清空输入框
            commentText.value = '';
            const charCount = document.querySelector('.char-count');
            if (charCount) {
                charCount.textContent = '0/500';
                charCount.style.color = '#65676b';
            }
            
            // 重新加载评论
            await loadModalComments(postId);
            
            // 更新评论数量
            await updateModalCommentCount(postId);
            
            alert('评论发布成功！');
        } else {
            alert(`评论发布失败: ${result.message || '未知错误'}`);
        }
    } catch (error) {
        console.error('Error submitting modal comment:', error);
        alert(`评论发布失败: ${error.message}`);
    } finally {
        // 恢复按钮和输入框状态
        const commentText = document.getElementById('modalCommentText');
        const submitBtn = document.querySelector('.modal-comment-submit-btn');
        
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = '发布评论';
        }
        if (commentText) {
            commentText.disabled = false;
        }
    }
}

// 删除模态框中的评论
async function deleteModalComment(commentId) {
    if (!confirm('确定要删除这条评论吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            // 移除评论元素
            const commentElement = document.querySelector(`.modal-comment-item[data-comment-id="${commentId}"]`);
            if (commentElement) {
                commentElement.remove();
            }
            
            // 更新评论数量
            const postModal = document.getElementById('postModal');
            if (postModal && postModal.style.display !== 'none') {
                const postIdAttr = document.querySelector('.modal-comment-submit-btn')?.getAttribute('data-post-id');
                if (postIdAttr) {
                    await updateModalCommentCount(parseInt(postIdAttr));
                }
            }
            
            alert('评论删除成功！');
        } else {
            alert(`删除失败: ${result.message || '未知错误'}`);
        }
    } catch (error) {
        console.error('Error deleting modal comment:', error);
        alert(`删除失败: ${error.message}`);
    }
}

// 更新模态框中的评论数量
async function updateModalCommentCount(postId) {
    try {
        const response = await fetch(`/api/posts/${postId}/comments/count`, {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            // 更新模态框中的评论数量
            const modalCommentsCount = document.querySelector('.modal-comment-info .comments-count');
            if (modalCommentsCount) {
                modalCommentsCount.textContent = result.data;
            }
            
            // 同时更新卡片中的评论数量
            await updatePostCommentCount(postId);
        }
    } catch (error) {
        console.error('Error updating modal comment count:', error);
    }
} 