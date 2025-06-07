// 用户状态管理
let currentUser = null;

// 获取当前用户信息
async function getCurrentUser() {
    try {
        const response = await fetch('/currentuser', {
            credentials: 'include'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            currentUser = result.data;
            return currentUser;
        } else {
            console.warn('获取用户信息失败:', result.msg);
            return null;
        }
    } catch (error) {
        console.error('Error getting current user:', error);
        return null;
    }
}

// 更新页面的用户信息显示
function updateUserDisplay() {
    const userElements = document.querySelectorAll('[data-user-info]');
    
    userElements.forEach(element => {
        const type = element.dataset.userInfo;
        
        if (currentUser) {
            switch (type) {
                case 'username':
                    element.textContent = typeof currentUser === 'string' ? currentUser : currentUser.username;
                    break;
                case 'avatar':
                    if (typeof currentUser === 'object' && currentUser.avatarUrl) {
                        element.src = currentUser.avatarUrl;
                    }
                    break;
                case 'status':
                    element.textContent = '已登录';
                    break;
            }
        } else {
            switch (type) {
                case 'username':
                    element.textContent = '未登录';
                    break;
                case 'avatar':
                    element.src = '/images/avatars/default.svg';
                    break;
                case 'status':
                    element.textContent = '未登录';
                    break;
            }
        }
    });
}

// 检查登录状态
async function checkLoginStatus() {
    await getCurrentUser();
    updateUserDisplay();
}

// 登出功能
async function logout() {
    try {
        const response = await fetch('/logout', {
            credentials: 'include'
        });
        if (response.ok) {
            currentUser = null;
            updateUserDisplay();
            // 重定向到登录页面
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Error logging out:', error);
    }
}

// 页面加载时检查登录状态
document.addEventListener('DOMContentLoaded', () => {
    checkLoginStatus();
});

// 导出函数供其他模块使用
window.UserManager = {
    getCurrentUser,
    checkLoginStatus,
    logout,
    get currentUser() { return currentUser; },
    set currentUser(user) { 
        currentUser = user; 
        updateUserDisplay(); 
    }
}; 