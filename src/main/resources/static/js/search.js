// 搜索功能管理
class SearchManager {
    constructor() {
        this.searchInput = null;
        this.searchIcon = null;
        this.searchResults = null;
        this.searchTimeout = null;
        this.isSearching = false;
        this.init();
    }

    // 初始化搜索功能
    init() {
        this.searchInput = document.getElementById('searchInput');
        this.searchIcon = document.getElementById('searchIcon');
        this.searchResults = document.getElementById('searchResults');
        
        if (this.searchInput && this.searchIcon && this.searchResults) {
            this.bindEvents();
        }
    }

    // 绑定事件
    bindEvents() {
        // 输入事件 - 实时搜索
        this.searchInput.addEventListener('input', (e) => {
            this.handleSearchInput(e.target.value);
        });

        // 搜索图标点击事件
        this.searchIcon.addEventListener('click', () => {
            this.performSearch(this.searchInput.value);
        });

        // 回车键搜索
        this.searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.performSearch(this.searchInput.value);
            }
        });

        // 焦点事件
        this.searchInput.addEventListener('focus', () => {
            if (this.searchInput.value.trim()) {
                this.showResults();
            }
        });

        // 失去焦点时延迟隐藏结果
        this.searchInput.addEventListener('blur', () => {
            setTimeout(() => {
                this.hideResults();
            }, 200);
        });

        // 点击其他地方时隐藏搜索结果
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.search-container')) {
                this.hideResults();
            }
        });
    }

    // 处理搜索输入
    handleSearchInput(query) {
        // 清除之前的定时器
        if (this.searchTimeout) {
            clearTimeout(this.searchTimeout);
        }

        // 如果查询为空，隐藏结果
        if (!query.trim()) {
            this.hideResults();
            return;
        }

        // 设置防抖，避免频繁请求
        this.searchTimeout = setTimeout(() => {
            this.showSearchDropdown(query.trim());
        }, 300);
    }

    // 执行搜索
    async performSearch(query) {
        if (!query.trim()) {
            return;
        }

        try {
            // 隐藏搜索结果下拉框
            this.hideResults();
            
            // 显示加载状态
            this.showMainSearchResults('搜索中...');
            
            // 执行搜索
            const results = await this.searchPosts(query.trim());
            
            // 显示搜索结果在主页面
            this.displayMainSearchResults(results, query);
            
        } catch (error) {
            console.error('搜索执行失败:', error);
            this.showMainSearchResults('搜索失败，请重试');
        }
    }

    // 搜索帖子
    async searchPosts(query) {
        try {
            this.isSearching = true;
            
            const response = await fetch(`/api/posts/search?q=${encodeURIComponent(query)}`, {
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.code === 200) {
                return result.data || [];
            } else {
                throw new Error(result.message || '搜索失败');
            }
            
        } catch (error) {
            console.error('搜索请求失败:', error);
            throw error;
        } finally {
            this.isSearching = false;
        }
    }

    // 显示搜索结果下拉框
    async showSearchDropdown(query) {
        try {
            const results = await this.searchPosts(query);
            
            if (results.length === 0) {
                this.searchResults.innerHTML = '<div class="search-result-item">未找到相关帖子</div>';
            } else {
                // 只显示前5个结果
                const limitedResults = results.slice(0, 5);
                this.searchResults.innerHTML = limitedResults.map(post => `
                    <div class="search-result-item" data-post-id="${post.postId}">
                        <div class="search-result-title">${this.highlightText(post.title || '无标题', query)}</div>
                        <div class="search-result-desc">${this.highlightText(post.description || '无描述', query)}</div>
                    </div>
                `).join('');
                
                // 为搜索结果项添加点击事件
                this.searchResults.querySelectorAll('.search-result-item').forEach(item => {
                    item.addEventListener('click', (e) => {
                        const postId = e.currentTarget.dataset.postId;
                        if (postId) {
                            this.openPostDetail(parseInt(postId));
                        }
                    });
                });
            }
            
            this.showResults();
            
        } catch (error) {
            console.error('显示搜索下拉框失败:', error);
            this.searchResults.innerHTML = '<div class="search-result-item">搜索出错，请重试</div>';
            this.showResults();
        }
    }

    // 高亮显示搜索关键词
    highlightText(text, query) {
        if (!text || !query) return text;
        
        const regex = new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
        return text.replace(regex, '<strong style="color: #1877f2; background-color: #e3f2fd;">$1</strong>');
    }

    // 显示搜索结果
    showResults() {
        if (this.searchResults) {
            this.searchResults.style.display = 'block';
        }
    }

    // 隐藏搜索结果
    hideResults() {
        if (this.searchResults) {
            this.searchResults.style.display = 'none';
        }
    }

    // 显示主页面搜索结果
    displayMainSearchResults(results, query) {
        const postsContainer = document.querySelector('.posts-container');
        if (!postsContainer) return;

        if (results.length === 0) {
            postsContainer.innerHTML = `
                <div class="search-message">
                    <i class="fas fa-search" style="font-size: 3rem; color: #65676b; margin-bottom: 1rem;"></i>
                    <h3>未找到 "${query}" 的相关结果</h3>
                    <p>尝试使用不同的关键词或<a href="#" onclick="location.reload()">浏览所有帖子</a></p>
                </div>
            `;
        } else {
            // 添加搜索结果头部
            const searchHeader = `
                <div class="search-header">
                    <h3>搜索 "${query}" 的结果 (${results.length} 条)</h3>
                    <button onclick="location.reload()" class="btn-secondary">查看所有帖子</button>
                </div>
            `;
            
            // 使用main.js中的createPostElement函数来创建帖子元素
            let postsHtml = searchHeader;
            results.forEach(post => {
                if (typeof window.createPostElement === 'function') {
                    const postElement = window.createPostElement(post);
                    postsHtml += postElement.outerHTML;
                } else {
                    // 备用方案：简单的帖子卡片
                    postsHtml += this.createSimplePostCard(post, query);
                }
            });
            
            postsContainer.innerHTML = postsHtml;
            
            // 重新绑定事件（如果需要）
            this.rebindPostEvents();
        }
    }

    // 显示主页面消息
    showMainSearchResults(message) {
        const postsContainer = document.querySelector('.posts-container');
        if (postsContainer) {
            postsContainer.innerHTML = `
                <div class="search-message" style="
                    grid-column: 1/-1; 
                    text-align: center; 
                    padding: 3rem; 
                    color: #65676b;
                ">
                    ${message}
                </div>
            `;
        }
    }

    // 创建简单的帖子卡片（备用方案）
    createSimplePostCard(post, query) {
        const userName = post.user ? post.user.username : '匿名用户';
        const userAvatar = post.user && post.user.avatarUrl ? post.user.avatarUrl : '/images/avatars/default.jpg';
        const imageUrl = post.imageUrl || 'https://via.placeholder.com/300x200';
        
        return `
            <article class="card" data-post-id="${post.postId}">
                <div class="card-image">
                    <img src="${imageUrl}" alt="${post.title || '无标题'}" loading="lazy">
                </div>
                <div class="card-content">
                    <div class="card-user">
                        <img src="${userAvatar}" alt="${userName}" class="user-avatar">
                        <span class="user-name">${userName}</span>
                    </div>
                    <h2>${this.highlightText(post.title || '无标题', query)}</h2>
                    <p>${this.highlightText(post.description || '暂无描述', query)}</p>
                    <div class="card-stats">
                        <div class="stat-item">
                            <i class="fas fa-heart heart-icon" data-post-id="${post.postId}"></i>
                            <span class="likes-count">${post.likes || 0}</span>
                        </div>
                        <div class="stat-item">
                            <i class="fas fa-comment"></i>
                            <span>${post.commentsCount || 0}</span>
                        </div>
                    </div>
                </div>
            </article>
        `;
    }

    // 重新绑定帖子事件
    rebindPostEvents() {
        // 重新绑定帖子点击事件
        document.querySelectorAll('.card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (!e.target.closest('.heart-icon')) {
                    const postId = parseInt(card.dataset.postId);
                    this.openPostDetail(postId);
                }
            });
        });

        // 重新绑定点赞事件
        document.querySelectorAll('.heart-icon').forEach(heartIcon => {
            heartIcon.addEventListener('click', (e) => {
                e.stopPropagation();
                // 这里可以调用main.js中的toggleLike函数
                if (typeof window.toggleLike === 'function') {
                    const postId = parseInt(e.target.dataset.postId);
                    // 需要获取对应的post对象，这里简化处理
                    const post = { postId: postId, likes: parseInt(e.target.nextElementSibling.textContent) };
                    window.toggleLike(e.target, post);
                }
            });
        });
    }

    // 打开帖子详情
    async openPostDetail(postId) {
        try {
            const response = await fetch(`/api/posts/${postId}`, {
                credentials: 'include'
            });
            const result = await response.json();
            
            if (result.code === 200) {
                // 调用main.js中的showPostDetails函数
                if (typeof window.showPostDetails === 'function') {
                    window.showPostDetails(result.data);
                }
            }
        } catch (error) {
            console.error('获取帖子详情失败:', error);
        }
    }

    // 清除搜索
    clearSearch() {
        if (this.searchInput) {
            this.searchInput.value = '';
        }
        this.hideResults();
        // 重新加载所有帖子
        if (typeof window.loadPosts === 'function') {
            window.loadPosts();
        }
    }
}

// 添加搜索结果样式
const searchStyles = `
<style>
.search-message {
    grid-column: 1/-1;
    text-align: center;
    padding: 3rem;
    color: #65676b;
}

.search-message h3 {
    margin-bottom: 0.5rem;
    color: #1c1e21;
}

.search-message a {
    color: #1877f2;
    text-decoration: none;
}

.search-message a:hover {
    text-decoration: underline;
}

.search-header {
    grid-column: 1/-1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 0;
    border-bottom: 2px solid #f0f2f5;
    margin-bottom: 1rem;
}

.search-header h3 {
    color: #1c1e21;
    margin: 0;
}

.search-header .btn-secondary {
    padding: 0.5rem 1rem;
    background-color: #f0f2f5;
    color: #1c1e21;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9rem;
    transition: background-color 0.2s;
}

.search-header .btn-secondary:hover {
    background-color: #e4e6eb;
}
</style>
`;

// 添加样式到head
document.head.insertAdjacentHTML('beforeend', searchStyles);

// 初始化搜索管理器
document.addEventListener('DOMContentLoaded', () => {
    window.searchManager = new SearchManager();
});

// 导出供其他模块使用
window.SearchManager = SearchManager; 