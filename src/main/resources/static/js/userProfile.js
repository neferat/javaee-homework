// 用户主页侧边栏管理
class UserProfile {
    constructor() {
        this.sidebar = null;
        this.overlay = null;
        this.currentUser = null;
        this.init();
    }

    // 初始化用户主页
    init() {
        this.createSidebar();
        this.bindEvents();
        this.loadUserProfile();
    }

    // 创建侧边栏HTML结构
    createSidebar() {
        // 创建遮罩层
        this.overlay = document.createElement('div');
        this.overlay.className = 'profile-overlay';
        
        // 创建侧边栏
        this.sidebar = document.createElement('div');
        this.sidebar.className = 'profile-sidebar';
        this.sidebar.innerHTML = `
            <div class="profile-header">
                <h3>用户主页</h3>
                <button class="profile-close" type="button">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            
            <div class="profile-content">
                <!-- 头像区域 -->
                <div class="profile-avatar-section">
                    <div class="avatar-container">
                        <img id="profileAvatar" src="/images/avatars/default.jpg" alt="用户头像" class="profile-avatar">
                        <div class="avatar-overlay">
                            <i class="fas fa-camera"></i>
                            <span>更换头像</span>
                        </div>
                    </div>
                    <input type="file" id="avatarInput" accept="image/jpeg,image/jpg,image/png" style="display: none">
                </div>
                
                <!-- 用户信息 -->
                <div class="profile-info-section">
                    <div class="info-item">
                        <label for="profileUsername">用户名</label>
                        <input type="text" id="profileUsername" placeholder="请输入用户名" maxlength="20">
                    </div>
                    
                    <div class="info-item">
                        <label for="profileBio">个性签名</label>
                        <textarea id="profileBio" placeholder="写下您的个性签名..." maxlength="100" rows="3"></textarea>
                    </div>
                    
                    <div class="info-item">
                        <label for="profileEmail">邮箱</label>
                        <input type="email" id="profileEmail" placeholder="邮箱地址" readonly>
                    </div>
                    
                    <div class="info-item">
                        <label>注册时间</label>
                        <span id="profileRegisterDate" class="readonly-info">-</span>
                    </div>
                    
                    <div class="info-item">
                        <label>用户等级</label>
                        <span id="profileLevel" class="readonly-info">-</span>
                    </div>
                    
                    <div class="info-item">
                        <label>积分</label>
                        <span id="profilePoints" class="readonly-info">-</span>
                    </div>
                </div>
                
                <!-- 操作按钮 -->
                <div class="profile-actions">
                    <button type="button" class="btn btn-primary" id="saveProfile">
                        <i class="fas fa-save"></i> 保存修改
                    </button>
                    <button type="button" class="btn btn-secondary" id="cancelProfile">
                        <i class="fas fa-times"></i> 取消
                    </button>
                </div>
            </div>
        `;
        
        // 添加到页面
        document.body.appendChild(this.overlay);
        document.body.appendChild(this.sidebar);
    }

    // 绑定事件
    bindEvents() {
        // 关闭侧边栏
        const closeBtn = this.sidebar.querySelector('.profile-close');
        const cancelBtn = this.sidebar.querySelector('#cancelProfile');
        
        closeBtn.addEventListener('click', () => this.close());
        cancelBtn.addEventListener('click', () => this.close());
        this.overlay.addEventListener('click', () => this.close());
        
        // 头像上传
        const avatarContainer = this.sidebar.querySelector('.avatar-container');
        const avatarInput = this.sidebar.querySelector('#avatarInput');
        
        avatarContainer.addEventListener('click', () => avatarInput.click());
        avatarInput.addEventListener('change', (e) => this.handleAvatarUpload(e));
        
        // 保存按钮
        const saveBtn = this.sidebar.querySelector('#saveProfile');
        saveBtn.addEventListener('click', () => this.saveProfile());
        
        // 监听用户头像点击事件
        document.addEventListener('click', (e) => {
            if (e.target.matches('.user-avatar') || e.target.closest('.user-avatar')) {
                this.open();
            }
        });
    }

    // 加载用户资料
    async loadUserProfile() {
        try {
            if (window.UserManager && window.UserManager.currentUser) {
                this.currentUser = window.UserManager.currentUser;
                this.displayUserInfo();
            } else {
                // 如果UserManager还没有用户信息，尝试获取
                const response = await fetch('/currentuser', {
                    credentials: 'include'
                });
                const result = await response.json();
                
                if (result.code === 200) {
                    this.currentUser = result.data;
                    this.displayUserInfo();
                }
            }
        } catch (error) {
            console.error('加载用户资料失败:', error);
        }
    }

    // 显示用户信息
    displayUserInfo() {
        if (!this.currentUser) return;
        
        const user = typeof this.currentUser === 'string' ? 
            { username: this.currentUser } : this.currentUser;
        
        // 更新头像
        const avatar = this.sidebar.querySelector('#profileAvatar');
        if (user.avatarUrl) {
            avatar.src = user.avatarUrl;
        }
        
        // 更新用户信息
        const usernameInput = this.sidebar.querySelector('#profileUsername');
        const bioInput = this.sidebar.querySelector('#profileBio');
        const emailInput = this.sidebar.querySelector('#profileEmail');
        const registerDate = this.sidebar.querySelector('#profileRegisterDate');
        const level = this.sidebar.querySelector('#profileLevel');
        const points = this.sidebar.querySelector('#profilePoints');
        
        usernameInput.value = user.username || '';
        bioInput.value = user.bio || '';
        emailInput.value = user.email || '';
        
        if (user.registrationDate) {
            const date = new Date(user.registrationDate);
            registerDate.textContent = date.toLocaleDateString('zh-CN');
        }
        
        level.textContent = `等级 ${user.userLevel || 1}`;
        points.textContent = `${user.points || 0} 积分`;
    }

    // 处理头像上传
    async handleAvatarUpload(event) {
        const file = event.target.files[0];
        if (!file) return;
        
        try {
            // 验证文件
            if (!file.type.match(/^image\/(jpeg|jpg|png)$/)) {
                alert('只支持JPG和PNG格式的图片');
                return;
            }
            
            if (file.size > 5 * 1024 * 1024) {
                alert('图片大小不能超过5MB');
                return;
            }
            
            // 显示加载状态
            const avatar = this.sidebar.querySelector('#profileAvatar');
            const originalSrc = avatar.src;
            avatar.style.opacity = '0.6';
            
            // 上传头像
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch('/api/users/upload-avatar', {
                method: 'POST',
                body: formData,
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                // 更新头像显示
                avatar.src = result.data.avatarUrl;
                avatar.style.opacity = '1';
                
                // 更新当前用户对象
                if (typeof this.currentUser === 'object') {
                    this.currentUser.avatarUrl = result.data.avatarUrl;
                }
                
                // 更新页面上所有的用户头像
                this.updatePageAvatars(result.data.avatarUrl);
                
                alert('头像更新成功！');
            } else {
                avatar.src = originalSrc;
                avatar.style.opacity = '1';
                alert(result.message || '头像上传失败');
            }
            
        } catch (error) {
            console.error('头像上传失败:', error);
            alert('头像上传失败：' + error.message);
        }
        
        // 清空input
        event.target.value = '';
    }

    // 更新页面上所有的用户头像
    updatePageAvatars(newAvatarUrl) {
        const avatars = document.querySelectorAll('.user-avatar');
        avatars.forEach(avatar => {
            avatar.src = newAvatarUrl;
        });
    }

    // 保存用户资料
    async saveProfile() {
        try {
            const username = this.sidebar.querySelector('#profileUsername').value.trim();
            const bio = this.sidebar.querySelector('#profileBio').value.trim();
            
            if (!username) {
                alert('用户名不能为空');
                return;
            }
            
            if (username.length < 2 || username.length > 20) {
                alert('用户名长度应在2-20个字符之间');
                return;
            }
            
            if (bio.length > 100) {
                alert('个性签名不能超过100个字符');
                return;
            }
            
            // 显示保存状态
            const saveBtn = this.sidebar.querySelector('#saveProfile');
            const originalText = saveBtn.innerHTML;
            saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 保存中...';
            saveBtn.disabled = true;
            
            // 发送更新请求
            const response = await fetch('/api/users/update-profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    bio: bio
                }),
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                // 更新当前用户对象
                if (typeof this.currentUser === 'object') {
                    this.currentUser.username = username;
                    this.currentUser.bio = bio;
                } else {
                    this.currentUser = username;
                }
                
                // 更新页面上所有的用户名显示
                this.updatePageUsernames(username);
                
                // 更新UserManager中的用户信息
                if (window.UserManager) {
                    window.UserManager.currentUser = this.currentUser;
                }
                
                alert('资料更新成功！');
                this.close();
            } else {
                alert(result.message || '保存失败');
            }
            
            // 恢复按钮状态
            saveBtn.innerHTML = originalText;
            saveBtn.disabled = false;
            
        } catch (error) {
            console.error('保存用户资料失败:', error);
            alert('保存失败：' + error.message);
            
            // 恢复按钮状态
            const saveBtn = this.sidebar.querySelector('#saveProfile');
            saveBtn.innerHTML = '<i class="fas fa-save"></i> 保存修改';
            saveBtn.disabled = false;
        }
    }

    // 更新页面上所有的用户名显示
    updatePageUsernames(newUsername) {
        const usernames = document.querySelectorAll('.user-name, [data-user-info="username"]');
        usernames.forEach(element => {
            element.textContent = newUsername;
        });
    }

    // 打开侧边栏
    open() {
        this.loadUserProfile();
        this.overlay.classList.add('active');
        this.sidebar.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    // 关闭侧边栏
    close() {
        this.overlay.classList.remove('active');
        this.sidebar.classList.remove('active');
        document.body.style.overflow = '';
        
        // 重置表单
        setTimeout(() => {
            this.loadUserProfile();
        }, 300);
    }
}

// 初始化用户主页
document.addEventListener('DOMContentLoaded', () => {
    window.userProfile = new UserProfile();
});

// 导出供其他模块使用
window.UserProfile = UserProfile; 