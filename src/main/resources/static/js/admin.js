// 管理员功能模块
class AdminManager {
    constructor() {
        this.isAdmin = false;
        this.init();
    }
    
    async init() {
        await this.checkAdminStatus();
        this.initAdminUI();
    }
    
    // 检查管理员状态
    async checkAdminStatus() {
        try {
            const response = await fetch('/admin/api/check-admin', {
                credentials: 'include'
            });
            const result = await response.json();
            
            if (result.code === 200) {
                this.isAdmin = result.data;
                console.log('管理员状态:', this.isAdmin);
            }
        } catch (error) {
            console.error('检查管理员状态失败:', error);
            this.isAdmin = false;
        }
    }
    
    // 初始化管理员UI
    initAdminUI() {
        const adminBtn = document.getElementById('adminPanelBtn');
        if (adminBtn) {
            if (this.isAdmin) {
                adminBtn.style.display = 'flex';
                console.log('显示管理员面板按钮');
            } else {
                adminBtn.style.display = 'none';
                console.log('隐藏管理员面板按钮');
            }
        }
        
        // 为帖子卡片添加管理员操作按钮
        if (this.isAdmin) {
            this.addAdminActionsToCards();
        }
    }
    
    // 为帖子卡片添加管理员操作
    addAdminActionsToCards() {
        const cards = document.querySelectorAll('.post-card');
        cards.forEach(card => {
            const postId = card.dataset.postId;
            if (postId && !card.querySelector('.admin-actions')) {
                const adminActions = this.createAdminActions(postId);
                card.appendChild(adminActions);
            }
        });
    }
    
    // 创建管理员操作按钮
    createAdminActions(postId) {
        const adminActions = document.createElement('div');
        adminActions.className = 'admin-actions';
        adminActions.innerHTML = `
            <button class="admin-action-btn delete-post-btn" onclick="adminManager.deletePost(${postId})" title="删除帖子">
                <i class="fas fa-trash"></i>
            </button>
            <button class="admin-action-btn edit-post-btn" onclick="adminManager.editPost(${postId})" title="编辑帖子">
                <i class="fas fa-edit"></i>
            </button>
        `;
        return adminActions;
    }
    
    // 打开管理面板
    async openAdminPanel() {
        try {
            // 检查权限
            if (!this.isAdmin) {
                alert('您没有管理员权限');
                return;
            }
            
            // 打开管理面板页面
            const adminWindow = window.open('/admin/dashboard', '_blank');
            if (!adminWindow) {
                // 如果弹窗被阻止，尝试在当前页面打开
                window.location.href = '/admin/dashboard';
            }
        } catch (error) {
            console.error('打开管理面板失败:', error);
            alert('打开管理面板失败：' + error.message);
        }
    }
    
    // 删除帖子
    async deletePost(postId) {
        if (!this.isAdmin) {
            alert('您没有管理员权限');
            return;
        }
        
        if (!confirm('确定要删除这个帖子吗？此操作不可恢复。')) {
            return;
        }
        
        try {
            const response = await fetch(`/admin/api/posts/${postId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                alert('帖子删除成功');
                // 从页面移除卡片
                const card = document.querySelector(`.post-card[data-post-id="${postId}"]`);
                if (card) {
                    card.remove();
                }
            } else {
                alert('删除失败：' + (result.message || '未知错误'));
            }
        } catch (error) {
            console.error('删除帖子失败:', error);
            alert('删除失败：' + error.message);
        }
    }
    
    // 编辑帖子（简化版）
    async editPost(postId) {
        if (!this.isAdmin) {
            alert('您没有管理员权限');
            return;
        }
        
        const newTitle = prompt('请输入新的帖子标题：');
        if (!newTitle) {
            return;
        }
        
        const newDescription = prompt('请输入新的帖子描述：');
        if (!newDescription) {
            return;
        }
        
        try {
            const response = await fetch(`/admin/api/posts/${postId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: newTitle,
                    description: newDescription
                }),
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                alert('帖子更新成功');
                // 刷新页面或更新卡片内容
                location.reload();
            } else {
                alert('更新失败：' + (result.message || '未知错误'));
            }
        } catch (error) {
            console.error('编辑帖子失败:', error);
            alert('更新失败：' + error.message);
        }
    }
    
    // 获取统计信息
    async getStatistics() {
        try {
            const response = await fetch('/admin/api/statistics', {
                credentials: 'include'
            });
            const result = await response.json();
            
            if (result.code === 200) {
                return result.data;
            } else {
                throw new Error(result.message || '获取统计信息失败');
            }
        } catch (error) {
            console.error('获取统计信息失败:', error);
            throw error;
        }
    }
    
    // 批量删除帖子
    async batchDeletePosts(postIds) {
        if (!this.isAdmin) {
            alert('您没有管理员权限');
            return;
        }
        
        if (!confirm(`确定要删除选中的 ${postIds.length} 个帖子吗？此操作不可恢复。`)) {
            return;
        }
        
        try {
            const response = await fetch('/admin/api/posts/batch', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ postIds }),
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                alert(result.data || '批量删除成功');
                location.reload();
            } else {
                alert('批量删除失败：' + (result.message || '未知错误'));
            }
        } catch (error) {
            console.error('批量删除失败:', error);
            alert('批量删除失败：' + error.message);
        }
    }
    
    // 用户管理相关方法
    async deleteUser(userId) {
        if (!this.isAdmin) {
            alert('您没有管理员权限');
            return;
        }
        
        if (!confirm('确定要删除这个用户吗？此操作不可恢复。')) {
            return;
        }
        
        try {
            const response = await fetch(`/admin/api/users/${userId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                alert('用户删除成功');
                return true;
            } else {
                alert('删除失败：' + (result.message || '未知错误'));
                return false;
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            alert('删除失败：' + error.message);
            return false;
        }
    }
    
    // 在帖子重新加载时重新添加管理员操作
    onPostsReloaded() {
        if (this.isAdmin) {
            setTimeout(() => {
                this.addAdminActionsToCards();
            }, 100);
        }
    }
}

// 全局管理员实例
let adminManager = null;

// 打开管理面板的全局函数
function openAdminPanel() {
    if (adminManager) {
        adminManager.openAdminPanel();
    } else {
        console.error('管理员管理器未初始化');
    }
}

// 页面加载完成后初始化管理员功能
document.addEventListener('DOMContentLoaded', () => {
    adminManager = new AdminManager();
    
    // 监听帖子加载完成事件（如果有的话）
    document.addEventListener('postsLoaded', () => {
        if (adminManager) {
            adminManager.onPostsReloaded();
        }
    });
});

// 导出管理员管理器
window.AdminManager = AdminManager;
window.adminManager = adminManager; 