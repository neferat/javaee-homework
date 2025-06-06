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
        
        const response = await fetch(url);
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
                <div class="stat-item">
                    <i class="fas fa-comment"></i>
                    <span>${post.commentsCount || 0}</span>
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

    return article;
}

// 显示帖子详情
async function showPostDetails(post) {
    try {
        // 如果是直接点击卡片，则需要从后端获取完整帖子信息
        if (!post.user || typeof post.user === 'undefined') {
            try {
                const response = await fetch(`/api/posts/${post.postId}`);
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
                    <span><i class="fas fa-heart" style="color: #e41e3f;"></i> ${post.likes || 0} 赞</span>
                    <span><i class="fas fa-comment" style="color: #1877f2;"></i> ${post.commentsCount || 0} 评论</span>
                </div>
            </div>
        `;

        // 重新绑定关闭按钮事件
        const newModalClose = modalContent.querySelector('.modal-close');
        newModalClose.addEventListener('click', () => {
            closeModal(modal);
        });
        
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
        const isLiked = button.classList.contains('liked');
        const newLikes = isLiked ? post.likes - 1 : post.likes + 1;
        
        // 发送请求更新点赞数
        const response = await fetch(`/api/posts/${post.postId}/likes?likes=${newLikes}`, {
            method: 'PUT'
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '更新点赞失败');
        }
        
        // 更新UI
        button.classList.toggle('liked');
        const likesCount = button.nextElementSibling;
        likesCount.textContent = newLikes;
        
        // 更新post对象中的点赞数
        post.likes = newLikes;
    } catch (error) {
        console.error('Error toggling like:', error);
        alert(`更新点赞失败: ${error.message}`);
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

// 预览图片
function previewImage(file) {
    if (file) {
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
    // const file = document.getElementById('postImage').files[0]; // 移除文件获取
    
    // 简单验证
    if (!title || !description || !category) {
        alert('请填写必填字段');
        return;
    }
    
    try {
        // 构建帖子数据
        const postData = {
            title: title,
            description: description,
            category: category,
            userId: 1 // 假设当前用户ID为1
        };
        
        // 直接根据分类设置 imageUrl
        // 如果分类是 "默认" 或其他特定值，您可能需要特殊处理，这里使用通用模式
        // 例如: postData.imageUrl = category === 'all' ? 'images/posts/default.jpg' : `images/posts/${category}.jpg`;
        // 根据之前的总结，我们设置为 `images/posts/${category}.jpg`
        // 如果分类本身可能就是 default, 那么路径就是 images/posts/default.jpg
        // 如果需要像 images/posts/default1.jpg 这样的，可以在后端处理或者在这里调整逻辑
        postData.imageUrl = `images/posts/${category}.jpg`;
        // 如果后端createPost中的默认图片逻辑是 images/posts/category1.jpg, 
        // 并且希望在没有选择分类时（category可能为空或特定值）也使用它，
        // 这里的逻辑可能需要与后端对齐，或者让后端全权处理默认图片。
        // 为简化，此处遵循之前总结的 `images/posts/${category}.jpg`。

        // 发送请求创建帖子
        const response = await fetch('/api/posts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(postData)
        });
        
        const result = await response.json();
        
        if (result.code !== 200) {
            throw new Error(result.message || '创建帖子失败');
        }
        
        // 关闭模态框
        closeAddPostModal();
        
        // 重新加载帖子列表
        loadPosts(currentCategory);
        
        // 显示成功提示
        alert('帖子发布成功！');
    } catch (error) {
        console.error('Error creating post:', error);
        alert(`创建帖子失败: ${error.message}`);
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