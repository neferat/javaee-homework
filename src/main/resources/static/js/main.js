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
            body: formData
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
        const response = await fetch('/api/posts/test-upload-dir');
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
        
        // 构建帖子数据
        const postData = {
            title: title,
            description: description,
            category: category,
            userId: 1, // 假设当前用户ID为1
            imageUrl: imageUrl // 使用上传后的图片URL，如果没有上传图片则为空字符串
        };

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